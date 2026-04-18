package com.personal.Expense_Tracker.controllers;

import com.personal.Expense_Tracker.DTO.NotificationResponseDto;
import com.personal.Expense_Tracker.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // GET /notifications — all notifications (read + unread)
    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getAll() {
        return ResponseEntity.ok(notificationService.getAll());
    }

    // GET /notifications/unread — unread only (bell dropdown)
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDto>> getUnread() {
        return ResponseEntity.ok(notificationService.getUnread());
    }

    // GET /notifications/count — unread count for badge number
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getCount() {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount()));
    }

    // PUT /notifications/{id}/read — mark one as read (on click)
    @PutMapping("/{id}/read")
    public ResponseEntity<String> markRead(@PathVariable Long id) {
        notificationService.markRead(id);
        return ResponseEntity.ok("Marked as read");
    }

    // PUT /notifications/readAll — mark all as read (clear bell)
    @PutMapping("/readAll")
    public ResponseEntity<String> markAllRead() {
        notificationService.markAllRead();
        return ResponseEntity.ok("All notifications marked as read");
    }
}
