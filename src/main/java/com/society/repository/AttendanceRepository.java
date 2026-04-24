package com.society.repository;

import com.society.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    Attendance findByWorkerIdAndDate(Long workerId, LocalDate date);
    
    List<Attendance> findByDate(LocalDate date);
    
    List<Attendance> findByWorkerIdOrderByDateDesc(Long workerId);
    
    List<Attendance> findByWorkerIdAndDateBetween(Long workerId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.date = :date AND a.checkInTime IS NOT NULL")
    long countPresentByDate(@Param("date") LocalDate date);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.date = :date AND a.checkInTime IS NULL")
    long countAbsentByDate(@Param("date") LocalDate date);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.worker.id = :workerId AND a.date BETWEEN :startDate AND :endDate")
    long countAttendanceForWorkerInMonth(@Param("workerId") Long workerId, 
                                      @Param("startDate") LocalDate startDate, 
                                      @Param("endDate") LocalDate endDate);
}
