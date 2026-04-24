package com.society.service;

import com.society.entity.*;
import com.society.entity.Notification;
import com.society.entity.Guard;
import com.society.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GuardService {

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuardAttendanceRepository guardAttendanceRepository;

    // Guard Management
    public Guard createGuard(Guard guard) {
        try {
            System.out.println("Creating guard with data: " + guard.getName() + ", " + guard.getEmail());
            
            guard.setCreatedAt(LocalDateTime.now());
            guard.setRole(User.Role.GUARD);
            guard.setStatus(User.UserStatus.ACTIVE);
            
            // Hash password before saving
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(guard.getPassword());
            guard.setPassword(hashedPassword);
            
            System.out.println("Guard before save: " + guard.getName() + ", role: " + guard.getRole());
            
            Guard savedGuard = userRepository.save(guard);
            System.out.println("Guard saved successfully with ID: " + savedGuard.getId());
            
            return savedGuard;
        } catch (Exception e) {
            System.err.println("Error creating guard: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create guard: " + e.getMessage(), e);
        }
    }

    public List<Guard> getAllGuards() {
        try {
            System.out.println("Fetching all guards...");
            List<User> guardUsers = userRepository.findByRole(User.Role.GUARD);
            System.out.println("Found " + guardUsers.size() + " guard users");
            
            return guardUsers.stream()
                    .map(user -> {
                        Guard guard = new Guard();
                        guard.setId(user.getId());
                        guard.setName(user.getName());
                        guard.setEmail(user.getEmail());
                        guard.setPassword(user.getPassword());
                        guard.setPhoneNumber(user.getPhoneNumber());
                        guard.setRole(user.getRole());
                        guard.setStatus(user.getStatus());
                        guard.setCreatedAt(user.getCreatedAt());
                        // Cast to Guard to access Guard-specific fields
                        if (user instanceof Guard) {
                            Guard guardUser = (Guard) user;
                            guard.setAddress(guardUser.getAddress());
                            guard.setShift(guardUser.getShift());
                            guard.setSalary(guardUser.getSalary());
                            guard.setHireDate(guardUser.getHireDate());
                        }
                        return guard;
                    })
                    .toList();  
        } catch (Exception e) {
            System.err.println("Error fetching guards: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public Guard getGuardById(Long id) {
        User user = userRepository.findById(id)
                .filter(u -> u.getRole() == User.Role.GUARD)
                .orElseThrow(() -> new RuntimeException("Guard not found"));
        
        // Convert User to Guard properly
        Guard guard = new Guard();
        guard.setId(user.getId());
        guard.setName(user.getName());
        guard.setEmail(user.getEmail());
        guard.setPassword(user.getPassword());
        guard.setPhoneNumber(user.getPhoneNumber());
        guard.setRole(user.getRole());
        guard.setStatus(user.getStatus());
        guard.setCreatedAt(user.getCreatedAt());
        // Cast to Guard to access Guard-specific fields
        if (user instanceof Guard) {
            Guard guardUser = (Guard) user;
            guard.setAddress(guardUser.getAddress());
            guard.setShift(guardUser.getShift());
            guard.setSalary(guardUser.getSalary());
            guard.setHireDate(guardUser.getHireDate());
        }
        
        return guard;
    }

    public Guard updateGuard(Long id, Guard guardDetails) {
        Guard guard = getGuardById(id);
        guard.setName(guardDetails.getName());
        guard.setEmail(guardDetails.getEmail());
        guard.setPhoneNumber(guardDetails.getPhoneNumber());
        guard.setAddress(guardDetails.getAddress());
        guard.setShift(guardDetails.getShift());
        guard.setSalary(guardDetails.getSalary());
        return userRepository.save(guard);
    }

    public void deleteGuard(Long id) {
        Guard guard = getGuardById(id);
        userRepository.delete(guard);
    }

    // Visitor Management
    public Visitor addVisitor(String name, String phone, String flatNumber, String purpose, String vehicleNumber, String visitorPhoto) {
        Visitor visitor = new Visitor();
        visitor.setVisitorName(name);
        visitor.setVisitorPhone(phone);
        visitor.setFlatNumber(flatNumber);
        visitor.setPurpose(purpose);
        visitor.setVehicleNumber(vehicleNumber);
        visitor.setVisitorPhoto(visitorPhoto);
        visitor.setStatus(Visitor.VisitorStatus.PENDING);
        
        Visitor savedVisitor = visitorRepository.save(visitor);
        
        // Create notification for resident
        List<User> residents = userRepository.findByFlatNumber(flatNumber);
        for (User resident : residents) {
            if (resident.getStatus() == User.UserStatus.ACTIVE) {
                Notification notification = new Notification();
                notification.setUserId(resident.getId());
                notification.setMessage("Visitor " + visitor.getVisitorName() + " is requesting entry.");
                notification.setType(Notification.NotificationType.VISITOR_APPROVAL);
                notificationRepository.save(notification);
            }
        }
        
        return savedVisitor;
    }

    public Visitor recordEntry(Long visitorId) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new RuntimeException("Visitor not found"));
        
        visitor.setStatus(Visitor.VisitorStatus.ENTERED);
        visitor.setEntryTime(LocalDateTime.now());
        
        return visitorRepository.save(visitor);
    }

    public Visitor recordExit(Long visitorId) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new RuntimeException("Visitor not found"));
        
        visitor.setStatus(Visitor.VisitorStatus.EXITED);
        visitor.setExitTime(LocalDateTime.now());
        
        return visitorRepository.save(visitor);
    }

    public List<Visitor> getVisitorHistory() {
        return visitorRepository.findAll();
    }

    public List<Visitor> getPendingVisitors() {
        return visitorRepository.findByStatus(Visitor.VisitorStatus.PENDING);
    }

    // Emergency Alert
    public void sendEmergencyAlert(String message) {
        List<User> admins = userRepository.findByRole(User.Role.ADMIN);
        
        for (User admin : admins) {
            Notification notification = new Notification();
            notification.setUserId(admin.getId());
            notification.setMessage("EMERGENCY: " + message);
            notification.setType(Notification.NotificationType.GENERAL);
            notificationRepository.save(notification);
        }
    }

    // Guard Attendance Management
    public GuardAttendance markGuardAttendance(Long guardId, LocalDate date, GuardAttendance.AttendanceStatus status, 
                                                LocalDateTime checkInTime, String checkInPhoto, String notes, String markedBy) {
        Guard guard = getGuardById(guardId);
        
        GuardAttendance attendance = guardAttendanceRepository.findByGuardIdAndDate(guardId, date)
                .orElse(new GuardAttendance());
        
        attendance.setGuard(guard);
        attendance.setDate(date);
        attendance.setStatus(status);
        
        if (status == GuardAttendance.AttendanceStatus.PRESENT && checkInTime != null) {
            attendance.setCheckInTime(checkInTime);
            attendance.setCheckInPhoto(checkInPhoto);
        }
        
        attendance.setNotes(notes);
        attendance.setMarkedBy(markedBy);
        
        return guardAttendanceRepository.save(attendance);
    }

    public GuardAttendance markGuardCheckOut(Long guardId, LocalDate date, LocalDateTime checkOutTime, String checkOutPhoto) {
        GuardAttendance attendance = guardAttendanceRepository.findByGuardIdAndDate(guardId, date)
                .orElseThrow(() -> new RuntimeException("Attendance not found for this guard and date"));
        
        attendance.setCheckOutTime(checkOutTime);
        attendance.setCheckOutPhoto(checkOutPhoto);
        
        return guardAttendanceRepository.save(attendance);
    }

    public List<GuardAttendance> getGuardAttendance(Long guardId) {
        return guardAttendanceRepository.findByGuardIdOrderByDateDesc(guardId);
    }

    public List<GuardAttendance> getTodayGuardAttendance() {
        return guardAttendanceRepository.findByDate(LocalDate.now());
    }

    public List<Guard> getActiveGuards() {
        return getAllGuards().stream()
                .filter(guard -> guard.getStatus() == User.UserStatus.ACTIVE)
                .toList();
    }

    public Map<String, Object> getGuardAttendanceStats() {
        LocalDate today = LocalDate.now();
        long totalGuards = getAllGuards().size();
        long presentToday = guardAttendanceRepository.countByDateAndStatus(today, GuardAttendance.AttendanceStatus.PRESENT);
        long absentToday = guardAttendanceRepository.countByDateAndStatus(today, GuardAttendance.AttendanceStatus.ABSENT);

        return Map.of(
                "totalGuards", totalGuards,
                "presentToday", presentToday,
                "absentToday", absentToday,
                "attendanceRate", totalGuards > 0 ? (presentToday * 100.0 / totalGuards) : 0.0
        );
    }
}
