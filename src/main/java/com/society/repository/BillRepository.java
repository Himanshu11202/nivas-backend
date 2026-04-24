package com.society.repository;

import com.society.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    
    // Find bills by user ID
    List<Bill> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Find bills by flat number
    List<Bill> findByFlatNumberOrderByCreatedAtDesc(String flatNumber);
    
    // Find bills by status
    List<Bill> findByStatusOrderByCreatedAtDesc(Bill.BillStatus status);
    
    // Find bills by user and status
    List<Bill> findByUserIdAndStatus(Long userId, Bill.BillStatus status);
    
    // Find bills by month and year
    List<Bill> findByMonthAndYearOrderByCreatedAtDesc(Integer month, Integer year);
    
    // Count bills by status
    Long countByStatus(Bill.BillStatus status);
    
    // Count bills by user and status
    Long countByUserIdAndStatus(Long userId, Bill.BillStatus status);
    
    // Check if bill exists for user, month, year
    Optional<Bill> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);
    
    // Get all bills sorted by date
    List<Bill> findAllByOrderByCreatedAtDesc();
    
    // Search bills by flat number (partial match)
    @Query("SELECT b FROM Bill b WHERE b.flatNumber LIKE %:search% ORDER BY b.createdAt DESC")
    List<Bill> searchByFlatNumber(String search);
}
