package com.society.repository;

import com.society.entity.WorkerAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerAttendanceRepository extends JpaRepository<WorkerAttendance, Long> {
    
    List<WorkerAttendance> findByWorkerIdAndDateBetween(Long workerId, LocalDate startDate, LocalDate endDate);
    
    Optional<WorkerAttendance> findByWorkerIdAndDate(Long workerId, LocalDate date);
    
    List<WorkerAttendance> findByDate(LocalDate date);
    
    @Query("SELECT wa FROM WorkerAttendance wa WHERE wa.date = :date AND wa.worker.status = 'ACTIVE'")
    List<WorkerAttendance> findByDateWithActiveWorkers(LocalDate date);
    
    @Query("SELECT wa FROM WorkerAttendance wa WHERE wa.worker.id = :workerId ORDER BY wa.date DESC")
    List<WorkerAttendance> findByWorkerIdOrderByDateDesc(Long workerId);
    
    @Query("SELECT COUNT(wa) FROM WorkerAttendance wa WHERE wa.date = :date AND wa.status = :status")
    Long countByDateAndStatus(LocalDate date, WorkerAttendance.AttendanceStatus status);
    
    @Query("SELECT COUNT(wa) FROM WorkerAttendance wa WHERE wa.date = :date AND wa.checkOutTime IS NOT NULL")
    Long countCheckedOutToday(LocalDate date);
}
