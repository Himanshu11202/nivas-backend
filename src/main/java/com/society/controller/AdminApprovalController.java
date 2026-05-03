package com.society.controller;

import com.society.entity.User;
import com.society.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/society-admin")
@PreAuthorize("hasRole('SOCIETY_ADMIN')")
public class AdminApprovalController {

    @Autowired
    private UserRepository userRepository;

    // Get pending residents for the society admin's society
    @GetMapping("/pending-residents")
    public ResponseEntity<List<User>> getPendingResidents(Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        Long societyId = admin.getSocietyId();

        if (societyId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<User> pendingResidents = userRepository.findByRole(User.Role.RESIDENT).stream()
                .filter(user -> user.getSocietyId() != null && 
                              user.getSocietyId().equals(societyId) && 
                              user.getStatus() == User.UserStatus.PENDING)
                .toList();

        return ResponseEntity.ok(pendingResidents);
    }

    // Approve resident
    @PostMapping("/residents/{id}/approve")
    public ResponseEntity<?> approveResident(@PathVariable Long id, Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        Long societyId = admin.getSocietyId();

        User resident = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resident not found"));

        // Verify resident belongs to the same society
        if (!resident.getSocietyId().equals(societyId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Resident does not belong to your society"));
        }

        resident.setStatus(User.UserStatus.ACTIVE);
        userRepository.save(resident);

        return ResponseEntity.ok(Map.of("message", "Resident approved successfully"));
    }

    // Reject resident
    @PostMapping("/residents/{id}/reject")
    public ResponseEntity<?> rejectResident(@PathVariable Long id, Authentication authentication) {
        User admin = (User) authentication.getPrincipal();
        Long societyId = admin.getSocietyId();

        User resident = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resident not found"));

        // Verify resident belongs to the same society
        if (!resident.getSocietyId().equals(societyId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Resident does not belong to your society"));
        }

        resident.setStatus(User.UserStatus.REJECTED);
        userRepository.save(resident);

        return ResponseEntity.ok(Map.of("message", "Resident rejected successfully"));
    }
}
