package com.society.controller;

import com.society.entity.Visitor;
import com.society.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/visitors")
@PreAuthorize("hasRole('ADMIN')")
public class VisitorController {

    @Autowired
    private VisitorService visitorService;

    // Create Visitor
    @PostMapping
    public ResponseEntity<Visitor> createVisitor(@Valid @RequestBody Visitor visitor) {
        try {
            Visitor createdVisitor = visitorService.createVisitor(visitor);
            return ResponseEntity.ok(createdVisitor);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Update Visitor
    @PutMapping("/{id}")
    public ResponseEntity<Visitor> updateVisitor(@PathVariable Long id, @Valid @RequestBody Visitor visitor) {
        try {
            Visitor updatedVisitor = visitorService.updateVisitor(id, visitor);
            return ResponseEntity.ok(updatedVisitor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Delete Visitor
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisitor(@PathVariable Long id) {
        try {
            visitorService.deleteVisitor(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get Visitor by ID
    @GetMapping("/{id}")
    public ResponseEntity<Visitor> getVisitorById(@PathVariable Long id) {
        try {
            Visitor visitor = visitorService.getVisitorById(id);
            return ResponseEntity.ok(visitor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get All Visitors
    @GetMapping
    public ResponseEntity<List<Visitor>> getAllVisitors() {
        List<Visitor> visitors = visitorService.getAllVisitors();
        return ResponseEntity.ok(visitors);
    }

    // Get Visitors by Flat Number
    @GetMapping("/flat/{flatNumber}")
    public ResponseEntity<List<Visitor>> getVisitorsByFlat(@PathVariable String flatNumber) {
        List<Visitor> visitors = visitorService.getVisitorsByFlatNumber(flatNumber);
        return ResponseEntity.ok(visitors);
    }

    // Get Visitors by Status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Visitor>> getVisitorsByStatus(@PathVariable Visitor.VisitorStatus status) {
        List<Visitor> visitors = visitorService.getVisitorsByStatus(status);
        return ResponseEntity.ok(visitors);
    }

    // Approve Visitor
    @PatchMapping("/{id}/approve")
    public ResponseEntity<Visitor> approveVisitor(@PathVariable Long id) {
        try {
            Visitor approvedVisitor = visitorService.approveVisitor(id);
            return ResponseEntity.ok(approvedVisitor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Reject Visitor
    @PatchMapping("/{id}/reject")
    public ResponseEntity<Visitor> rejectVisitor(@PathVariable Long id) {
        try {
            Visitor rejectedVisitor = visitorService.rejectVisitor(id);
            return ResponseEntity.ok(rejectedVisitor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Mark Visitor Exit
    @PatchMapping("/{id}/exit")
    public ResponseEntity<Visitor> markVisitorExit(@PathVariable Long id) {
        try {
            Visitor visitor = visitorService.markVisitorExit(id);
            return ResponseEntity.ok(visitor);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get Visitor Statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getVisitorStats() {
        Map<String, Object> stats = Map.of(
                "totalVisitors", visitorService.getTotalVisitors(),
                "pendingVisitors", visitorService.getPendingVisitors(),
                "todayVisitors", visitorService.getTodayVisitors()
        );
        return ResponseEntity.ok(stats);
    }

    // Get Today's Visitors
    @GetMapping("/today")
    public ResponseEntity<List<Visitor>> getTodayVisitors() {
        List<Visitor> visitors = visitorService.getTodayVisitorsList();
        return ResponseEntity.ok(visitors);
    }
}
