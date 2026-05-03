package com.society.controller;

import com.society.entity.Worker;
import com.society.service.WorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {

    @Autowired
    private WorkerService workerService;

    // Get all workers - accessible to all authenticated users
    @GetMapping
    public ResponseEntity<List<Worker>> getAllWorkers(@RequestParam(required = false) String status) {
        List<Worker> workers;
        if ("ACTIVE".equalsIgnoreCase(status)) {
            workers = workerService.getActiveWorkers();
        } else {
            workers = workerService.getAllWorkers();
        }
        return ResponseEntity.ok(workers);
    }

    // Get worker by ID
    @GetMapping("/{id}")
    public ResponseEntity<Worker> getWorkerById(@PathVariable Long id) {
        Worker worker = workerService.getWorkerById(id);
        if (worker != null) {
            return ResponseEntity.ok(worker);
        }
        return ResponseEntity.notFound().build();
    }

    // Admin only endpoints
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<Worker> createWorker(@RequestBody Worker worker) {
        Worker savedWorker = workerService.saveWorker(worker);
        return ResponseEntity.ok(savedWorker);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Worker> updateWorker(@PathVariable Long id, @RequestBody Worker worker) {
        worker.setId(id);
        Worker updatedWorker = workerService.saveWorker(worker);
        return ResponseEntity.ok(updatedWorker);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorker(@PathVariable Long id) {
        workerService.deleteWorker(id);
        return ResponseEntity.ok().build();
    }

    // Attendance stats - admin only
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAttendanceStats() {
        Map<String, Object> stats = workerService.getAttendanceStats();
        return ResponseEntity.ok(stats);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/attendance/report/{year}/{month}")
    public ResponseEntity<Map<String, Object>> getMonthlyAttendanceReport(
            @PathVariable int year, @PathVariable int month) {
        Map<String, Object> report = workerService.getMonthlyAttendanceReport(year, month);
        return ResponseEntity.ok(report);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/attendance/worker/{workerId}")
    public ResponseEntity<Map<String, Object>> getWorkerAttendanceReport(
            @PathVariable Long workerId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        Map<String, Object> report = workerService.getWorkerAttendanceReport(
                workerId, 
                java.time.LocalDate.parse(startDate), 
                java.time.LocalDate.parse(endDate)
        );
        return ResponseEntity.ok(report);
    }
}
