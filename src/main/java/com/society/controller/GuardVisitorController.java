package com.society.controller;

import com.society.entity.Visitor;
import com.society.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/guard/visitor-management")
@PreAuthorize("hasRole('GUARD')")
@CrossOrigin(origins = "http://localhost:3000")
public class GuardVisitorController {

    @Autowired
    private VisitorService visitorService;

    // Create Visitor (Guard adds visitor)
    @PostMapping
    public ResponseEntity<Visitor> createVisitor(@Valid @RequestBody Visitor visitor) {
        try {
            Visitor createdVisitor = visitorService.createVisitor(visitor);
            return ResponseEntity.ok(createdVisitor);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Mark Visitor Entry
    @PatchMapping("/{id}/entry")
    public ResponseEntity<Visitor> markVisitorEntry(@PathVariable Long id) {
        try {
            Visitor visitor = visitorService.approveVisitor(id);
            return ResponseEntity.ok(visitor);
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

    // Get Today's Visitors
    @GetMapping("/today")
    public ResponseEntity<List<Visitor>> getTodayVisitors() {
        List<Visitor> visitors = visitorService.getTodayVisitorsList();
        return ResponseEntity.ok(visitors);
    }

    // Get Pending Visitors
    @GetMapping("/pending")
    public ResponseEntity<List<Visitor>> getPendingVisitors() {
        List<Visitor> visitors = visitorService.getVisitorsByStatus(Visitor.VisitorStatus.PENDING);
        return ResponseEntity.ok(visitors);
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
}
