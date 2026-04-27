package com.society.controller;

import com.society.entity.User;
import com.society.entity.Worker;
import com.society.entity.Guard;
import com.society.service.AuthService;
import com.society.repository.UserRepository;
import com.society.service.WorkerService;
import com.society.service.GuardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private GuardService guardService;

    // Get Pending Residents
    @GetMapping("/residents/pending")
    public ResponseEntity<List<User>> getPendingResidents() {
        List<User> pendingResidents = userRepository.findByStatus(User.UserStatus.PENDING);
        return ResponseEntity.ok(pendingResidents);
    }

    // Approve Resident
    @PostMapping("/residents/{id}/approve")
    public ResponseEntity<?> approveResident(@PathVariable Long id) {
        try {
            User resident = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Resident not found"));
            
            resident.setStatus(User.UserStatus.ACTIVE);
            User updatedResident = userRepository.save(resident);
            
            return ResponseEntity.ok(Map.of(
                "message", "Resident approved successfully!",
                "resident", updatedResident
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error approving resident: " + e.getMessage()
            ));
        }
    }

    // Reject Resident
    @PostMapping("/residents/{id}/reject")
    public ResponseEntity<?> rejectResident(@PathVariable Long id) {
        try {
            User resident = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Resident not found"));
            
            resident.setStatus(User.UserStatus.REJECTED);
            User updatedResident = userRepository.save(resident);
            
            return ResponseEntity.ok(Map.of(
                "message", "Resident rejected successfully!",
                "resident", updatedResident
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error rejecting resident: " + e.getMessage()
            ));
        }
    }

    // Get Dashboard Statistics
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = Map.of(
                "totalResidents", userRepository.countByRole(User.Role.RESIDENT),
                "totalWorkers", userRepository.countByRole(User.Role.WORKER),
                "totalGuards", userRepository.countByRole(User.Role.GUARD),
                "pendingResidents", userRepository.countByStatus(User.UserStatus.PENDING),
                "activeResidents", userRepository.countByStatus(User.UserStatus.ACTIVE),
                "rejectedResidents", userRepository.countByStatus(User.UserStatus.REJECTED),
                "presentToday", 0, // This will be updated when attendance system is implemented
                "totalStaff", userRepository.countByRole(User.Role.WORKER) + userRepository.countByRole(User.Role.GUARD)
        );
        return ResponseEntity.ok(stats);
    }

    // Get All Residents
    @GetMapping("/residents")
    public ResponseEntity<List<User>> getAllResidents() {
        List<User> residents = userRepository.findByRole(User.Role.RESIDENT);
        return ResponseEntity.ok(residents);
    }

    // Delete Resident
    @DeleteMapping("/residents/{id}")
    public ResponseEntity<?> deleteResident(@PathVariable Long id) {
        try {
            User resident = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Resident not found"));
            
            if (resident.getRole() != User.Role.RESIDENT) {
                return ResponseEntity.badRequest().body(Map.of("error", "User is not a resident"));
            }
            
            userRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Resident deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error deleting resident: " + e.getMessage()));
        }
    }

    // Get All Workers
    @GetMapping("/workers")
    public ResponseEntity<List<Worker>> getAllWorkers() {
        List<Worker> workers = workerService.getAllWorkers();
        return ResponseEntity.ok(workers);
    }

    // Create Worker
    @PostMapping("/workers")
    public ResponseEntity<Worker> createWorker(@RequestBody Worker worker) {
        Worker createdWorker = workerService.createWorker(worker);
        return ResponseEntity.ok(createdWorker);
    }

    // Update Worker
    @PutMapping("/workers/{id}")
    public ResponseEntity<Worker> updateWorker(@PathVariable Long id, @RequestBody Worker worker) {
        Worker updatedWorker = workerService.updateWorker(id, worker);
        return ResponseEntity.ok(updatedWorker);
    }

    // Delete Worker
    @DeleteMapping("/workers/{id}")
    public ResponseEntity<Void> deleteWorker(@PathVariable Long id) {
        workerService.deleteWorker(id);
        return ResponseEntity.ok().build();
    }

    // Get All Guards
    @GetMapping("/guards")
    public ResponseEntity<List<Guard>> getAllGuards() {
        List<Guard> guards = guardService.getAllGuards();
        return ResponseEntity.ok(guards);
    }

    // Create Guard
    @PostMapping("/guards")
    public ResponseEntity<?> createGuard(@RequestBody Guard guard, HttpServletRequest request) {
        try {
            System.out.println("AdminController: Creating guard with data: " + guard.getName() + ", " + guard.getEmail());
            System.out.println("AdminController: Request URI: " + request.getRequestURI());
            System.out.println("AdminController: Request method: " + request.getMethod());
            
            Guard createdGuard = guardService.createGuard(guard);
            System.out.println("AdminController: Guard created successfully: " + createdGuard.getId());
            return ResponseEntity.ok(createdGuard);
        } catch (Exception e) {
            System.err.println("AdminController: Error creating guard: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to create guard: " + e.getMessage()));
        }
    }

    // Update Guard
    @PutMapping("/guards/{id}")
    public ResponseEntity<Guard> updateGuard(@PathVariable Long id, @RequestBody Guard guard) {
        Guard updatedGuard = guardService.updateGuard(id, guard);
        return ResponseEntity.ok(updatedGuard);
    }

    // Delete Guard
    @DeleteMapping("/guards/{id}")
    public ResponseEntity<Void> deleteGuard(@PathVariable Long id) {
        guardService.deleteGuard(id);
        return ResponseEntity.ok().build();
    }

    // Get Monthly Attendance
    @GetMapping("/attendance/monthly")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyAttendance(@RequestParam String month) {
        // This will return attendance data for workers and guards
        List<Map<String, Object>> attendanceData = List.of(
            Map.of(
                "date", "2026-03-01",
                "name", "John Worker",
                "role", "WORKER",
                "checkInTime", "09:00",
                "checkOutTime", "18:00",
                "status", "PRESENT",
                "hoursWorked", "9.0"
            ),
            Map.of(
                "date", "2026-03-01",
                "name", "Mike Guard",
                "role", "GUARD",
                "checkInTime", "08:00",
                "checkOutTime", "20:00",
                "status", "PRESENT",
                "hoursWorked", "12.0"
            )
        );
        return ResponseEntity.ok(attendanceData);
    }
}
