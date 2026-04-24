package com.society.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "visitors")
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Visitor name is required")
    @Size(max = 100)
    @Column(name = "visitor_name")
    private String visitorName;

    @NotBlank(message = "Phone number is required")
    @Size(max = 15)
    @Column(name = "visitor_phone")
    private String visitorPhone;

    @NotBlank(message = "Flat number is required")
    @Column(name = "flat_number")
    private String flatNumber;

    @NotBlank(message = "Purpose is required")
    @Size(max = 200)
    @Column(name = "purpose")
    private String purpose;

    @Size(max = 20)
    @Column(name = "vehicle_number")
    private String vehicleNumber;

    @Column(name = "visitor_photo", length = 100000)
    private String visitorPhoto;

    @Column(name = "entry_time")
    private LocalDateTime entryTime;

    @Column(name = "exit_time")
    private LocalDateTime exitTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private VisitorStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum VisitorStatus {
        PENDING, APPROVED, REJECTED, ENTERED, EXITED
    }

    public Visitor() {
        this.createdAt = LocalDateTime.now();
        this.status = VisitorStatus.PENDING;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVisitorName() { return visitorName; }
    public void setVisitorName(String visitorName) { this.visitorName = visitorName; }

    public String getVisitorPhone() { return visitorPhone; }
    public void setVisitorPhone(String visitorPhone) { this.visitorPhone = visitorPhone; }

    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }

    public String getVisitorPhoto() { return visitorPhoto; }
    public void setVisitorPhoto(String visitorPhoto) { this.visitorPhoto = visitorPhoto; }

    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }

    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }

    public VisitorStatus getStatus() { return status; }
    public void setStatus(VisitorStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
