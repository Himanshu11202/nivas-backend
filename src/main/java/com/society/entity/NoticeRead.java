package com.society.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notice_reads", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"notice_id", "user_id"}, name = "uk_notice_user")
})
public class NoticeRead {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public NoticeRead() {
        this.createdAt = LocalDateTime.now();
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
    
    public NoticeRead(Notice notice, User user) {
        this();
        this.notice = notice;
        this.user = user;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Notice getNotice() { return notice; }
    public void setNotice(Notice notice) { this.notice = notice; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
