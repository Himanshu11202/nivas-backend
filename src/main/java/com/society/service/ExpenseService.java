package com.society.service;

import com.society.entity.Bill;
import com.society.entity.Expense;
import com.society.entity.Notification;
import com.society.entity.User;
import com.society.repository.BillRepository;
import com.society.repository.ExpenseRepository;
import com.society.repository.NotificationRepository;
import com.society.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    // Create expense
    @Transactional
    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    // Get all expenses
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAllByOrderByDateDesc();
    }

    // Get expenses by month
    public List<Expense> getExpensesByMonth(int month, int year) {
        return expenseRepository.findByMonthAndYear(month, year);
    }

    // Get expense by ID
    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
    }

    // Delete expense
    @Transactional
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    // Get total expense for month
    public BigDecimal getTotalExpenseForMonth(int month, int year) {
        return expenseRepository.getTotalExpenseByMonth(month, year);
    }

    // Get expense stats for dashboard
    public Map<String, Object> getExpenseStats(int month, int year) {
        Map<String, Object> stats = new HashMap<>();

        // Total expense
        BigDecimal total = getTotalExpenseForMonth(month, year);
        stats.put("totalExpense", total);

        // Expense breakdown by type
        List<Object[]> breakdown = expenseRepository.getExpenseBreakdownByType(month, year);
        Map<String, BigDecimal> breakdownMap = new HashMap<>();
        for (Object[] row : breakdown) {
            Expense.ExpenseType type = (Expense.ExpenseType) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            breakdownMap.put(type.toString(), amount);
        }
        stats.put("breakdown", breakdownMap);

        // Recent expenses
        List<Expense> recent = getExpensesByMonth(month, year);
        stats.put("recentExpenses", recent.stream().map(this::convertToMap).collect(Collectors.toList()));

        return stats;
    }

    // Get monthly trend (last 6 months)
    public List<Map<String, Object>> getMonthlyTrend() {
        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
        List<Object[]> monthlyData = expenseRepository.getMonthlyTotals(sixMonthsAgo);

        List<Map<String, Object>> trend = new ArrayList<>();
        for (Object[] row : monthlyData) {
            Map<String, Object> data = new HashMap<>();
            data.put("year", row[0]);
            data.put("month", row[1]);
            data.put("total", row[2]);
            trend.add(data);
        }
        return trend;
    }

    // Generate maintenance bills from expenses
    @Transactional
    public Map<String, Object> generateMaintenanceFromExpenses(int month, int year, String description) {
        // Get total expense
        BigDecimal totalExpense = getTotalExpenseForMonth(month, year);

        if (totalExpense.compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("No expenses found for the selected month");
        }

        // Get all active residents
        List<User> residents = userRepository.findByRoleAndStatus(User.Role.RESIDENT, User.UserStatus.ACTIVE);

        if (residents.isEmpty()) {
            throw new RuntimeException("No active residents found");
        }

        // Calculate per flat amount
        BigDecimal perFlatAmount = totalExpense.divide(
                new BigDecimal(residents.size()),
                2,
                RoundingMode.HALF_UP
        );

        // Create bills for each resident
        int billsCreated = 0;
        for (User resident : residents) {
            // Check if bill already exists for this month
            Optional<Bill> existingBill = billRepository.findByUserIdAndMonthAndYear(resident.getId(), month, year);
            if (existingBill.isPresent()) {
                continue; // Skip if bill already exists
            }

            Bill bill = new Bill();
            bill.setUser(resident);
            bill.setFlatNumber(resident.getFlatNumber());
            bill.setAmount(perFlatAmount);
            bill.setMonth(month);
            bill.setYear(year);
            bill.setStatus(Bill.BillStatus.UNPAID);
            bill.setDescription(description != null ? description : "Maintenance for " + getMonthName(month) + " " + year);
            bill.setCreatedAt(LocalDateTime.now());
            billRepository.save(bill);

            // Create notification
            Notification notification = new Notification();
            notification.setUserId(resident.getId());
            notification.setMessage("Your maintenance bill of ₹" + perFlatAmount + " for " + getMonthName(month) + " " + year + " has been generated.");
            notification.setType(Notification.NotificationType.MAINTENANCE_DUE);
            notification.setIsRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);

            billsCreated++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalExpense", totalExpense);
        result.put("perFlatAmount", perFlatAmount);
        result.put("totalFlats", residents.size());
        result.put("billsCreated", billsCreated);
        result.put("month", getMonthName(month));
        result.put("year", year);

        return result;
    }

    private String getMonthName(int month) {
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        return months[month - 1];
    }

    private Map<String, Object> convertToMap(Expense expense) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", expense.getId());
        map.put("type", expense.getType());
        map.put("typeLabel", expense.getType().toString());
        map.put("amount", expense.getAmount());
        map.put("date", expense.getDate());
        map.put("note", expense.getNote());
        map.put("billImage", expense.getBillImage());
        map.put("createdAt", expense.getCreatedAt());
        return map;
    }
}
