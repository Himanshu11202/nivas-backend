package com.society.service;

import com.society.entity.*;
import com.society.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoticeReadRepository noticeReadRepository;

    // Notice Management
    public Notice createNotice(Notice notice) {
        Notice savedNotice = noticeRepository.save(notice);
        
        // Send notification to all residents only
        sendNoticeToAllResidents(savedNotice);
        
        return savedNotice;
    }

    public Notice updateNotice(@jakarta.validation.constraints.NotNull Long id, Notice noticeDetails) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
        
        notice.setTitle(noticeDetails.getTitle());
        notice.setMessage(noticeDetails.getMessage());
        
        return noticeRepository.save(notice);
    }

    public void deleteNotice(@jakarta.validation.constraints.NotNull Long id) {
        noticeRepository.deleteById(id);
    }

    public Notice getNoticeById(@jakarta.validation.constraints.NotNull Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
    }

    public List<Notice> getAllNotices() {
        return noticeRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Notice> getRecentNotices() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        return noticeRepository.findRecentNotices();
    }

    // READ TRACKING METHODS

    // Mark notice as read by user
    @Transactional
    public void markNoticeAsRead(Long noticeId, Long userId) {
        // Check if already read
        Optional<NoticeRead> existingRead = noticeReadRepository.findByNoticeIdAndUserId(noticeId, userId);
        if (existingRead.isPresent()) {
            return; // Already marked as read
        }
        
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        NoticeRead noticeRead = new NoticeRead(notice, user);
        noticeRead.setIsRead(true);
        noticeRead.setReadAt(LocalDateTime.now());
        noticeReadRepository.save(noticeRead);
    }

    // Check if user has read notice
    public boolean hasUserReadNotice(Long noticeId, Long userId) {
        return noticeReadRepository.existsByNoticeIdAndUserId(noticeId, userId);
    }

    // Get all notices with read status for a user
    public List<Map<String, Object>> getAllNoticesWithReadStatus(Long userId) {
        List<Notice> notices = noticeRepository.findAllByOrderByCreatedAtDesc();
        
        return notices.stream().map(notice -> {
            Map<String, Object> noticeMap = new HashMap<>();
            noticeMap.put("id", notice.getId());
            noticeMap.put("title", notice.getTitle());
            noticeMap.put("message", notice.getMessage());
            noticeMap.put("createdAt", notice.getCreatedAt());
            
            if (userId != null) {
                boolean isRead = noticeReadRepository.existsByNoticeIdAndUserId(notice.getId(), userId);
                noticeMap.put("isRead", isRead);
            } else {
                noticeMap.put("isRead", false);
            }
            
            return noticeMap;
        }).collect(Collectors.toList());
    }

    // Get notice read stats for admin
    public Map<String, Object> getNoticeReadStats(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("Notice not found"));
        
        // Get total residents count
        long totalResidents = userRepository.countByRole(User.Role.RESIDENT);
        
        // Get read count
        long readCount = noticeReadRepository.countByNoticeId(noticeId);
        
        // Calculate unread count
        long unreadCount = totalResidents - readCount;
        if (unreadCount < 0) unreadCount = 0;
        
        // Calculate percentage
        double readPercentage = totalResidents > 0 ? (readCount * 100.0 / totalResidents) : 0;
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("noticeId", noticeId);
        stats.put("noticeTitle", notice.getTitle());
        stats.put("totalResidents", totalResidents);
        stats.put("readCount", readCount);
        stats.put("unreadCount", unreadCount);
        stats.put("readPercentage", Math.round(readPercentage * 100.0) / 100.0); // Round to 2 decimals
        
        return stats;
    }

    // Get list of users who have NOT read the notice
    public List<Map<String, Object>> getUnreadUsers(Long noticeId) {
        // Get all resident IDs
        List<User> allResidents = userRepository.findByRole(User.Role.RESIDENT);
        
        // Get user IDs who have read the notice
        List<Long> readUserIds = noticeReadRepository.findUserIdsByNoticeId(noticeId);
        
        // Filter users who haven't read
        return allResidents.stream()
                .filter(user -> !readUserIds.contains(user.getId()))
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("name", user.getName());
                    userMap.put("email", user.getEmail());
                    userMap.put("flatNumber", user.getFlatNumber());
                    return userMap;
                })
                .collect(Collectors.toList());
    }

    // Send notice to all residents
    private void sendNoticeToAllResidents(Notice notice) {
        List<User> residents = userRepository.findByRole(User.Role.RESIDENT);
        
        List<Notification> notifications = residents.stream()
                .map(resident -> {
                    Notification notification = new Notification();
                    notification.setUserId(resident.getId());
                    notification.setMessage("New notice: " + notice.getTitle());
                    notification.setType(Notification.NotificationType.NEW_NOTICE);
                    notification.setIsRead(false);
                    return notification;
                })
                .collect(Collectors.toList());
        
        notificationRepository.saveAll(notifications);
    }

    // Dashboard Statistics
    public long getTotalNotices() {
        return noticeRepository.count();
    }

    public long getRecentNoticesCount() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        return noticeRepository.countRecentNotices(oneWeekAgo);
    }
}
