package com.society.service;

import com.society.entity.Complaint;
import com.society.entity.Notification;
import com.society.entity.User;
import com.society.repository.ComplaintRepository;
import com.society.repository.NotificationRepository;
import com.society.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    // Create complaint for a specific user (Resident)
    public Complaint createComplaintForUser(Complaint complaint, Long userId) {
        complaint.setUserId(userId);
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setStatus(Complaint.ComplaintStatus.PENDING);
        
        return complaintRepository.save(complaint);
    }

    // Complaint Management
    public Complaint createComplaint(Complaint complaint) {
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setStatus(Complaint.ComplaintStatus.PENDING);
        
        return complaintRepository.save(complaint);
    }

    public Complaint updateComplaint(Long id, Complaint complaintDetails) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        
        complaint.setTitle(complaintDetails.getTitle());
        complaint.setDescription(complaintDetails.getDescription());
        complaint.setCategory(complaintDetails.getCategory());
        
        return complaintRepository.save(complaint);
    }

    public Complaint updateComplaintStatus(Long id, Complaint.ComplaintStatus status) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        
        Complaint.ComplaintStatus oldStatus = complaint.getStatus();
        complaint.setStatus(status);
        
        if (status == Complaint.ComplaintStatus.RESOLVED && oldStatus != Complaint.ComplaintStatus.RESOLVED) {
            complaint.setResolvedAt(LocalDateTime.now());
            sendComplaintResolvedNotification(complaint);
        }
        
        return complaintRepository.save(complaint);
    }

    public void deleteComplaint(Long id) {
        complaintRepository.deleteById(id);
    }

    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllOrderByCreatedAtDesc();
    }

    public List<Complaint> getComplaintsByUserId(Long userId) {
        return complaintRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Complaint> getComplaintsByStatus(Complaint.ComplaintStatus status) {
        return complaintRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    // Send notification when complaint is resolved
    private void sendComplaintResolvedNotification(Complaint complaint) {
        Notification notification = new Notification();
        notification.setUserId(complaint.getUserId());
        notification.setMessage("Your complaint '" + complaint.getTitle() + "' has been resolved.");
        notification.setType(Notification.NotificationType.COMPLAINT_UPDATE);
        notification.setIsRead(false);
        
        notificationRepository.save(notification);
    }

    // Dashboard Statistics
    public long getTotalComplaints() {
        return complaintRepository.count();
    }

    public long getPendingComplaints() {
        return complaintRepository.countByStatus(Complaint.ComplaintStatus.PENDING);
    }

    public long getInProgressComplaints() {
        return complaintRepository.countByStatus(Complaint.ComplaintStatus.IN_PROGRESS);
    }

    public long getResolvedComplaints() {
        return complaintRepository.countByStatus(Complaint.ComplaintStatus.RESOLVED);
    }
}
