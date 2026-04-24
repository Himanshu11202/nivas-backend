package com.society.controller;

import com.society.entity.Attendance;
import com.society.entity.Worker;
import com.society.entity.User;
import com.society.service.WorkerService;
import com.society.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/guard/worker-management")
@PreAuthorize("hasRole('GUARD')")
@CrossOrigin(origins = "http://localhost:3000")
public class GuardWorkerController {

    @Autowired
    private WorkerService workerService;

    @Autowired
    private UserRepository userRepository;

    // Get All Staff - Workers and Guards (for Guard Panel)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStaff() {
        // Get Active Workers
        List<Worker> workers = workerService.getActiveWorkers();
        
        // Get Active Guards
        List<User> guards = userRepository.findByRoleAndStatus(User.Role.GUARD, User.UserStatus.ACTIVE);
        
        Map<String, Object> response = new HashMap<>();
        response.put("workers", workers);
        response.put("guards", guards);
        
        return ResponseEntity.ok(response);
    }

    // Mark Check-In
    @PostMapping("/{workerId}/checkin")
    public ResponseEntity<Attendance> markCheckIn(
            @PathVariable Long workerId,
            @RequestBody Map<String, String> request) {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime checkInTime = LocalDateTime.now();
            String workerPhoto = request.get("workerPhoto");
            
            Attendance attendance = workerService.markAttendance(workerId, today, checkInTime, workerPhoto);
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Mark Check-Out
    @PostMapping("/{workerId}/checkout")
    public ResponseEntity<Attendance> markCheckOut(@PathVariable Long workerId) {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime checkOutTime = LocalDateTime.now();
            
            Attendance attendance = workerService.markCheckOut(workerId, today, checkOutTime);
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get Today's Attendance
    @GetMapping("/attendance/today")
    public ResponseEntity<List<Attendance>> getTodayAttendance() {
        List<Attendance> attendances = workerService.getTodayAttendance();
        return ResponseEntity.ok(attendances);
    }

    // Get Worker by ID
    @GetMapping("/{workerId}")
    public ResponseEntity<Worker> getWorkerById(@PathVariable Long workerId) {
        try {
            Worker worker = workerService.getWorkerById(workerId);
            return ResponseEntity.ok(worker);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
