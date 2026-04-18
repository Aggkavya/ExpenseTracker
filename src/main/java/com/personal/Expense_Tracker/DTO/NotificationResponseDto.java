package com.personal.Expense_Tracker.DTO;

import com.personal.Expense_Tracker.entity.NotificationType;
import lombok.Data;

import java.util.Date;

@Data
public class NotificationResponseDto {
    private Long id;
    private NotificationType type;        // FRIEND_REQUEST, FRIEND_ACCEPTED, etc.
    private String message;               // "Kavya sent you a friend request"
    private Long referenceId;             // FriendRequest id or LinkedTransaction id
    private Boolean isRead;
    private Date createdAt;
}
