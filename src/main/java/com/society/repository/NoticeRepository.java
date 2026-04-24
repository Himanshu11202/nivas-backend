package com.society.repository;

import com.society.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    
    List<Notice> findAllByOrderByCreatedAtDesc();
    
    @Query("SELECT n FROM Notice n ORDER BY n.createdAt DESC")
    List<Notice> findRecentNotices();
    
    @Query("SELECT COUNT(n) FROM Notice n WHERE n.createdAt >= :startDate")
    long countRecentNotices(@jakarta.validation.constraints.NotNull LocalDateTime startDate);
}
