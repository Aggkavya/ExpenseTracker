package com.personal.Expense_Tracker.services;

import com.personal.Expense_Tracker.DTO.NotificationResponseDto;
import com.personal.Expense_Tracker.entity.Notification;
import com.personal.Expense_Tracker.entity.NotificationType;
import com.personal.Expense_Tracker.entity.User;
import com.personal.Expense_Tracker.repositry.NotificationRepository;
import com.personal.Expense_Tracker.repositry.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // WebSocket sender — pushes to /user/{username}/queue/notifications
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ── Helper ────────────────────────────────────────────────────────────────

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserName(username);
    }

    private NotificationResponseDto toDTO(Notification n) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setId(n.getId());
        dto.setType(n.getType());
        dto.setMessage(n.getMessage());
        dto.setReferenceId(n.getReferenceId());
        dto.setIsRead(n.getIsRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }

    // ── Called by FriendService on every event ────────────────────────────────
    // This is the ONLY entry point for creating notifications

    public void create(User recipient, NotificationType type,
                       String message, Long referenceId) {
        // 1. Save to DB (persistent history)
        Notification notification = Notification.builder()
                .recipient(recipient)
                .type(type)
                .message(message)
                .referenceId(referenceId)
                .isRead(false)
                .createdAt(new Date())
                .build();
        notificationRepository.save(notification);

        // 2. Push in real-time via WebSocket to the recipient's private channel
        // Frontend subscribes to: /user/queue/notifications
        // Spring routes this to: /user/{username}/queue/notifications
        messagingTemplate.convertAndSendToUser(
                recipient.getUserName(),
                "/queue/notifications",
                toDTO(notification)
        );

        log.info("Notification [{}] created + pushed to: {}", type, recipient.getUserName());
    }

    // ── GET all notifications ─────────────────────────────────────────────────

    public List<NotificationResponseDto> getAll() {
        User me = getCurrentUser();
        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(me.getId())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── GET unread only ───────────────────────────────────────────────────────

    public List<NotificationResponseDto> getUnread() {
        User me = getCurrentUser();
        return notificationRepository
                .findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(me.getId())
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── GET unread count (bell badge number) ──────────────────────────────────

    public Long getUnreadCount() {
        User me = getCurrentUser();
        return notificationRepository.countByRecipientIdAndIsReadFalse(me.getId());
    }

    // ── Mark one as read ──────────────────────────────────────────────────────

    public void markRead(Long id) {
        User me = getCurrentUser();
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (!n.getRecipient().getId().equals(me.getId())) {
            throw new RuntimeException("Not authorized to mark this notification");
        }
        n.setIsRead(true);
        notificationRepository.save(n);
    }

    // ── Mark all as read ──────────────────────────────────────────────────────

    public void markAllRead() {
        User me = getCurrentUser();
        List<Notification> unread = notificationRepository
                .findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(me.getId());
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
        log.info("Marked {} notifications as read for {}", unread.size(), me.getUserName());
    }
}
