package com.society.service;

import com.society.entity.*;
import com.society.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResidentService {

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // Maintenance Management
    public List<Maintenance> getMyMaintenance(Long userId) {
        return maintenanceRepository.findByUserIdOrderByDueDateDesc(userId);
    }

    public Maintenance payMaintenance(Long maintenanceId) {
        Maintenance maintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new RuntimeException("Maintenance not found"));
        maintenance.setStatus(Maintenance.PaymentStatus.PAID);
        maintenance.setPaidAt(LocalDateTime.now());
        return maintenanceRepository.save(maintenance);
    }

    // Complaint Management
    public Complaint createComplaint(String title, String description, Complaint.ComplaintCategory category, Long userId) {
        Complaint complaint = new Complaint();
        complaint.setTitle(title);
        complaint.setDescription(description);
        complaint.setCategory(category);
        complaint.setUserId(userId);
        return complaintRepository.save(complaint);
    }

    public List<Complaint> getMyComplaints(Long userId) {
        return complaintRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Visitor Management
    public void approveVisitor(Long visitorId) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new RuntimeException("Visitor not found"));
        
        visitor.setStatus(Visitor.VisitorStatus.APPROVED);
        visitorRepository.save(visitor);
        
        // Create notification for guard
        List<User> guards = userRepository.findByRole(User.Role.GUARD);
        for (User guard : guards) {
            Notification notification = new Notification();
            notification.setUserId(guard.getId());
            notification.setMessage("Visitor " + visitor.getVisitorName() + " is waiting for your approval. Flat Number: " + visitor.getFlatNumber());
            notification.setType(Notification.NotificationType.VISITOR_APPROVAL);
            notificationRepository.save(notification);
        }
    }

    public void rejectVisitor(Long visitorId) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new RuntimeException("Visitor not found"));
        
        visitor.setStatus(Visitor.VisitorStatus.REJECTED);
        visitorRepository.save(visitor);
    }

    public List<Visitor> getMyVisitors(String flatNumber) {
        return visitorRepository.findByFlatNumberOrderByEntryTimeDesc(flatNumber);
    }

    // Notifications
    public List<Notification> getMyNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public Long getUnreadNotificationCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
}
