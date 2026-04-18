# Notification System — Implementation Documentation

> Complete reference for the DB + WebSocket notification system added to the Personal Finance Tracker API.

---

## Architecture Overview

```
Any Event (friend request, accept, reject)
          │
          ▼
    FriendService
          │
          ├──► Save FriendRequest to DB
          │
          └──► NotificationService.create()
                        │
                        ├──► Save Notification to DB   ← persistent history
                        │
                        └──► WebSocket push (STOMP)    ← real-time delivery
                                    │
                                    ▼
                         /user/{username}/queue/notifications
                                    │
                                    ▼
                            React bell icon updates instantly
```

---

## Files Created

### 1. `entity/NotificationType.java`
**What it is:** Enum describing WHAT event happened.

```
FRIEND_REQUEST       → Someone sent you a friend request
FRIEND_ACCEPTED      → Someone accepted your friend request
FRIEND_REJECTED      → Someone rejected your friend request
LINKED_TXN_REQUEST   → Future: shared transaction request
LINKED_TXN_ACCEPTED  → Future: transaction accepted
LINKED_TXN_REJECTED  → Future: transaction rejected
```

> [!NOTE]
> `READ`/`UNREAD` are **not** types — those are states. `isRead` is a boolean field on the `Notification` entity. The type field answers "what happened?", not "has it been seen?".

---

### 2. `entity/Notification.java`
**What it is:** JPA entity — each row = one notification in the DB.

| Field | Type | Description |
|:---|:---|:---|
| `id` | Long | Primary key |
| `recipient` | User (FK) | WHO receives the notification |
| `type` | NotificationType | WHAT event happened |
| `message` | String | Human-readable text shown in UI |
| `referenceId` | Long | ID of the FriendRequest/LinkedTransaction |
| `isRead` | Boolean | Has the user seen it? |
| `createdAt` | Date | Timestamp |

**DB table:** `notifications`
**Relationship:** Many notifications → One recipient user (`@ManyToOne`, LAZY fetch)

---

### 3. `repositry/NotificationRepository.java`
**What it is:** Spring Data JPA repository — 3 derived query methods.

| Method | Purpose |
|:---|:---|
| `findByRecipientIdOrderByCreatedAtDesc` | All notifications, newest first |
| `findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc` | Unread only (bell dropdown) |
| `countByRecipientIdAndIsReadFalse` | Unread count (bell badge number) |

These are **derived queries** — Spring reads the method name and generates SQL automatically. No `@Query` annotation needed.

---

### 4. `DTO/NotificationResponseDto.java`
**What it is:** Safe response object sent to the frontend. Never exposes the full User entity (no passwords).

| Field | Type |
|:---|:---|
| `id` | Long |
| `type` | NotificationType |
| `message` | String |
| `referenceId` | Long |
| `isRead` | Boolean |
| `createdAt` | Date |

---

### 5. `services/NotificationService.java`
**What it is:** Core service — creates notifications and serves them to the frontend.

| Method | Called By | Description |
|:---|:---|:---|
| `create(recipient, type, message, referenceId)` | FriendService | Saves to DB + pushes via WebSocket |
| `getAll()` | Controller | All notifications for current user |
| `getUnread()` | Controller | Unread only |
| `getUnreadCount()` | Controller | Number for bell badge |
| `markRead(id)` | Controller | Marks one as read (with auth check) |
| `markAllRead()` | Controller | Clears all unread |

**Key design — `create()` does TWO things in one call:**

```java
// 1. Save to DB (history — survives page refresh, works without WebSocket)
notificationRepository.save(notification);

// 2. Push via WebSocket (real-time — instant bell update)
messagingTemplate.convertAndSendToUser(
    recipient.getUserName(),
    "/queue/notifications",
    toDTO(notification)
);
```

---

### 6. `controllers/NotificationController.java`
**What it is:** REST controller exposing 5 endpoints.

| Method | URL | Description |
|:---|:---|:---|
| `GET` | `/notifications` | All notifications |
| `GET` | `/notifications/unread` | Unread only |
| `GET` | `/notifications/count` | Returns `{"count": 3}` |
| `PUT` | `/notifications/{id}/read` | Mark one as read |
| `PUT` | `/notifications/readAll` | Mark all as read |

---

### 7. `config/WebSocketConfig.java`
**What it is:** Configures the STOMP WebSocket broker.

```
Broker channels:
  /queue  → private (one-to-one) — used for notifications
  /topic  → broadcast (one-to-many) — for future group features

App destination prefix:  /app
User destination prefix: /user
Connection endpoint:     /ws  (with SockJS fallback)
```

**How routing works:**
```
Backend calls:
  messagingTemplate.convertAndSendToUser("alice", "/queue/notifications", dto)

Spring routes this to:
  /user/alice/queue/notifications

Alice's React subscribes to:
  /user/queue/notifications   ← Spring maps this to Alice automatically via session
```

---

## Files Modified

### 8. `pom.xml`
**Change:** Added WebSocket dependency.

```xml
<!-- WebSocket (STOMP) for real-time notifications -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

---

### 9. `config/springSecurityConfig.java`
**Change:** Added `/ws/**` to `permitAll` so the WebSocket handshake isn't blocked by the JWT filter.

```java
.requestMatchers("/ws/**").permitAll()
```

> [!IMPORTANT]
> The initial WebSocket HTTP upgrade request (handshake) happens before STOMP connection headers (JWT) are attached. Without this, Spring Security blocks the handshake with 403 before the connection is even established.

---

### 10. `services/FriendService.java`
**Change:** Injected `NotificationService` and added notification calls after each friend action.

| Action | Who gets notified | Type |
|:---|:---|:---|
| `sendFriendRequest()` | The **receiver** | `FRIEND_REQUEST` |
| `acceptRequest()` | The original **sender** | `FRIEND_ACCEPTED` |
| `rejectRequest()` | The original **sender** | `FRIEND_REJECTED` |

---

## Complete Event Flow

```
Bob sends friend request to Alice
  │
  ├─ DB:        FriendRequest row created (status = PENDING)
  ├─ DB:        Notification row created for Alice
  │               { type: FRIEND_REQUEST, message: "Bob sent you a friend request", isRead: false }
  └─ WebSocket: Pushed to /user/alice/queue/notifications
                  → Alice's React bell badge: 0 → 1
                  → Toast: "Bob sent you a friend request"

Alice accepts
  │
  ├─ DB:        FriendRequest status updated (PENDING → ACCEPTED)
  ├─ DB:        Notification row created for Bob
  │               { type: FRIEND_ACCEPTED, message: "Alice accepted your friend request" }
  └─ WebSocket: Pushed to /user/bob/queue/notifications
                  → Bob's bell badge: 0 → 1
                  → Toast: "Alice accepted your friend request"
```

---

## React Integration (Frontend)

### Install libraries
```bash
npm install @stomp/stompjs sockjs-client
```

### Custom Hook — `useWebSocket.js`
```javascript
import { useEffect } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export function useWebSocket(token, onNotification) {
  useEffect(() => {
    if (!token) return;

    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8004/ws'),
      connectHeaders: { Authorization: `Bearer ${token}` },
      onConnect: () => {
        client.subscribe('/user/queue/notifications', (msg) => {
          const notification = JSON.parse(msg.body);
          onNotification(notification);
        });
      },
      reconnectDelay: 5000
    });

    client.activate();
    return () => client.deactivate();
  }, [token]);
}
```

### Usage in `App.jsx`
```javascript
const [unreadCount, setUnreadCount] = useState(0);

useWebSocket(localStorage.getItem('token'), (notification) => {
  setUnreadCount(prev => prev + 1);
  toast.info(notification.message);
});

// On first load: restore badge count from DB (WS only delivers new events)
useEffect(() => {
  fetch('/notifications/count', { headers: { Authorization: `Bearer ${token}` }})
    .then(r => r.json())
    .then(data => setUnreadCount(data.count));
}, []);
```

---

## Notification Payload Example

```json
{
  "id": 42,
  "type": "FRIEND_REQUEST",
  "message": "Kavya sent you a friend request",
  "referenceId": 15,
  "isRead": false,
  "createdAt": "2024-04-18T00:30:00.000Z"
}
```

---

## Wiring Future Features

When Linked Transactions are built, call `notificationService.create()` the same way — **zero changes needed** to `NotificationService`, `NotificationController`, or `WebSocketConfig`.

```java
// In LinkedTxnService, after creating a request:
notificationService.create(
    receiver,
    NotificationType.LINKED_TXN_REQUEST,
    sender.getName() + " wants to split a transaction with you",
    linkedTxnId
);

// On accept:
notificationService.create(
    originalRequester,
    NotificationType.LINKED_TXN_ACCEPTED,
    receiver.getName() + " accepted the shared transaction",
    linkedTxnId
);
```

---

## API Quick Reference

### REST Endpoints (require JWT)
| Method | URL | Returns |
|:---|:---|:---|
| GET | `/notifications` | All notifications array |
| GET | `/notifications/unread` | Unread notifications array |
| GET | `/notifications/count` | `{ "count": 3 }` |
| PUT | `/notifications/{id}/read` | `"Marked as read"` |
| PUT | `/notifications/readAll` | `"All notifications marked as read"` |

### WebSocket (STOMP)
| Action | Value |
|:---|:---|
| Connect URL | `ws://localhost:8004/ws` (SockJS) |
| Subscribe channel | `/user/queue/notifications` |
| Data format | `NotificationResponseDto` as JSON |
