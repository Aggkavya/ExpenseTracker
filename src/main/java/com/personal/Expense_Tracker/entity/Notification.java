package com.personal.Expense_Tracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // WHO receives this notification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    // WHAT event happened (FRIEND_REQUEST, FRIEND_ACCEPTED, etc.)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    // Human-readable text: "Kavya sent you a friend request"
    @Column(nullable = false)
    private String message;

    // The ID of the related entity (FriendRequest id, LinkedTransaction id, etc.)
    private Long referenceId;

    // Has the user seen this notification?
    @Column(nullable = false)
    private Boolean isRead = false;

    @Column(nullable = false)
    private Date createdAt;
}
