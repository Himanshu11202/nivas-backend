package com.society.controller;

import com.society.entity.Complaint;
import com.society.entity.User;
import com.society.service.ComplaintService;
import com.society.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
public class ResidentComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private UserRepository userRepository;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            User user = userRepository.findByEmail(email).orElse(null);
            return user != null ? user.getId() : null;
        }
        return null;
    }

    // Submit complaint (Resident only)
    @PostMapping
    @PreAuthorize("hasRole('RESIDENT')")
    public ResponseEntity<?> submitComplaint(@RequestBody Map<String, String> request) {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not authenticated"));
            }

            Complaint complaint = new Complaint();
            complaint.setTitle(request.get("title"));
            complaint.setDescription(request.get("description"));
            complaint.setCategory(Complaint.ComplaintCategory.valueOf(request.get("category")));
            complaint.setStatus(Complaint.ComplaintStatus.PENDING);

            Complaint created = complaintService.createComplaintForUser(complaint, userId);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get user's complaints (Resident only)
    @GetMapping("/my")
    @PreAuthorize("hasRole('RESIDENT')")
    public ResponseEntity<?> getMyComplaints() {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "User not authenticated"));
            }
            List<Complaint> complaints = complaintService.getComplaintsByUserId(userId);
            return ResponseEntity.ok(complaints);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
