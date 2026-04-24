package com.society.repository;

import com.society.entity.NoticeRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeReadRepository extends JpaRepository<NoticeRead, Long> {
    
    // Find by notice and user (to check if already read)
    Optional<NoticeRead> findByNoticeIdAndUserId(Long noticeId, Long userId);
    
    // Check if notice is read by user
    boolean existsByNoticeIdAndUserId(Long noticeId, Long userId);
    
    // Get all reads for a notice
    List<NoticeRead> findByNoticeId(Long noticeId);
    
    // Get all reads for a user
    List<NoticeRead> findByUserId(Long userId);
    
    // Count reads for a notice
    Long countByNoticeId(Long noticeId);
    
    // Count reads for a notice where isRead = true
    @Query("SELECT COUNT(nr) FROM NoticeRead nr WHERE nr.notice.id = :noticeId AND nr.isRead = true")
    Long countReadByNoticeId(Long noticeId);
    
    // Get all users who read a notice
    @Query("SELECT nr.user.id FROM NoticeRead nr WHERE nr.notice.id = :noticeId AND nr.isRead = true")
    List<Long> findUserIdsByNoticeId(Long noticeId);
    
    // Get all users who have NOT read a notice
    @Query("SELECT u.id FROM User u WHERE u.role = 'RESIDENT' AND u.id NOT IN (SELECT nr.user.id FROM NoticeRead nr WHERE nr.notice.id = :noticeId AND nr.isRead = true)")
    List<Long> findUnreadUserIdsByNoticeId(Long noticeId);
}
