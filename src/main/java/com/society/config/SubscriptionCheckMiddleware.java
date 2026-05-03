package com.society.config;

import com.society.entity.Society;
import com.society.entity.User;
import com.society.repository.SocietyRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class SubscriptionCheckMiddleware implements HandlerInterceptor {

    @Autowired
    private SocietyRepository societyRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        
        // Skip subscription check for:
        // 1. Public endpoints (login, register, health)
        // 2. Super Admin endpoints
        // 3. Static resources
        if (path.startsWith("/api/auth") || 
            path.startsWith("/api/super-admin") ||
            path.startsWith("/error") ||
            path.startsWith("/css") ||
            path.startsWith("/js") ||
            path.startsWith("/images")) {
            return true;
        }

        // Check subscription for SOCIETY_ADMIN, RESIDENT, GUARD, WORKER
        // Get user from request attribute (set by authentication filter)
        Object userObj = request.getAttribute("user");
        if (userObj instanceof User) {
            User user = (User) userObj;
            
            // Skip check for SUPER_ADMIN and regular ADMIN
            if (user.getRole() == User.Role.SUPER_ADMIN || user.getRole() == User.Role.ADMIN) {
                return true;
            }
            
            // Check subscription for society-based roles
            if (user.getSocietyId() != null) {
                Society society = societyRepository.findById(user.getSocietyId()).orElse(null);
                
                if (society == null) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"Society not found\"}");
                    return false;
                }
                
                // Check if subscription is expired or blocked
                if (society.getSubscriptionStatus() == Society.SubscriptionStatus.EXPIRED ||
                    society.getSubscriptionStatus() == Society.SubscriptionStatus.BLOCKED ||
                    (society.getSubscriptionExpiryDate() != null && society.getSubscriptionExpiryDate().isBefore(LocalDateTime.now()))) {
                    
                    // Update status if expired but not marked as expired
                    if (society.getSubscriptionExpiryDate() != null && 
                        society.getSubscriptionExpiryDate().isBefore(LocalDateTime.now()) &&
                        society.getSubscriptionStatus() != Society.SubscriptionStatus.EXPIRED) {
                        society.setSubscriptionStatus(Society.SubscriptionStatus.EXPIRED);
                        societyRepository.save(society);
                    }
                    
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Subscription expired or blocked\", \"subscriptionStatus\": \"" + society.getSubscriptionStatus() + "\"}");
                    return false;
                }
            }
        }
        
        return true;
    }
}
