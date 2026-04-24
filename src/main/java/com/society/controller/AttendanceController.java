package com.society.controller;

import com.society.entity.Attendance;
import com.society.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private WorkerService workerService;

    @PostMapping("/guard/checkin")
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN')")
    public ResponseEntity<?> markCheckIn(@RequestBody Map<String, Object> request) {
        try {
            Long workerId = Long.valueOf(request.get("workerId").toString());
            String checkInTimeStr = request.get("checkInTime").toString();
            String workerPhoto = request.get("workerPhoto") != null ? request.get("workerPhoto").toString() : null;
            
            // Parse ISO dateTime string
            LocalDateTime checkInTime = LocalDateTime.parse(checkInTimeStr.replace("Z", "").substring(0, 19));
            LocalDate date = checkInTime.toLocalDate();
            
            Attendance attendance = workerService.markAttendance(workerId, date, checkInTime, workerPhoto);
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/guard/checkout")
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN')")
    public ResponseEntity<?> markCheckOut(@RequestBody Map<String, Object> request) {
        try {
            Long workerId = Long.valueOf(request.get("workerId").toString());
            String checkOutTimeStr = request.get("checkOutTime").toString();
            
            // Parse ISO dateTime string
            LocalDateTime checkOutTime = LocalDateTime.parse(checkOutTimeStr.replace("Z", "").substring(0, 19));
            LocalDate date = checkOutTime.toLocalDate();
            
            Attendance attendance = workerService.markCheckOut(workerId, date, checkOutTime);
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/guard/today")
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN')")
    public ResponseEntity<List<Attendance>> getTodayAttendance() {
        List<Attendance> attendance = workerService.getTodayAttendance();
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/guard/worker/{workerId}")
    @PreAuthorize("hasRole('GUARD') or hasRole('ADMIN')")
    public ResponseEntity<List<Attendance>> getWorkerAttendance(@PathVariable Long workerId) {
        List<Attendance> attendance = workerService.getWorkerAttendance(workerId);
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/admin/reports/daily")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getDailyReport(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Map<String, Object> report = workerService.getDailyAttendanceReport(date);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/admin/reports/monthly")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getMonthlyReport(
            @RequestParam int year,
            @RequestParam int month) {
        Map<String, Object> report = workerService.getMonthlyAttendanceReport(year, month);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/admin/reports/worker/{workerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getWorkerReport(
            @PathVariable Long workerId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Map<String, Object> report = workerService.getWorkerAttendanceReport(workerId, startDate, endDate);
        return ResponseEntity.ok(report);
    }
}
