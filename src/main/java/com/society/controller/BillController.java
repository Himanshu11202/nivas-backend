package com.society.controller;

import com.society.entity.Bill;
import com.society.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BillController {

    @Autowired
    private BillService billService;

    // ==================== ADMIN APIs ====================

    // Send maintenance to all residents
    @PostMapping("/maintenance/send")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> sendMaintenanceToAll(
            @RequestBody Map<String, Object> request) {
        try {
            BigDecimal amount = new BigDecimal(request.get("amount").toString());
            String description = (String) request.getOrDefault("description", "Monthly Maintenance");
            Integer month = request.containsKey("month") ? (Integer) request.get("month") : null;
            Integer year = request.containsKey("year") ? (Integer) request.get("year") : null;

            Map<String, Object> result = billService.sendMaintenanceToAll(amount, description, month, year);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get all bills (Admin)
    @GetMapping("/bills")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Bill>> getAllBills() {
        try {
            List<Bill> bills = billService.getAllBills();
            return ResponseEntity.ok(bills);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Search bills by flat number
    @GetMapping("/bills/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Bill>> searchBills(@RequestParam String flatNumber) {
        try {
            List<Bill> bills = billService.searchBillsByFlatNumber(flatNumber);
            return ResponseEntity.ok(bills);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get bill statistics (Admin)
    @GetMapping("/bills/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getBillStats() {
        try {
            Map<String, Object> stats = billService.getBillStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== RESIDENT APIs ====================

    // Get bills for current user
    @GetMapping("/bills/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Bill>> getUserBills(@PathVariable Long userId) {
        try {
            List<Bill> bills = billService.getBillsByUserId(userId);
            return ResponseEntity.ok(bills);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get my bills (for logged in user)
    @GetMapping("/bills/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Bill>> getMyBills(@RequestParam Long userId) {
        try {
            List<Bill> bills = billService.getBillsByUserId(userId);
            return ResponseEntity.ok(bills);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Mark bill as paid (dummy payment)
    @PutMapping("/bills/{id}/pay")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> markBillAsPaid(@PathVariable Long id) {
        try {
            Bill bill = billService.markAsPaid(id);
            return ResponseEntity.ok(Map.of(
                "message", "Payment successful",
                "bill", bill
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error processing payment"));
        }
    }

    // Generate receipt
    @GetMapping("/bills/{id}/receipt")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> generateReceipt(@PathVariable Long id) {
        try {
            Map<String, Object> receipt = billService.generateReceipt(id);
            return ResponseEntity.ok(receipt);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get user's bill statistics
    @GetMapping("/bills/user/{userId}/stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getUserBillStats(@PathVariable Long userId) {
        try {
            Map<String, Object> stats = billService.getUserBillStats(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== PENALTY APIs ====================

    // Check and apply penalties (Admin only)
    @PostMapping("/bills/check-penalties")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> checkAndApplyPenalties() {
        try {
            billService.checkAndApplyPenalties();
            return ResponseEntity.ok(Map.of("message", "Penalty check completed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get bills with penalty info for user
    @GetMapping("/bills/user/{userId}/with-penalty")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Map<String, Object>>> getBillsWithPenaltyInfo(@PathVariable Long userId) {
        try {
            List<Map<String, Object>> bills = billService.getBillsWithPenaltyInfo(userId);
            return ResponseEntity.ok(bills);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
