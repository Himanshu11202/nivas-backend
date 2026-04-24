package com.society.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "worker_attendance")
public class WorkerAttendance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;
    
    @Column
    private LocalDateTime checkInTime;
    
    @Column
    private LocalDateTime checkOutTime;
    
    @Column(name = "check_in_photo", columnDefinition = "TEXT")
    private String checkInPhoto;
    
    @Column(name = "check_out_photo", columnDefinition = "TEXT")
    private String checkOutPhoto;
    
    @Column
    private String notes;
    
    @Column
    private String markedBy;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Enums
    public enum AttendanceStatus {
        PRESENT, ABSENT, HALF_DAY, ON_LEAVE, NOT_MARKED
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Worker getWorker() { return worker; }
    public void setWorker(Worker worker) { this.worker = worker; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
    
    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }
    
    public LocalDateTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalDateTime checkOutTime) { this.checkOutTime = checkOutTime; }
    
    public String getCheckInPhoto() { return checkInPhoto; }
    public void setCheckInPhoto(String checkInPhoto) { this.checkInPhoto = checkInPhoto; }
    
    public String getCheckOutPhoto() { return checkOutPhoto; }
    public void setCheckOutPhoto(String checkOutPhoto) { this.checkOutPhoto = checkOutPhoto; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getMarkedBy() { return markedBy; }
    public void setMarkedBy(String markedBy) { this.markedBy = markedBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
