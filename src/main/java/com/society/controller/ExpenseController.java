package com.society.controller;

import com.society.entity.Expense;
import com.society.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "http://localhost:3000")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    // Create expense
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createExpense(@RequestBody Map<String, Object> request) {
        try {
            Expense expense = new Expense();
            expense.setType(Expense.ExpenseType.valueOf((String) request.get("type")));
            expense.setAmount(new BigDecimal(request.get("amount").toString()));
            expense.setDate(LocalDate.parse((String) request.get("date")));
            expense.setNote((String) request.get("note"));
            Object billImageObj = request.get("billImage");
            expense.setBillImage(billImageObj != null ? (String) billImageObj : null);

            Expense saved = expenseService.createExpense(expense);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get all expenses
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Expense>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    // Get expenses by month
    @GetMapping("/month/{month}/{year}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Expense>> getExpensesByMonth(@PathVariable int month, @PathVariable int year) {
        return ResponseEntity.ok(expenseService.getExpensesByMonth(month, year));
    }

    // Get expenses by month (for residents - read-only)
    @GetMapping("/resident/month/{month}/{year}")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Expense>> getExpensesByMonthForResident(@PathVariable int month, @PathVariable int year) {
        return ResponseEntity.ok(expenseService.getExpensesByMonth(month, year));
    }

    // Get expense stats (for residents - read-only)
    @GetMapping("/resident/stats/{month}/{year}")
    @PreAuthorize("hasRole('RESIDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getExpenseStatsForResident(@PathVariable int month, @PathVariable int year) {
        return ResponseEntity.ok(expenseService.getExpenseStats(month, year));
    }

    // Get expense stats
    @GetMapping("/stats/{month}/{year}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, Object>> getExpenseStats(@PathVariable int month, @PathVariable int year) {
        return ResponseEntity.ok(expenseService.getExpenseStats(month, year));
    }

    // Get monthly trend
    @GetMapping("/trend")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyTrend() {
        return ResponseEntity.ok(expenseService.getMonthlyTrend());
    }

    // Delete expense
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        try {
            expenseService.deleteExpense(id);
            return ResponseEntity.ok(Map.of("message", "Expense deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Generate maintenance from expenses
    @PostMapping("/generate-maintenance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> generateMaintenance(@RequestBody Map<String, Object> request) {
        try {
            int month = Integer.parseInt(request.get("month").toString());
            int year = Integer.parseInt(request.get("year").toString());
            String description = (String) request.get("description");

            Map<String, Object> result = expenseService.generateMaintenanceFromExpenses(month, year, description);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
