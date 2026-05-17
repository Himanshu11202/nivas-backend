package com.society.controller;

import com.society.entity.User;
import com.society.entity.Worker;
import com.society.entity.Guard;
import com.society.service.CurrentUserService;
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
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'SOCIETY_ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerService workerService;

    @Autowired
    private GuardService guardService;

    @Autowired
    private CurrentUserService currentUserService;

    private Optional<Long> societyScope(HttpServletRequest request) {
        return currentUserService.getUserFromAuthHeader(request.getHeader("Authorization"))
                .filter(currentUserService::isSocietyScopedAdmin)
                .map(User::getSocietyId);
    }

    private boolean belongsToSociety(User user, Long societyId) {
        return societyId == null || (user.getSocietyId() != null && user.getSocietyId().equals(societyId));
    }

    @GetMapping("/residents/pending")
    public ResponseEntity<List<User>> getPendingResidents(HttpServletRequest request) {
        Optional<Long> societyId = societyScope(request);
        List<User> pendingResidents = userRepository.findByStatus(User.UserStatus.PENDING).stream()
                .filter(u -> u.getRole() == User.Role.RESIDENT)
                .filter(u -> belongsToSociety(u, societyId.orElse(null)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(pendingResidents);
    }

    @PostMapping("/residents/{id}/approve")
    public ResponseEntity<?> approveResident(@PathVariable Long id, HttpServletRequest request) {
        try {
            Optional<Long> societyId = societyScope(request);
            User resident = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Resident not found"));

            if (societyId.isPresent() && !belongsToSociety(resident, societyId.get())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Resident does not belong to your society"));
            }

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

    @PostMapping("/residents/{id}/reject")
    public ResponseEntity<?> rejectResident(@PathVariable Long id, HttpServletRequest request) {
        try {
            Optional<Long> societyId = societyScope(request);
            User resident = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Resident not found"));

            if (societyId.isPresent() && !belongsToSociety(resident, societyId.get())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Resident does not belong to your society"));
            }

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

    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats(HttpServletRequest request) {
        Optional<Long> societyId = societyScope(request);

        List<User> residents = userRepository.findByRole(User.Role.RESIDENT).stream()
                .filter(u -> belongsToSociety(u, societyId.orElse(null)))
                .collect(Collectors.toList());

        long pendingResidents = residents.stream()
                .filter(u -> u.getStatus() == User.UserStatus.PENDING).count();
        long activeResidents = residents.stream()
                .filter(u -> u.getStatus() == User.UserStatus.ACTIVE).count();
        long rejectedResidents = residents.stream()
                .filter(u -> u.getStatus() == User.UserStatus.REJECTED).count();

        Map<String, Object> stats = Map.of(
                "totalResidents", residents.size(),
                "totalWorkers", userRepository.countByRole(User.Role.WORKER),
                "totalGuards", userRepository.countByRole(User.Role.GUARD),
                "pendingResidents", pendingResidents,
                "activeResidents", activeResidents,
                "rejectedResidents", rejectedResidents,
                "presentToday", 0,
                "totalStaff", userRepository.countByRole(User.Role.WORKER) + userRepository.countByRole(User.Role.GUARD)
        );
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/residents")
    public ResponseEntity<List<User>> getAllResidents(HttpServletRequest request) {
        Optional<Long> societyId = societyScope(request);
        List<User> residents = userRepository.findByRole(User.Role.RESIDENT).stream()
                .filter(u -> belongsToSociety(u, societyId.orElse(null)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(residents);
    }

    @DeleteMapping("/residents/{id}")
    public ResponseEntity<?> deleteResident(@PathVariable Long id, HttpServletRequest request) {
        try {
            Optional<Long> societyId = societyScope(request);
            User resident = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Resident not found"));

            if (resident.getRole() != User.Role.RESIDENT) {
                return ResponseEntity.badRequest().body(Map.of("error", "User is not a resident"));
            }

            if (societyId.isPresent() && !belongsToSociety(resident, societyId.get())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Resident does not belong to your society"));
            }

            userRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Resident deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error deleting resident: " + e.getMessage()));
        }
    }

    @GetMapping("/workers")
    public ResponseEntity<List<Worker>> getAllWorkers() {
        List<Worker> workers = workerService.getAllWorkers();
        return ResponseEntity.ok(workers);
    }

    @PostMapping("/workers")
    public ResponseEntity<Worker> createWorker(@RequestBody Worker worker) {
        Worker createdWorker = workerService.createWorker(worker);
        return ResponseEntity.ok(createdWorker);
    }

    @PutMapping("/workers/{id}")
    public ResponseEntity<Worker> updateWorker(@PathVariable Long id, @RequestBody Worker worker) {
        Worker updatedWorker = workerService.updateWorker(id, worker);
        return ResponseEntity.ok(updatedWorker);
    }

    @DeleteMapping("/workers/{id}")
    public ResponseEntity<Void> deleteWorker(@PathVariable Long id) {
        workerService.deleteWorker(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/guards")
    public ResponseEntity<List<Guard>> getAllGuards() {
        List<Guard> guards = guardService.getAllGuards();
        return ResponseEntity.ok(guards);
    }

    @PostMapping("/guards")
    public ResponseEntity<?> createGuard(@RequestBody Guard guard, HttpServletRequest request) {
        try {
            Guard createdGuard = guardService.createGuard(guard);
            return ResponseEntity.ok(createdGuard);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to create guard: " + e.getMessage()));
        }
    }

    @PutMapping("/guards/{id}")
    public ResponseEntity<Guard> updateGuard(@PathVariable Long id, @RequestBody Guard guard) {
        Guard updatedGuard = guardService.updateGuard(id, guard);
        return ResponseEntity.ok(updatedGuard);
    }

    @DeleteMapping("/guards/{id}")
    public ResponseEntity<Void> deleteGuard(@PathVariable Long id) {
        guardService.deleteGuard(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/attendance/monthly")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyAttendance(@RequestParam String month) {
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
