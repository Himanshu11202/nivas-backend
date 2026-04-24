package com.society.controller;

import com.society.dto.VisitorRequest;
import com.society.entity.Visitor;
import com.society.service.GuardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/guard")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('GUARD')")
public class GuardController {

    @Autowired
    private GuardService guardService;

    // Visitor Management
    @PostMapping("/visitors")
    public ResponseEntity<Visitor> addVisitor(
            @RequestParam("visitorName") String visitorName,
            @RequestParam("visitorPhone") String visitorPhone,
            @RequestParam("flatNumber") String flatNumber,
            @RequestParam("purpose") String purpose,
            @RequestParam(value = "vehicleNumber", required = false) String vehicleNumber,
            @RequestParam(value = "visitorPhoto", required = false) MultipartFile visitorPhoto) {
        
        String photoBase64 = null;
        if (visitorPhoto != null && !visitorPhoto.isEmpty()) {
            try {
                photoBase64 = Base64.getEncoder().encodeToString(visitorPhoto.getBytes());
            } catch (IOException e) {
                System.err.println("Error converting photo to base64: " + e.getMessage());
            }
        }
        
        Visitor visitor = guardService.addVisitor(
                visitorName,
                visitorPhone,
                flatNumber,
                purpose,
                vehicleNumber,
                photoBase64
        );
        return ResponseEntity.ok(visitor);
    }

    @PostMapping("/visitors/{id}/entry")
    public ResponseEntity<Visitor> recordEntry(@PathVariable Long id) {
        Visitor visitor = guardService.recordEntry(id);
        return ResponseEntity.ok(visitor);
    }

    @PostMapping("/visitors/{id}/exit")
    public ResponseEntity<Visitor> recordExit(@PathVariable Long id) {
        Visitor visitor = guardService.recordExit(id);
        return ResponseEntity.ok(visitor);
    }

    @GetMapping("/visitors")
    public ResponseEntity<List<Visitor>> getVisitorHistory() {
        List<Visitor> visitors = guardService.getVisitorHistory();
        return ResponseEntity.ok(visitors);
    }

    @GetMapping("/visitors/pending")
    public ResponseEntity<List<Visitor>> getPendingVisitors() {
        List<Visitor> visitors = guardService.getPendingVisitors();
        return ResponseEntity.ok(visitors);
    }

    // Emergency Alert
    @PostMapping("/emergency")
    public ResponseEntity<Void> sendEmergencyAlert(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        guardService.sendEmergencyAlert(message);
        return ResponseEntity.ok().build();
    }
}
