package com.society.repository;

import com.society.entity.GuardAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GuardAttendanceRepository extends JpaRepository<GuardAttendance, Long> {
    
    List<GuardAttendance> findByGuardIdAndDateBetween(Long guardId, LocalDate startDate, LocalDate endDate);
    
    Optional<GuardAttendance> findByGuardIdAndDate(Long guardId, LocalDate date);
    
    List<GuardAttendance> findByDate(LocalDate date);
    
    @Query("SELECT ga FROM GuardAttendance ga WHERE ga.date = :date AND ga.guard.status = 'ACTIVE'")
    List<GuardAttendance> findByDateWithActiveGuards(LocalDate date);
    
    @Query("SELECT ga FROM GuardAttendance ga WHERE ga.guard.id = :guardId ORDER BY ga.date DESC")
    List<GuardAttendance> findByGuardIdOrderByDateDesc(Long guardId);
    
    @Query("SELECT COUNT(ga) FROM GuardAttendance ga WHERE ga.date = :date AND ga.status = :status")
    Long countByDateAndStatus(LocalDate date, GuardAttendance.AttendanceStatus status);
}
