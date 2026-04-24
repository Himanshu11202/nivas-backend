package com.society.service;

import com.society.entity.*;
import com.society.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class MaintenanceService {

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // Maintenance Management
    public Maintenance createMaintenance(Maintenance maintenance) {
        maintenance.setCreatedAt(LocalDateTime.now());
        maintenance.setStatus(Maintenance.PaymentStatus.PENDING);
        
        Maintenance savedMaintenance = maintenanceRepository.save(maintenance);
        
        // Send notification to resident
        sendMaintenanceNotification(savedMaintenance);
        
        return savedMaintenance;
    }

    public Maintenance updateMaintenance(Long id, Maintenance maintenanceDetails) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance not found"));
        
        maintenance.setAmount(maintenanceDetails.getAmount());
        maintenance.setDueDate(maintenanceDetails.getDueDate());
        maintenance.setMonth(maintenanceDetails.getMonth());
        maintenance.setYear(maintenanceDetails.getYear());
        
        return maintenanceRepository.save(maintenance);
    }

    public void deleteMaintenance(Long id) {
        maintenanceRepository.deleteById(id);
    }

    public List<Maintenance> getAllMaintenance() {
        return maintenanceRepository.findAll();
    }

    public List<Maintenance> getMaintenanceByUserId(Long userId) {
        return maintenanceRepository.findByUserIdOrderByDueDateDesc(userId);
    }

    public Maintenance payMaintenance(Long id) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance not found"));
        
        maintenance.setStatus(Maintenance.PaymentStatus.PAID);
        maintenance.setPaidAt(LocalDateTime.now());
        
        Maintenance paidMaintenance = maintenanceRepository.save(maintenance);
        
        // Send payment confirmation notification
        sendPaymentConfirmationNotification(paidMaintenance);
        
        return paidMaintenance;
    }

    // Send maintenance reminder notification
    private void sendMaintenanceNotification(Maintenance maintenance) {
        Notification notification = new Notification();
        notification.setUserId(maintenance.getUserId());
        notification.setMessage("New maintenance bill generated for " + maintenance.getMonth() + "/" + maintenance.getYear());
        notification.setType(Notification.NotificationType.MAINTENANCE_DUE);
        notification.setIsRead(false);
        
        notificationRepository.save(notification);
    }

    // Send payment confirmation notification
    private void sendPaymentConfirmationNotification(Maintenance maintenance) {
        Notification notification = new Notification();
        notification.setUserId(maintenance.getUserId());
        notification.setMessage("Payment confirmed for maintenance " + maintenance.getMonth() + "/" + maintenance.getYear());
        notification.setType(Notification.NotificationType.PAYMENT_CONFIRMATION);
        notification.setIsRead(false);
        
        notificationRepository.save(notification);
    }

    // Dashboard Statistics
    public Map<String, Object> getMaintenanceStats() {
        long totalMaintenance = maintenanceRepository.count();
        long paidMaintenance = maintenanceRepository.countByStatus(Maintenance.PaymentStatus.PAID);
        long pendingMaintenance = maintenanceRepository.countByStatus(Maintenance.PaymentStatus.PENDING);
        BigDecimal totalAmount = maintenanceRepository.sumPaidMaintenance();
        
        return Map.of(
                "totalMaintenance", totalMaintenance,
                "paidMaintenance", paidMaintenance,
                "pendingMaintenance", pendingMaintenance,
                "totalAmount", totalAmount != null ? totalAmount : BigDecimal.ZERO
        );
    }

    // Generate monthly maintenance for all residents
    public void generateMonthlyMaintenance(int year, int month, BigDecimal amount) {
        List<User> residents = userRepository.findByRole(User.Role.RESIDENT);
        
        for (User resident : residents) {
            // Check if maintenance already exists for this user, month, year
            if (!maintenanceRepository.existsByUserIdAndMonthAndYear(resident.getId(), month, year)) {
                Maintenance maintenance = new Maintenance();
                maintenance.setUserId(resident.getId());
                maintenance.setAmount(amount);
                maintenance.setDueDate(LocalDate.of(year, month, 1).plusMonths(1));
                maintenance.setMonth(month);
                maintenance.setYear(year);
                
                maintenanceRepository.save(maintenance);
                
                // Send notification
                sendMaintenanceNotification(maintenance);
            }
        }
    }

    // Check overdue maintenance
    public List<Maintenance> getOverdueMaintenance() {
        return maintenanceRepository.findByDueDateBefore(LocalDate.now());
    }

    // Check overdue maintenance and send notifications
    public void checkOverdueMaintenance() {
        List<Maintenance> overdueMaintenances = getOverdueMaintenance();
        
        for (Maintenance maintenance : overdueMaintenances) {
            if (maintenance.getStatus() == Maintenance.PaymentStatus.PENDING) {
                // Send overdue notification
                Notification notification = new Notification();
                notification.setUserId(maintenance.getUserId());
                notification.setMessage("Your maintenance payment for " + maintenance.getMonth() + "/" + maintenance.getYear() + " is overdue!");
                notification.setType(Notification.NotificationType.OVERDUE_REMINDER);
                notification.setIsRead(false);
                
                notificationRepository.save(notification);
            }
        }
    }

    // Get maintenance report
    public Map<String, Object> getMaintenanceReport(int year, int month) {
        List<Maintenance> maintenanceList = maintenanceRepository.findByMonthAndYear(month, year);
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal paidAmount = BigDecimal.ZERO;
        long paidCount = 0;
        
        for (Maintenance maintenance : maintenanceList) {
            totalAmount = totalAmount.add(maintenance.getAmount());
            if (maintenance.getStatus() == Maintenance.PaymentStatus.PAID) {
                paidAmount = paidAmount.add(maintenance.getAmount());
                paidCount++;
            }
        }
        
        return Map.of(
                "maintenanceList", maintenanceList,
                "totalAmount", totalAmount,
                "paidAmount", paidAmount,
                "pendingAmount", totalAmount.subtract(paidAmount),
                "paidCount", paidCount,
                "totalCount", maintenanceList.size(),
                "collectionRate", maintenanceList.size() > 0 ? (paidCount * 100.0 / maintenanceList.size()) : 0
        );
    }

    public Map<String, Object> getMonthlyReport(int year, int month) {
        return getMaintenanceReport(year, month);
    }
}
