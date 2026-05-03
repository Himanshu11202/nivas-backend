package com.society.controller;

import com.society.entity.Visitor;
import com.society.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resident/visitors")
@PreAuthorize("hasRole('RESIDENT')")
public class ResidentVisitorController {

    @Autowired
    private VisitorService visitorService;

    // Get My Flat's Visitors
    @GetMapping("/my-flat")
    public ResponseEntity<?> getMyFlatVisitors(@RequestParam String flatNumber) {
        try {
            System.out.println("DEBUG: Getting visitors for flat number: " + flatNumber);
            List<Visitor> visitors = visitorService.getVisitorsByFlatNumber(flatNumber);
            System.out.println("DEBUG: Found " + visitors.size() + " visitors for flat " + flatNumber);
            for (Visitor v : visitors) {
                System.out.println("DEBUG: Visitor - ID: " + v.getId() + ", Name: " + v.getVisitorName() + ", Status: " + v.getStatus() + ", Flat: " + v.getFlatNumber());
            }
            return ResponseEntity.ok(visitors);
        } catch (Exception e) {
            System.err.println("DEBUG ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // DEBUG: Get all visitors to check flat numbers
    @GetMapping("/debug/all")
    public ResponseEntity<List<Visitor>> getAllVisitorsDebug() {
        List<Visitor> allVisitors = visitorService.getAllVisitors();
        System.out.println("DEBUG ALL VISITORS: " + allVisitors.size() + " total");
        for (Visitor v : allVisitors) {
            System.out.println("DEBUG: Visitor " + v.getId() + " - Name: " + v.getVisitorName() + ", Flat: " + v.getFlatNumber() + ", Status: " + v.getStatus());
        }
        return ResponseEntity.ok(allVisitors);
    }

    // Approve Visitor
    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> approveVisitor(@PathVariable Long id) {
        try {
            System.out.println("DEBUG: Approving visitor ID: " + id);
            Visitor approvedVisitor = visitorService.approveVisitor(id);
            System.out.println("DEBUG: Visitor approved successfully: " + approvedVisitor.getId());
            return ResponseEntity.ok(approvedVisitor);
        } catch (RuntimeException e) {
            System.err.println("DEBUG ERROR approving visitor: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Visitor not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("DEBUG ERROR approving visitor: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Reject Visitor
    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectVisitor(@PathVariable Long id) {
        try {
            System.out.println("DEBUG: Rejecting visitor ID: " + id);
            Visitor rejectedVisitor = visitorService.rejectVisitor(id);
            System.out.println("DEBUG: Visitor rejected successfully: " + rejectedVisitor.getId());
            return ResponseEntity.ok(rejectedVisitor);
        } catch (RuntimeException e) {
            System.err.println("DEBUG ERROR rejecting visitor: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Visitor not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("DEBUG ERROR rejecting visitor: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
