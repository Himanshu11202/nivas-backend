package com.society.repository;

import com.society.entity.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {
    List<Maintenance> findByUserId(Long userId);
    List<Maintenance> findByUserIdOrderByDueDateDesc(Long userId);
    List<Maintenance> findByStatus(Maintenance.PaymentStatus status);
    List<Maintenance> findByMonthAndYear(Integer month, Integer year);
    Optional<Maintenance> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);
    List<Maintenance> findByDueDateBefore(LocalDate date);
    boolean existsByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);
    
    @Query("SELECT SUM(m.amount) FROM Maintenance m WHERE m.status = 'PAID'")
    BigDecimal sumPaidMaintenance();
    
    @Query("SELECT SUM(m.amount) FROM Maintenance m WHERE m.status = 'PENDING'")
    BigDecimal sumPendingMaintenance();
    
    @Query("SELECT COUNT(m) FROM Maintenance m WHERE m.status = :status")
    long countByStatus(Maintenance.PaymentStatus status);
    
    @Query("SELECT m FROM Maintenance m WHERE m.userId = :userId ORDER BY m.createdAt DESC")
    List<Maintenance> findByUserIdOrderByCreatedAtDesc(Long userId);
}
