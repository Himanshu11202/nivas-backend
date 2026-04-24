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
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FlatRepository flatRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    // Dashboard Statistics
    public Map<String, Object> getDashboardStats() {
        long totalResidents = userRepository.countByRole(User.Role.RESIDENT);
        long totalFlats = flatRepository.countTotalFlats();
        long pendingApprovals = userRepository.countByStatus(User.UserStatus.PENDING);
        long pendingComplaints = complaintRepository.countByStatus(Complaint.ComplaintStatus.PENDING);
        BigDecimal totalCollection = maintenanceRepository.sumPaidMaintenance();
        long todayVisitors = visitorRepository.countVisitorsBetween(
                LocalDateTime.now().toLocalDate().atStartOfDay(),
                LocalDateTime.now()
        );
        long totalWorkers = workerRepository.countByStatus(Worker.WorkerStatus.ACTIVE);
        LocalDate today = LocalDate.now();
        long workersPresentToday = attendanceRepository.countPresentByDate(today);

        return Map.of(
                "totalResidents", totalResidents,
                "totalFlats", totalFlats,
                "pendingApprovals", pendingApprovals,
                "pendingComplaints", pendingComplaints,
                "totalCollection", totalCollection != null ? totalCollection : BigDecimal.ZERO,
                "todayVisitors", todayVisitors,
                "totalWorkers", totalWorkers,
                "workersPresentToday", workersPresentToday
        );
    }

    // User Management
    public List<User> getPendingResidents() {
        return userRepository.findByStatus(User.UserStatus.PENDING);
    }

    public User approveResident(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(User.UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    public User rejectResident(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(User.UserStatus.REJECTED);
        return userRepository.save(user);
    }

    public List<User> getAllResidents() {
        return userRepository.findByRole(User.Role.RESIDENT);
    }

    // Flat Management
    public Flat createFlat(Flat flat) {
        return flatRepository.save(flat);
    }

    public Flat updateFlat(Long id, Flat flatDetails) {
        Flat flat = flatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flat not found"));
        flat.setFlatNumber(flatDetails.getFlatNumber());
        flat.setWing(flatDetails.getWing());
        flat.setFloor(flatDetails.getFloor());
        return flatRepository.save(flat);
    }

    public void deleteFlat(Long id) {
        flatRepository.deleteById(id);
    }

    public List<Flat> getAllFlats() {
        return flatRepository.findAll();
    }

    // Visitor Management - MOVED to VisitorController
    // public List<Visitor> getAllVisitors() {
    //     return visitorRepository.findAll();
    // }

    // Complaint Management - MOVED to ComplaintController
    // public List<Complaint> getAllComplaints() {
    //     return complaintRepository.findAllOrderByCreatedAtDesc();
    // }

    public Complaint updateComplaintStatus(Long complaintId, Complaint.ComplaintStatus status) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        complaint.setStatus(status);
        if (status == Complaint.ComplaintStatus.RESOLVED) {
            complaint.setResolvedAt(LocalDateTime.now());
        }
        return complaintRepository.save(complaint);
    }

    // Worker Management
    public Worker createWorker(Worker worker) {
        return workerRepository.save(worker);
    }

    public Worker updateWorker(Long id, Worker workerDetails) {
        Worker worker = workerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Worker not found"));
        worker.setName(workerDetails.getName());
        worker.setPhoneNumber(workerDetails.getPhoneNumber());
        worker.setJobRole(workerDetails.getJobRole());
        worker.setStatus(workerDetails.getStatus());
        return workerRepository.save(worker);
    }

    public void deleteWorker(Long id) {
        workerRepository.deleteById(id);
    }

    // Worker Management - MOVED to WorkerController
    // public List<Worker> getAllWorkers() {
    //     return workerRepository.findAll();
    // }

    // Notice Management - MOVED to NoticeController
    // public List<Notice> getAllNotices() {
    //     // This will be implemented with NoticeService
    //     return List.of();
    // }

    // Attendance Report
    public Map<String, Object> getAttendanceReport() {
        LocalDate today = LocalDate.now();
        long totalWorkers = workerRepository.countByStatus(Worker.WorkerStatus.ACTIVE);
        long presentToday = attendanceRepository.countPresentByDate(today);
        long absentToday = attendanceRepository.countAbsentByDate(today);

        return Map.of(
                "totalWorkers", totalWorkers,
                "presentToday", presentToday,
                "absentToday", absentToday
        );
    }

    // Maintenance Management
    public void generateMonthlyMaintenance(Integer month, Integer year, BigDecimal amount) {
        List<User> residents = userRepository.findByRole(User.Role.RESIDENT);
        
        for (User resident : residents) {
            if (resident.getStatus() == User.UserStatus.ACTIVE) {
                Maintenance maintenance = new Maintenance();
                maintenance.setAmount(amount);
                maintenance.setDueDate(LocalDate.of(year, month, 1).plusMonths(1).minusDays(1));
                maintenance.setUserId(resident.getId());
                maintenance.setMonth(month);
                maintenance.setYear(year);
                maintenanceRepository.save(maintenance);

                // Create notification
                Notification notification = new Notification();
                notification.setUserId(resident.getId());
                notification.setMessage("Monthly maintenance of ₹" + amount + " is due for " + month + "/" + year);
                notification.setType(Notification.NotificationType.MAINTENANCE_REMINDER);
                notificationRepository.save(notification);
            }
        }
    }

    public Map<String, Object> getPaymentReports() {
        BigDecimal totalPaid = maintenanceRepository.sumPaidMaintenance();
        BigDecimal totalPending = maintenanceRepository.sumPendingMaintenance();
        long paidCount = maintenanceRepository.countByStatus(Maintenance.PaymentStatus.PAID);
        long pendingCount = maintenanceRepository.countByStatus(Maintenance.PaymentStatus.PENDING);

        return Map.of(
                "totalPaid", totalPaid != null ? totalPaid : BigDecimal.ZERO,
                "totalPending", totalPending != null ? totalPending : BigDecimal.ZERO,
                "paidCount", paidCount,
                "pendingCount", pendingCount
        );
    }
}
