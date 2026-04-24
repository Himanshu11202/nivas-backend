package com.society.service;

import com.society.entity.*;
import com.society.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.validation.constraints.NotNull;

@Service
public class WorkerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    // Worker Management
    public Worker createWorker(Worker worker) {
        worker.setCreatedAt(LocalDateTime.now());
        // Auto-generate password for workers (they don't login)
        if (worker.getPassword() == null || worker.getPassword().isEmpty()) {
            worker.setPassword(java.util.UUID.randomUUID().toString().substring(0, 8));
        }
        // Auto-generate email if not provided
        if (worker.getEmail() == null || worker.getEmail().isEmpty()) {
            worker.setEmail("worker" + System.currentTimeMillis() + "@society.local");
        }
        return userRepository.save(worker);
    }
    
    public Worker saveWorker(Worker worker) {
        if (worker.getId() == null) {
            worker.setCreatedAt(LocalDateTime.now());
            // Auto-generate password for new workers
            if (worker.getPassword() == null || worker.getPassword().isEmpty()) {
                worker.setPassword(java.util.UUID.randomUUID().toString().substring(0, 8));
            }
            // Auto-generate email if not provided
            if (worker.getEmail() == null || worker.getEmail().isEmpty()) {
                worker.setEmail("worker" + System.currentTimeMillis() + "@society.local");
            }
        }
        return userRepository.save(worker);
    }

    public List<Worker> getAllWorkers() {
        return userRepository.findByRole(User.Role.WORKER).stream()
                .map(user -> (Worker) user)
                .toList();
    }

    public Worker getWorkerById(@NotNull Long id) {
        return userRepository.findById(id)
                .filter(user -> user.getRole() == User.Role.WORKER)
                .map(user -> (Worker) user)
                .orElseThrow(() -> new RuntimeException("Worker not found"));
    }

    public Worker updateWorker(@NotNull Long id, Worker workerDetails) {
        Worker worker = getWorkerById(id);
        worker.setName(workerDetails.getName());
        worker.setPhoneNumber(workerDetails.getPhoneNumber());
        worker.setJobRole(workerDetails.getJobRole());
        return userRepository.save(worker);
    }

    public void deleteWorker(@NotNull Long id) {
        Worker worker = getWorkerById(id);
        userRepository.delete(worker);
    }

    public List<Worker> getActiveWorkers() {
        return userRepository.findByRole(User.Role.WORKER).stream()
                .filter(user -> user.getStatus() == User.UserStatus.ACTIVE)
                .map(user -> (Worker) user)
                .toList();
    }

    // Attendance Management
    public Attendance markAttendance(@NotNull Long workerId, LocalDate date, LocalDateTime checkInTime, String workerPhoto) {
        Attendance attendance = attendanceRepository.findByWorkerIdAndDate(workerId, date);
        
        if (attendance == null) {
            attendance = new Attendance();
            Worker worker = getWorkerById(workerId);
            attendance.setWorker(worker);
            attendance.setDate(date);
        }
        
        attendance.setCheckInTime(checkInTime);
        attendance.setWorkerPhoto(workerPhoto);
        
        return attendanceRepository.save(attendance);
    }

    public Attendance markCheckOut(@NotNull Long workerId, LocalDate date, LocalDateTime checkOutTime) {
        Attendance attendance = attendanceRepository.findByWorkerIdAndDate(workerId, date);
        
        if (attendance == null) {
            throw new RuntimeException("Attendance not found for this worker and date");
        }
        
        attendance.setCheckOutTime(checkOutTime);
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getWorkerAttendance(@NotNull Long workerId) {
        return attendanceRepository.findByWorkerIdOrderByDateDesc(workerId);
    }

    public List<Attendance> getTodayAttendance() {
        return attendanceRepository.findByDate(LocalDate.now());
    }

    // Enhanced Dashboard Statistics
    public Map<String, Object> getAttendanceStats() {
        LocalDate today = LocalDate.now();
        long totalWorkers = userRepository.countByRole(User.Role.WORKER);
        long presentToday = attendanceRepository.countPresentByDate(today);
        long absentToday = attendanceRepository.countAbsentByDate(today);

        return Map.of(
                "totalWorkers", totalWorkers,
                "presentToday", presentToday,
                "absentToday", absentToday,
                "attendanceRate", totalWorkers > 0 ? (presentToday * 100.0 / totalWorkers) : 0.0
        );
    }

    // Daily Attendance Report
    public Map<String, Object> getDailyAttendanceReport(LocalDate date) {
        long totalWorkers = userRepository.countByRole(User.Role.WORKER);
        long presentToday = attendanceRepository.countPresentByDate(date);
        long absentToday = attendanceRepository.countAbsentByDate(date);
        List<Attendance> todayAttendances = attendanceRepository.findByDate(date);

        Map<String, Object> report = new HashMap<>();
        report.put("date", date);
        report.put("totalWorkers", totalWorkers);
        report.put("present", presentToday);
        report.put("absent", absentToday);
        report.put("attendanceRate", totalWorkers > 0 ? (presentToday * 100.0 / totalWorkers) : 0.0);
        report.put("attendances", todayAttendances);

        return report;
    }

    // Monthly Attendance Report
    public Map<String, Object> getMonthlyAttendanceReport(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        
        List<Worker> workers = userRepository.findByRole(User.Role.WORKER).stream()
                .filter(user -> user.getStatus() == User.UserStatus.ACTIVE)
                .map(user -> (Worker) user)
                .toList();
        
        List<Map<String,Object>> workerReports = workers.stream()
                .map(worker -> {
                    long attendanceDays = attendanceRepository.countAttendanceForWorkerInMonth(
                            worker.getId(), startDate, endDate);
                    
                    Map<String, Object> report = new HashMap<>();
                    report.put("workerId", worker.getId());
                    report.put("workerName", worker.getName());
                    report.put("jobRole", worker.getJobRole());
                    report.put("presentDays", attendanceDays);
                    report.put("totalDays", endDate.getDayOfMonth());
                    report.put("attendancePercentage", (attendanceDays * 100.0 / endDate.getDayOfMonth()));
                    return report;
                })
                .toList();

        Map<String, Object> report = new HashMap<>();
        report.put("month", month);
        report.put("year", year);
        report.put("totalWorkers", workers.size());
        report.put("workerReports", workerReports);

        return report;
    }

    // Individual Worker Attendance Report
    public Map<String, Object> getWorkerAttendanceReport(@NotNull Long workerId, LocalDate startDate, LocalDate endDate) {
        Worker worker = getWorkerById(workerId);
        
        List<Attendance> attendances = attendanceRepository.findByWorkerIdAndDateBetween(workerId, startDate, endDate);
        long presentDays = attendances.size();
        long totalDays = startDate.until(endDate).getDays() + 1;

        Map<String, Object> report = new HashMap<>();
        report.put("worker", worker);
        report.put("startDate", startDate);
        report.put("endDate", endDate);
        report.put("presentDays", presentDays);
        report.put("totalDays", totalDays);
        report.put("attendancePercentage", (presentDays * 100.0 / totalDays));
        report.put("attendances", attendances);

        return report;
    }
}
