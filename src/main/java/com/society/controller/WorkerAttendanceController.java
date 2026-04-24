package com.society.controller;

import com.society.entity.WorkerAttendance;
import com.society.service.WorkerAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/worker-attendance")
@CrossOrigin(origins = "*")
public class WorkerAttendanceController {
    
    @Autowired
    private WorkerAttendanceService attendanceService;
    
    // Mark attendance (check-in) - OLD method
    @PostMapping("/mark")
    public ResponseEntity<?> markAttendance(
            @RequestParam Long workerId,
            @RequestParam WorkerAttendance.AttendanceStatus status,
            @RequestParam(required = false) String photoBase64,
            @RequestParam(required = false) String notes,
            @RequestParam String markedBy) {
        try {
            WorkerAttendance attendance = attendanceService.markAttendance(workerId, status, photoBase64, notes, markedBy);
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    // NEW: Check In with Request Body
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/checkin")
    public ResponseEntity<?> checkIn(@RequestBody Map<String, Object> request) {
        try {
            Long workerId = Long.valueOf(request.get("workerId").toString());
            String checkInPhoto = (String) request.get("checkInPhoto");
            String markedBy = (String) request.get("markedBy");
            
            WorkerAttendance attendance = attendanceService.checkIn(workerId, checkInPhoto, markedBy);
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // NEW: Check Out with Request Body
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping("/checkout")
    public ResponseEntity<?> checkOut(@RequestBody Map<String, Object> request) {
        try {
            Long workerId = Long.valueOf(request.get("workerId").toString());
            String markedBy = (String) request.get("markedBy");
            
            WorkerAttendance attendance = attendanceService.checkOut(workerId, markedBy);
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    // Mark checkout - OLD method
    @PostMapping("/checkout-old")
    public ResponseEntity<?> markCheckout(
            @RequestParam Long workerId,
            @RequestParam(required = false) String photoBase64) {
        try {
            WorkerAttendance attendance = attendanceService.markCheckout(workerId, photoBase64);
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
    
    // Get today's attendance
    @GetMapping("/today")
    public ResponseEntity<List<WorkerAttendance>> getTodayAttendance() {
        return ResponseEntity.ok(attendanceService.getTodayAttendance());
    }
    
    // Get attendance by date
    @GetMapping("/date/{date}")
    public ResponseEntity<List<WorkerAttendance>> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getAttendanceByDate(date));
    }
    
    // Get worker attendance history
    @GetMapping("/worker/{workerId}")
    public ResponseEntity<List<WorkerAttendance>> getWorkerAttendance(@PathVariable Long workerId) {
        return ResponseEntity.ok(attendanceService.getWorkerAttendanceHistory(workerId));
    }
    
    // Get today's stats
    @GetMapping("/stats/today")
    public ResponseEntity<WorkerAttendanceService.AttendanceStats> getTodayStats() {
        return ResponseEntity.ok(attendanceService.getTodayStats());
    }
    
    // Get attendance stats for admin dashboard
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAttendanceStats() {
        return ResponseEntity.ok(attendanceService.getDetailedStats());
    }
    
    // Get monthly report for a worker
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/worker/{workerId}/monthly")
    public ResponseEntity<Map<String, Object>> getMonthlyReport(
            @PathVariable Long workerId,
            @RequestParam int year,
            @RequestParam int month) {
        Map<String, Object> report = attendanceService.getMonthlyReport(workerId, year, month);
        return ResponseEntity.ok(report);
    }
}
