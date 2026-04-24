package com.society.repository;

import com.society.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    List<Visitor> findByFlatNumber(String flatNumber);
    List<Visitor> findByStatus(Visitor.VisitorStatus status);
    List<Visitor> findByEntryTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT v FROM Visitor v WHERE v.flatNumber = :flatNumber ORDER BY v.entryTime DESC")
    List<Visitor> findByFlatNumberOrderByEntryTimeDesc(String flatNumber);
    
    @Query("SELECT COUNT(v) FROM Visitor v WHERE v.status = :status")
    long countByStatus(Visitor.VisitorStatus status);
    
    @Query("SELECT COUNT(v) FROM Visitor v WHERE v.entryTime >= :start AND v.entryTime <= :end")
    long countVisitorsBetween(LocalDateTime start, LocalDateTime end);
}
