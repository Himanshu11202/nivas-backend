package com.society.controller;

import com.society.entity.Notice;
import com.society.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/notices")
@CrossOrigin(origins = "http://localhost:3000")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    // Create Notice
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Notice> createNotice(@Valid @RequestBody Notice notice) {
        try {
            Notice createdNotice = noticeService.createNotice(notice);
            return ResponseEntity.ok(createdNotice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update Notice
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Notice> updateNotice(@PathVariable Long id, @Valid @RequestBody Notice notice) {
        try {
            Notice updatedNotice = noticeService.updateNotice(id, notice);
            return ResponseEntity.ok(updatedNotice);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Delete Notice
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long id) {
        try {
            noticeService.deleteNotice(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get Notice by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Notice> getNoticeById(@PathVariable Long id) {
        try {
            Notice notice = noticeService.getNoticeById(id);
            return ResponseEntity.ok(notice);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get All Notices (Admin)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Notice>> getAllNotices() {
        List<Notice> notices = noticeService.getAllNotices();
        return ResponseEntity.ok(notices);
    }

    // Get Recent Notices
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Notice>> getRecentNotices() {
        List<Notice> notices = noticeService.getRecentNotices();
        return ResponseEntity.ok(notices);
    }

    // Get Notice Statistics
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getNoticeStats() {
        Map<String, Object> stats = Map.of(
                "totalNotices", noticeService.getTotalNotices(),
                "recentNotices", noticeService.getRecentNoticesCount()
        );
        return ResponseEntity.ok(stats);
    }

    // Mark notice as read (for any authenticated user)
    @PostMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> markNoticeAsRead(@PathVariable Long id, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuthentication(authentication);
            noticeService.markNoticeAsRead(id, userId);
            return ResponseEntity.ok(Map.of("message", "Notice marked as read"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get notice stats with read/unread counts
    @GetMapping("/{id}/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getNoticeReadStats(@PathVariable Long id) {
        try {
            Map<String, Object> stats = noticeService.getNoticeReadStats(id);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get list of users who have NOT read the notice
    @GetMapping("/{id}/unread-users")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getUnreadUsers(@PathVariable Long id) {
        try {
            List<Map<String, Object>> unreadUsers = noticeService.getUnreadUsers(id);
            return ResponseEntity.ok(unreadUsers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUBLIC endpoint for residents to get all notices with read status
    @GetMapping("/public/all")
    public ResponseEntity<List<Map<String, Object>>> getAllNoticesForResidents(Authentication authentication) {
        try {
            Long userId = null;
            if (authentication != null && authentication.isAuthenticated()) {
                userId = getUserIdFromAuthentication(authentication);
            }
            List<Map<String, Object>> notices = noticeService.getAllNoticesWithReadStatus(userId);
            return ResponseEntity.ok(notices);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Helper method to extract user ID from authentication
    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication.getPrincipal() instanceof com.society.entity.User) {
            return ((com.society.entity.User) authentication.getPrincipal()).getId();
        }
        return null;
    }
}
