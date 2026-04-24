package com.society.service;

import com.society.entity.Worker;
import com.society.entity.WorkerAttendance;
import com.society.repository.WorkerAttendanceRepository;
import com.society.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WorkerAttendanceService {
    
    @Autowired
    private WorkerAttendanceRepository attendanceRepository;
    
    @Autowired
    private WorkerRepository workerRepository;
    
    // Mark attendance with photo
    @Transactional
    public WorkerAttendance markAttendance(Long workerId, WorkerAttendance.AttendanceStatus status, 
                                          String photoBase64, String notes, String markedBy) {
        LocalDate today = LocalDate.now();
        
        Optional<WorkerAttendance> existing = attendanceRepository.findByWorkerIdAndDate(workerId, today);
        
        WorkerAttendance attendance;
        if (existing.isPresent()) {
            attendance = existing.get();
        } else {
            attendance = new WorkerAttendance();
            Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));
            attendance.setWorker(worker);
            attendance.setDate(today);
        }
        
        attendance.setStatus(status);
        attendance.setCheckInTime(LocalDateTime.now());
        if (photoBase64 != null && !photoBase64.isEmpty()) {
            attendance.setCheckInPhoto(photoBase64);
        }
        attendance.setNotes(notes);
        attendance.setMarkedBy(markedBy);
        
        return attendanceRepository.save(attendance);
    }
    
    // Mark checkout with photo
    @Transactional
    public WorkerAttendance markCheckout(Long workerId, String photoBase64) {
        LocalDate today = LocalDate.now();
        
        WorkerAttendance attendance = attendanceRepository.findByWorkerIdAndDate(workerId, today)
            .orElseThrow(() -> new RuntimeException("No attendance record found for today"));
        
        attendance.setCheckOutTime(LocalDateTime.now());
        if (photoBase64 != null && !photoBase64.isEmpty()) {
            attendance.setCheckOutPhoto(photoBase64);
        }
        
        return attendanceRepository.save(attendance);
    }
    
    // NEW: Check In method
    @Transactional
    public WorkerAttendance checkIn(Long workerId, String checkInPhoto, String markedBy) {
        LocalDate today = LocalDate.now();
        
        Optional<WorkerAttendance> existing = attendanceRepository.findByWorkerIdAndDate(workerId, today);
        
        WorkerAttendance attendance;
        if (existing.isPresent()) {
            attendance = existing.get();
            // Update existing record
            attendance.setCheckInTime(LocalDateTime.now());
            if (checkInPhoto != null && !checkInPhoto.isEmpty()) {
                attendance.setCheckInPhoto(checkInPhoto);
            }
            attendance.setStatus(WorkerAttendance.AttendanceStatus.PRESENT);
            attendance.setMarkedBy(markedBy);
        } else {
            // Create new attendance record
            attendance = new WorkerAttendance();
            Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));
            attendance.setWorker(worker);
            attendance.setDate(today);
            attendance.setCheckInTime(LocalDateTime.now());
            if (checkInPhoto != null && !checkInPhoto.isEmpty()) {
                attendance.setCheckInPhoto(checkInPhoto);
            }
            attendance.setStatus(WorkerAttendance.AttendanceStatus.PRESENT);
            attendance.setMarkedBy(markedBy);
        }
        
        return attendanceRepository.save(attendance);
    }
    
    // NEW: Check Out method
    @Transactional
    public WorkerAttendance checkOut(Long workerId, String markedBy) {
        LocalDate today = LocalDate.now();
        
        WorkerAttendance attendance = attendanceRepository.findByWorkerIdAndDate(workerId, today)
            .orElseThrow(() -> new RuntimeException("No check-in record found for today. Please check in first."));
        
        if (attendance.getCheckOutTime() != null) {
            throw new RuntimeException("Already checked out for today");
        }
        
        attendance.setCheckOutTime(LocalDateTime.now());
        attendance.setMarkedBy(markedBy);
        
        return attendanceRepository.save(attendance);
    }
    
    // NEW: Get detailed stats for admin dashboard
    public java.util.Map<String, Object> getDetailedStats() {
        LocalDate today = LocalDate.now();
        
        long totalWorkers = workerRepository.countActiveWorkers();
        long presentToday = attendanceRepository.countByDateAndStatus(today, WorkerAttendance.AttendanceStatus.PRESENT);
        long checkedOutToday = attendanceRepository.countCheckedOutToday(today);
        long notMarked = totalWorkers - presentToday;
        
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalWorkers", totalWorkers);
        stats.put("presentToday", presentToday);
        stats.put("checkedOutToday", checkedOutToday);
        stats.put("notMarked", notMarked > 0 ? notMarked : 0);
        stats.put("attendanceRate", totalWorkers > 0 ? (presentToday * 100.0 / totalWorkers) : 0.0);
        
        return stats;
    }
    
    // NEW: Get monthly report for a worker
    public java.util.Map<String, Object> getMonthlyReport(Long workerId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        
        Worker worker = workerRepository.findById(workerId)
            .orElseThrow(() -> new RuntimeException("Worker not found"));
        
        List<WorkerAttendance> attendances = attendanceRepository.findByWorkerIdAndDateBetween(workerId, startDate, endDate);
        
        long presentDays = attendances.stream().filter(a -> a.getStatus() == WorkerAttendance.AttendanceStatus.PRESENT).count();
        long absentDays = attendances.stream().filter(a -> a.getStatus() == WorkerAttendance.AttendanceStatus.ABSENT).count();
        long halfDays = attendances.stream().filter(a -> a.getStatus() == WorkerAttendance.AttendanceStatus.HALF_DAY).count();
        long leaveDays = attendances.stream().filter(a -> a.getStatus() == WorkerAttendance.AttendanceStatus.ON_LEAVE).count();
        
        int totalWorkingDays = endDate.getDayOfMonth();
        
        java.util.Map<String, Object> report = new java.util.HashMap<>();
        report.put("worker", worker);
        report.put("year", year);
        report.put("month", month);
        report.put("presentDays", presentDays);
        report.put("absentDays", absentDays);
        report.put("halfDays", halfDays);
        report.put("leaveDays", leaveDays);
        report.put("totalWorkingDays", totalWorkingDays);
        report.put("attendancePercentage", totalWorkingDays > 0 ? (presentDays * 100.0 / totalWorkingDays) : 0.0);
        report.put("attendances", attendances);
        
        return report;
    }
    
    // Get today's attendance for all workers
    public List<WorkerAttendance> getTodayAttendance() {
        return attendanceRepository.findByDateWithActiveWorkers(LocalDate.now());
    }
    
    // Get attendance by date
    public List<WorkerAttendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDateWithActiveWorkers(date);
    }
    
    // Get worker attendance history
    public List<WorkerAttendance> getWorkerAttendanceHistory(Long workerId) {
        return attendanceRepository.findByWorkerIdOrderByDateDesc(workerId);
    }
    
    // Get attendance by worker and date range
    public List<WorkerAttendance> getWorkerAttendanceByDateRange(Long workerId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByWorkerIdAndDateBetween(workerId, startDate, endDate);
    }
    
    // Get attendance statistics
    public AttendanceStats getTodayStats() {
        LocalDate today = LocalDate.now();
        long present = attendanceRepository.countByDateAndStatus(today, WorkerAttendance.AttendanceStatus.PRESENT);
        long absent = attendanceRepository.countByDateAndStatus(today, WorkerAttendance.AttendanceStatus.ABSENT);
        long halfDay = attendanceRepository.countByDateAndStatus(today, WorkerAttendance.AttendanceStatus.HALF_DAY);
        long onLeave = attendanceRepository.countByDateAndStatus(today, WorkerAttendance.AttendanceStatus.ON_LEAVE);
        
        return new AttendanceStats(present, absent, halfDay, onLeave);
    }
    
    // Inner class for stats
    public static class AttendanceStats {
        private long present;
        private long absent;
        private long halfDay;
        private long onLeave;
        
        public AttendanceStats(long present, long absent, long halfDay, long onLeave) {
            this.present = present;
            this.absent = absent;
            this.halfDay = halfDay;
            this.onLeave = onLeave;
        }
        
        public long getPresent() { return present; }
        public long getAbsent() { return absent; }
        public long getHalfDay() { return halfDay; }
        public long getOnLeave() { return onLeave; }
        public long getTotal() { return present + absent + halfDay + onLeave; }
    }
}
