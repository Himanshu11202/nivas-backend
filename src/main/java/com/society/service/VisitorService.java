package com.society.service;

import com.society.entity.Notification;
import com.society.entity.User;
import com.society.entity.Visitor;
import com.society.repository.NotificationRepository;
import com.society.repository.UserRepository;
import com.society.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VisitorService {

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    // Visitor Management
    public Visitor createVisitor(Visitor visitor) {
        visitor.setCreatedAt(LocalDateTime.now());
        visitor.setStatus(Visitor.VisitorStatus.PENDING);
        
        Visitor savedVisitor = visitorRepository.save(visitor);
        
        // Send notification to resident
        sendVisitorNotification(savedVisitor, "requesting entry");
        
        return savedVisitor;
    }

    public Visitor updateVisitor(Long id, Visitor visitorDetails) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visitor not found"));
        
        visitor.setVisitorName(visitorDetails.getVisitorName());
        visitor.setVisitorPhone(visitorDetails.getVisitorPhone());
        visitor.setFlatNumber(visitorDetails.getFlatNumber());
        visitor.setPurpose(visitorDetails.getPurpose());
        visitor.setVehicleNumber(visitorDetails.getVehicleNumber());
        visitor.setVisitorPhoto(visitorDetails.getVisitorPhoto());
        
        return visitorRepository.save(visitor);
    }

    public void deleteVisitor(Long id) {
        visitorRepository.deleteById(id);
    }

    public Visitor getVisitorById(Long id) {
        return visitorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visitor not found"));
    }

    public List<Visitor> getAllVisitors() {
        return visitorRepository.findAll();
    }

    public List<Visitor> getVisitorsByFlatNumber(String flatNumber) {
        return visitorRepository.findByFlatNumberOrderByEntryTimeDesc(flatNumber);
    }

    public List<Visitor> getVisitorsByStatus(Visitor.VisitorStatus status) {
        return visitorRepository.findByStatus(status);
    }

    // Visitor Approval
    @Transactional
    public Visitor approveVisitor(Long id) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visitor not found"));
        
        visitor.setStatus(Visitor.VisitorStatus.APPROVED);
        visitor.setEntryTime(LocalDateTime.now());
        
        Visitor savedVisitor = visitorRepository.save(visitor);
        
        // Send notification to resident
        sendVisitorNotification(visitor, "approved");
        
        return savedVisitor;
    }

    @Transactional
    public Visitor rejectVisitor(Long id) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visitor not found"));
        
        visitor.setStatus(Visitor.VisitorStatus.REJECTED);
        
        Visitor savedVisitor = visitorRepository.save(visitor);
        
        // Send notification to resident
        sendVisitorNotification(visitor, "rejected");
        
        return savedVisitor;
    }

    // Mark visitor exit
    public Visitor markVisitorExit(Long id) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Visitor not found"));
        
        visitor.setStatus(Visitor.VisitorStatus.EXITED);
        visitor.setExitTime(LocalDateTime.now());
        
        return visitorRepository.save(visitor);
    }

    // Send visitor notification to resident
    private void sendVisitorNotification(Visitor visitor, String action) {
        List<User> residents = userRepository.findByFlatNumber(visitor.getFlatNumber());
        
        if (!residents.isEmpty()) {
            User resident = residents.get(0);
            Notification notification = new Notification();
            notification.setUserId(resident.getId());
            
            if (action.equals("requesting entry")) {
                notification.setMessage("Visitor " + visitor.getVisitorName() + " is requesting entry to your flat.");
                notification.setType(Notification.NotificationType.VISITOR_REQUEST);
            } else {
                notification.setMessage("Your visitor request for " + visitor.getVisitorName() + " has been " + action + ".");
                notification.setType(Notification.NotificationType.VISITOR_APPROVAL);
            }
            
            notification.setIsRead(false);
            
            notificationRepository.save(notification);
        }
    }

    // Dashboard Statistics
    public long getTotalVisitors() {
        return visitorRepository.count();
    }

    public long getPendingVisitors() {
        return visitorRepository.countByStatus(Visitor.VisitorStatus.PENDING);
    }

    public long getTodayVisitors() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        return visitorRepository.countVisitorsBetween(startOfDay, endOfDay);
    }

    public List<Visitor> getTodayVisitorsList() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        return visitorRepository.findByEntryTimeBetween(startOfDay, endOfDay);
    }
}
