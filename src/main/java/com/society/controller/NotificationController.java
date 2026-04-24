package com.society.controller;

import com.society.entity.Notification;
import com.society.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Get User Notifications
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('GUARD')")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    // Get Unread Count
    @GetMapping("/user/{userId}/unread-count")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('GUARD')")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long userId) {
        long unreadCount = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("count", unreadCount));
    }

    // Mark as Read
    @PatchMapping("/{notificationId}/read")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('GUARD')")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long notificationId) {
        try {
            Notification notification = notificationService.markAsRead(notificationId);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Mark All as Read
    @PatchMapping("/user/{userId}/read-all")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('GUARD')")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    // Delete Notification
    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('GUARD')")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        try {
            notificationService.deleteNotification(notificationId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
