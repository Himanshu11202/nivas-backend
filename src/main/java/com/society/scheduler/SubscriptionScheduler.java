package com.society.scheduler;

import com.society.entity.Society;
import com.society.repository.SocietyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class SubscriptionScheduler {

    @Autowired
    private SocietyRepository societyRepository;

    // Run daily at 2 AM to check for expired subscriptions
    @Scheduled(cron = "0 0 2 * * ?")
    public void checkExpiredSubscriptions() {
        System.out.println("Running scheduled check for expired subscriptions...");
        
        List<Society> societies = societyRepository.findAll();
        int blockedCount = 0;
        
        for (Society society : societies) {
            // Check if subscription is expired and society is still active
            if (society.getSubscriptionExpiryDate() != null && 
                society.getSubscriptionExpiryDate().isBefore(LocalDateTime.now()) &&
                society.getSubscriptionStatus() == Society.SubscriptionStatus.ACTIVE) {
                
                // Block the society
                society.setSubscriptionStatus(Society.SubscriptionStatus.EXPIRED);
                societyRepository.save(society);
                blockedCount++;
                
                System.out.println("Blocked society: " + society.getName() + 
                    " (Subscription expired on: " + society.getSubscriptionExpiryDate() + ")");
            }
        }
        
        System.out.println("Subscription check completed. Blocked " + blockedCount + " societies.");
    }

    // Run daily at 3 AM to add pending maintenance payments
    @Scheduled(cron = "0 0 3 * * ?")
    public void calculatePendingPayments() {
        System.out.println("Running scheduled calculation for pending maintenance payments...");
        
        List<Society> societies = societyRepository.findAll();
        
        for (Society society : societies) {
            // If society is active, add monthly maintenance to pending payments
            if (society.getSubscriptionStatus() == Society.SubscriptionStatus.ACTIVE) {
                Double maintenanceAmount = society.getMaintenanceAmount() != null ? 
                    society.getMaintenanceAmount() : 1000.0;
                
                Double currentPending = society.getPendingPayments() != null ? 
                    society.getPendingPayments() : 0.0;
                
                society.setPendingPayments(currentPending + maintenanceAmount);
                society.setMaintenanceDueDate(LocalDateTime.now().plusDays(30));
                societyRepository.save(society);
                
                System.out.println("Added maintenance payment for society: " + society.getName() + 
                    " (Amount: ₹" + maintenanceAmount + ")");
            }
        }
        
        System.out.println("Pending payment calculation completed.");
    }
}
