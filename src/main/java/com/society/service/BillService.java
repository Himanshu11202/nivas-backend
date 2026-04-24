package com.society.service;

import com.society.entity.Bill;
import com.society.entity.Notification;
import com.society.entity.User;
import com.society.repository.BillRepository;
import com.society.repository.NotificationRepository;
import com.society.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    // Get all bills for a user
    public List<Bill> getBillsByUserId(Long userId) {
        return billRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // Get all bills (for admin)
    public List<Bill> getAllBills() {
        return billRepository.findAllByOrderByCreatedAtDesc();
    }

    // Get bills by flat number
    public List<Bill> getBillsByFlatNumber(String flatNumber) {
        return billRepository.findByFlatNumberOrderByCreatedAtDesc(flatNumber);
    }

    // Search bills by flat number
    public List<Bill> searchBillsByFlatNumber(String search) {
        return billRepository.searchByFlatNumber(search);
    }

    // Get bill by ID
    public Bill getBillById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
    }

    // Mark bill as paid (dummy payment)
    @Transactional
    public Bill markAsPaid(Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
        
        if (bill.getStatus() == Bill.BillStatus.PAID) {
            throw new RuntimeException("Bill is already paid");
        }
        
        bill.setStatus(Bill.BillStatus.PAID);
        bill.setPaidAt(LocalDateTime.now());
        
        // Send notification to user
        Notification notification = new Notification();
        notification.setUserId(bill.getUser().getId());
        notification.setMessage("Maintenance payment of ₹" + bill.getAmount() + " received for " + bill.getMonth() + "/" + bill.getYear());
        notification.setType(Notification.NotificationType.PAYMENT_CONFIRMATION);
        notification.setIsRead(false);
        notificationRepository.save(notification);
        
        return billRepository.save(bill);
    }

    // Send maintenance to all residents
    @Transactional
    public Map<String, Object> sendMaintenanceToAll(BigDecimal amount, String description, Integer month, Integer year) {
        // If month/year not provided, use current month/year
        if (month == null) {
            month = LocalDateTime.now().getMonthValue();
        }
        if (year == null) {
            year = LocalDateTime.now().getYear();
        }
        
        List<User> residents = userRepository.findByRole(User.Role.RESIDENT);
        
        int billsCreated = 0;
        int notificationsSent = 0;
        
        for (User resident : residents) {
            // Check if bill already exists for this month/year
            Optional<Bill> existingBill = billRepository.findByUserIdAndMonthAndYear(
                resident.getId(), month, year
            );
            
            if (existingBill.isPresent()) {
                continue; // Skip if bill already exists
            }
            
            // Create new bill
            Bill bill = new Bill();
            bill.setUser(resident);
            bill.setFlatNumber(resident.getFlatNumber() != null ? resident.getFlatNumber() : "N/A");
            bill.setAmount(amount);
            bill.setMonth(month);
            bill.setYear(year);
            bill.setDescription(description != null ? description : "Monthly Maintenance");
            bill.setStatus(Bill.BillStatus.UNPAID);
            billRepository.save(bill);
            billsCreated++;
            
            // Send notification
            Notification notification = new Notification();
            notification.setUserId(resident.getId());
            notification.setMessage("Monthly Maintenance Due: ₹" + amount + " for " + month + "/" + year);
            notification.setType(Notification.NotificationType.MAINTENANCE_DUE);
            notification.setIsRead(false);
            notificationRepository.save(notification);
            notificationsSent++;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("billsCreated", billsCreated);
        result.put("notificationsSent", notificationsSent);
        result.put("month", month);
        result.put("year", year);
        result.put("amount", amount);
        
        return result;
    }

    // Get bill statistics
    public Map<String, Object> getBillStats() {
        Long totalBills = billRepository.count();
        Long paidBills = billRepository.countByStatus(Bill.BillStatus.PAID);
        Long unpaidBills = billRepository.countByStatus(Bill.BillStatus.UNPAID);
        
        // Calculate total collected amount
        List<Bill> paidBillList = billRepository.findByStatusOrderByCreatedAtDesc(Bill.BillStatus.PAID);
        BigDecimal totalCollected = paidBillList.stream()
                .map(Bill::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBills", totalBills);
        stats.put("paidBills", paidBills);
        stats.put("unpaidBills", unpaidBills);
        stats.put("totalCollected", totalCollected);
        stats.put("collectionRate", totalBills > 0 ? (paidBills * 100 / totalBills) : 0);
        
        return stats;
    }

    // Get user's bill statistics
    public Map<String, Object> getUserBillStats(Long userId) {
        Long totalBills = billRepository.countByUserIdAndStatus(userId, Bill.BillStatus.PAID) 
                        + billRepository.countByUserIdAndStatus(userId, Bill.BillStatus.UNPAID);
        Long paidBills = billRepository.countByUserIdAndStatus(userId, Bill.BillStatus.PAID);
        Long unpaidBills = billRepository.countByUserIdAndStatus(userId, Bill.BillStatus.UNPAID);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalBills", totalBills);
        stats.put("paidBills", paidBills);
        stats.put("unpaidBills", unpaidBills);
        
        return stats;
    }

    // Generate receipt data
    public Map<String, Object> generateReceipt(Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
        
        Map<String, Object> receipt = new HashMap<>();
        receipt.put("billId", bill.getId());
        receipt.put("residentName", bill.getUser().getName());
        receipt.put("flatNumber", bill.getFlatNumber());
        receipt.put("amount", bill.getAmount());
        receipt.put("month", bill.getMonth());
        receipt.put("year", bill.getYear());
        receipt.put("status", bill.getStatus());
        receipt.put("paymentDate", bill.getPaidAt());
        receipt.put("billDate", bill.getCreatedAt());
        receipt.put("description", bill.getDescription());
        
        return receipt;
    }

    // ==================== PENALTY SYSTEM ====================
    
    private static final BigDecimal PENALTY_AMOUNT = new BigDecimal("100");
    private static final int PENALTY_DAYS = 6;
    
    // Check and apply penalty for overdue bills
    @Transactional
    public void checkAndApplyPenalties() {
        List<Bill> unpaidBills = billRepository.findByStatusOrderByCreatedAtDesc(Bill.BillStatus.UNPAID);
        
        for (Bill bill : unpaidBills) {
            if (shouldApplyPenalty(bill)) {
                applyPenalty(bill);
            }
        }
    }
    
    // Check if penalty should be applied (after 6 days)
    private boolean shouldApplyPenalty(Bill bill) {
        if (bill.getStatus() != Bill.BillStatus.UNPAID) {
            return false;
        }
        
        LocalDateTime createdAt = bill.getCreatedAt();
        if (createdAt == null) {
            return false;
        }
        
        long daysSinceCreated = ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
        return daysSinceCreated >= PENALTY_DAYS;
    }
    
    // Apply penalty to a bill
    @Transactional
    public void applyPenalty(Bill bill) {
        // Check if penalty already applied (amount already includes penalty)
        // We'll track this by checking if description mentions penalty
        if (bill.getDescription() != null && bill.getDescription().contains("(Includes ₹100 Penalty)")) {
            return; // Penalty already applied
        }
        
        // Add penalty amount
        BigDecimal newAmount = bill.getAmount().add(PENALTY_AMOUNT);
        bill.setAmount(newAmount);
        bill.setDescription(bill.getDescription() + " (Includes ₹100 Penalty)");
        billRepository.save(bill);
        
        // Send notification to user
        Notification notification = new Notification();
        notification.setUserId(bill.getUser().getId());
        notification.setMessage("Penalty of ₹100 added to your maintenance bill for " + bill.getMonth() + "/" + bill.getYear() + " due to late payment. New amount: ₹" + newAmount);
        notification.setType(Notification.NotificationType.OVERDUE_REMINDER);
        notification.setIsRead(false);
        notificationRepository.save(notification);
    }
    
    // Get bills with penalty info
    public List<Map<String, Object>> getBillsWithPenaltyInfo(Long userId) {
        List<Bill> bills = getBillsByUserId(userId);
        
        return bills.stream().map(bill -> {
            Map<String, Object> billInfo = new HashMap<>();
            billInfo.put("id", bill.getId());
            billInfo.put("amount", bill.getAmount());
            billInfo.put("originalAmount", bill.getDescription() != null && bill.getDescription().contains("(Includes ₹100 Penalty)") 
                    ? bill.getAmount().subtract(PENALTY_AMOUNT) : bill.getAmount());
            billInfo.put("hasPenalty", bill.getDescription() != null && bill.getDescription().contains("(Includes ₹100 Penalty)"));
            billInfo.put("penaltyAmount", bill.getDescription() != null && bill.getDescription().contains("(Includes ₹100 Penalty)") ? PENALTY_AMOUNT : BigDecimal.ZERO);
            billInfo.put("month", bill.getMonth());
            billInfo.put("year", bill.getYear());
            billInfo.put("status", bill.getStatus());
            billInfo.put("createdAt", bill.getCreatedAt());
            billInfo.put("paidAt", bill.getPaidAt());
            billInfo.put("description", bill.getDescription());
            billInfo.put("flatNumber", bill.getFlatNumber());
            
            // Calculate days remaining for penalty
            if (bill.getStatus() == Bill.BillStatus.UNPAID && bill.getCreatedAt() != null) {
                long daysSinceCreated = ChronoUnit.DAYS.between(bill.getCreatedAt(), LocalDateTime.now());
                long daysUntilPenalty = PENALTY_DAYS - daysSinceCreated;
                billInfo.put("daysUntilPenalty", daysUntilPenalty > 0 ? daysUntilPenalty : 0);
                billInfo.put("daysOverdue", daysSinceCreated > PENALTY_DAYS ? daysSinceCreated - PENALTY_DAYS : 0);
            }
            
            return billInfo;
        }).collect(Collectors.toList());
    }
}
