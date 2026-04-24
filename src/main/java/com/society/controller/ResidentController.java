package com.society.controller;

import com.society.dto.ComplaintRequest;
import com.society.entity.*;
import com.society.service.ResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resident")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('RESIDENT')")
public class ResidentController {

    @Autowired
    private ResidentService residentService;

    // Maintenance
    @GetMapping("/maintenance")
    public ResponseEntity<List<Maintenance>> getMyMaintenance(@RequestParam Long userId) {
        List<Maintenance> bills = residentService.getMyMaintenance(userId);
        return ResponseEntity.ok(bills);
    }

    @PostMapping("/maintenance/{id}/pay")
    public ResponseEntity<Maintenance> payMaintenance(@PathVariable Long id) {
        Maintenance maintenance = residentService.payMaintenance(id);
        return ResponseEntity.ok(maintenance);
    }

    // Complaints
    @PostMapping("/complaints")
    public ResponseEntity<Complaint> createComplaint(@RequestBody ComplaintRequest complaintRequest, @RequestParam Long userId) {
        Complaint complaint = residentService.createComplaint(
                complaintRequest.getTitle(),
                complaintRequest.getDescription(),
                complaintRequest.getCategory(),
                userId
        );
        return ResponseEntity.ok(complaint);
    }

    @GetMapping("/complaints")
    public ResponseEntity<List<Complaint>> getMyComplaints(@RequestParam Long userId) {
        List<Complaint> complaints = residentService.getMyComplaints(userId);
        return ResponseEntity.ok(complaints);
    }

    // Visitors
    @PostMapping("/visitors/{id}/approve")
    public ResponseEntity<Void> approveVisitor(@PathVariable Long id) {
        residentService.approveVisitor(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/visitors/{id}/reject")
    public ResponseEntity<Void> rejectVisitor(@PathVariable Long id) {
        residentService.rejectVisitor(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/visitors")
    public ResponseEntity<List<Visitor>> getMyVisitors(@RequestParam String flatNumber) {
        List<Visitor> visitors = residentService.getMyVisitors(flatNumber);
        return ResponseEntity.ok(visitors);
    }

    // Notifications
    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getMyNotifications(@RequestParam Long userId) {
        List<Notification> notifications = residentService.getMyNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/notifications/{id}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id) {
        residentService.markNotificationAsRead(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/notifications/unread-count")
    public ResponseEntity<Long> getUnreadNotificationCount(@RequestParam Long userId) {
        Long count = residentService.getUnreadNotificationCount(userId);
        return ResponseEntity.ok(count);
    }
}
