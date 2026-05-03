package com.society.controller;

import com.society.entity.Guard;
import com.society.entity.GuardAttendance;
import com.society.service.GuardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/guard-attendance")
@PreAuthorize("hasRole('GUARD') or hasRole('ADMIN')")
public class GuardAttendanceController {

    @Autowired
    private GuardService guardService;

    // Get All Active Guards
    @GetMapping("/guards")
    public ResponseEntity<List<Guard>> getAllGuards() {
        List<Guard> guards = guardService.getActiveGuards();
        return ResponseEntity.ok(guards);
    }

    // Mark Guard Attendance
    @PostMapping("/mark")
    public ResponseEntity<GuardAttendance> markAttendance(@RequestBody Map<String, Object> request) {
        try {
            Long guardId = Long.valueOf(request.get("guardId").toString());
            String statusStr = (String) request.get("status");
            String checkInPhoto = (String) request.get("photoBase64");
            String notes = (String) request.get("notes");
            String markedBy = (String) request.get("markedBy");

            GuardAttendance.AttendanceStatus status = GuardAttendance.AttendanceStatus.valueOf(statusStr);
            LocalDate today = LocalDate.now();
            LocalDateTime checkInTime = LocalDateTime.now();

            GuardAttendance attendance = guardService.markGuardAttendance(
                    guardId, today, status, checkInTime, checkInPhoto, notes, markedBy);

            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Mark Guard Check-Out
    @PostMapping("/{guardId}/checkout")
    public ResponseEntity<GuardAttendance> markCheckOut(@PathVariable Long guardId) {
        try {
            LocalDate today = LocalDate.now();
            LocalDateTime checkOutTime = LocalDateTime.now();

            GuardAttendance attendance = guardService.markGuardCheckOut(guardId, today, checkOutTime, null);
            return ResponseEntity.ok(attendance);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get Today's Guard Attendance
    @GetMapping("/today")
    public ResponseEntity<List<GuardAttendance>> getTodayAttendance() {
        List<GuardAttendance> attendances = guardService.getTodayGuardAttendance();
        return ResponseEntity.ok(attendances);
    }

    // Get Guard Attendance Stats
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAttendanceStats() {
        Map<String, Object> stats = guardService.getGuardAttendanceStats();
        return ResponseEntity.ok(stats);
    }

    // Get Individual Guard Attendance History
    @GetMapping("/{guardId}")
    public ResponseEntity<List<GuardAttendance>> getGuardAttendance(@PathVariable Long guardId) {
        List<GuardAttendance> attendances = guardService.getGuardAttendance(guardId);
        return ResponseEntity.ok(attendances);
    }
}
