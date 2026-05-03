package com.society.controller;

import com.society.entity.Complaint;
import com.society.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/complaints")
@PreAuthorize("hasRole('ADMIN')")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    // Create Complaint
    @PostMapping
    public ResponseEntity<Complaint> createComplaint(@Valid @RequestBody Complaint complaint) {
        try {
            Complaint createdComplaint = complaintService.createComplaint(complaint);
            return ResponseEntity.ok(createdComplaint);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update Complaint
    @PutMapping("/{id}")
    public ResponseEntity<Complaint> updateComplaint(@PathVariable Long id, @Valid @RequestBody Complaint complaint) {
        try {
            Complaint updatedComplaint = complaintService.updateComplaint(id, complaint);
            return ResponseEntity.ok(updatedComplaint);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update Complaint Status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Complaint> updateComplaintStatus(@PathVariable Long id, @RequestBody Map<String, String> statusMap) {
        try {
            Complaint.ComplaintStatus status = Complaint.ComplaintStatus.valueOf(statusMap.get("status"));
            Complaint updatedComplaint = complaintService.updateComplaintStatus(id, status);
            return ResponseEntity.ok(updatedComplaint);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Delete Complaint
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplaint(@PathVariable Long id) {
        try {
            complaintService.deleteComplaint(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get Complaint by ID
    @GetMapping("/{id}")
    public ResponseEntity<Complaint> getComplaintById(@PathVariable Long id) {
        try {
            Complaint complaint = complaintService.getComplaintById(id);
            return ResponseEntity.ok(complaint);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get All Complaints
    @GetMapping
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        List<Complaint> complaints = complaintService.getAllComplaints();
        return ResponseEntity.ok(complaints);
    }

    // Get Complaints by Status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Complaint>> getComplaintsByStatus(@PathVariable Complaint.ComplaintStatus status) {
        List<Complaint> complaints = complaintService.getComplaintsByStatus(status);
        return ResponseEntity.ok(complaints);
    }

    // Get Complaint Statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getComplaintStats() {
        Map<String, Object> stats = Map.of(
                "totalComplaints", complaintService.getTotalComplaints(),
                "pendingComplaints", complaintService.getPendingComplaints(),
                "inProgressComplaints", complaintService.getInProgressComplaints(),
                "resolvedComplaints", complaintService.getResolvedComplaints()
        );
        return ResponseEntity.ok(stats);
    }
}
