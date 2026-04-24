package com.society.controller;

import com.society.entity.Maintenance;
import com.society.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    // Admin endpoints
    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Maintenance> createMaintenance(@Valid @RequestBody Maintenance maintenance) {
        Maintenance createdMaintenance = maintenanceService.createMaintenance(maintenance);
        return ResponseEntity.ok(createdMaintenance);
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Maintenance> updateMaintenance(@PathVariable Long id, @Valid @RequestBody Maintenance maintenance) {
        Maintenance updatedMaintenance = maintenanceService.updateMaintenance(id, maintenance);
        return ResponseEntity.ok(updatedMaintenance);
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteMaintenance(@PathVariable Long id) {
        maintenanceService.deleteMaintenance(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Maintenance>> getAllMaintenance() {
        List<Maintenance> maintenanceList = maintenanceService.getAllMaintenance();
        return ResponseEntity.ok(maintenanceList);
    }

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getMaintenanceStats() {
        Map<String, Object> stats = maintenanceService.getMaintenanceStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/admin/report/monthly/{year}/{month}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getMonthlyReport(@PathVariable int year, @PathVariable int month) {
        Map<String, Object> report = maintenanceService.getMonthlyReport(year, month);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/admin/generate-monthly")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> generateMonthlyMaintenance(
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam BigDecimal amount) {
        maintenanceService.generateMonthlyMaintenance(year, month, amount);
        return ResponseEntity.ok("Monthly maintenance generated successfully");
    }

    @PostMapping("/check-overdue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> checkOverdueMaintenance() {
        maintenanceService.checkOverdueMaintenance();
        return ResponseEntity.ok("Overdue maintenance checked and notifications sent");
    }

    // Resident endpoints
    @GetMapping("/resident/my-maintenance")
    @PreAuthorize("hasRole('RESIDENT')")
    public ResponseEntity<List<Maintenance>> getMyMaintenance() {
        // In a real app, get user ID from security context
        Long userId = getCurrentUserId();
        List<Maintenance> maintenanceList = maintenanceService.getMaintenanceByUserId(userId);
        return ResponseEntity.ok(maintenanceList);
    }

    @PostMapping("/resident/pay/{id}")
    @PreAuthorize("hasRole('RESIDENT')")
    public ResponseEntity<Maintenance> payMaintenance(@PathVariable Long id) {
        Maintenance paidMaintenance = maintenanceService.payMaintenance(id);
        return ResponseEntity.ok(paidMaintenance);
    }

    // Helper method to get current user ID (simplified)
    private Long getCurrentUserId() {
        // In a real app, get this from Spring Security Context
        return 1L; // Placeholder
    }
}
