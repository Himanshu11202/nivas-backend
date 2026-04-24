package com.society.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("WORKER")
public class Worker extends User {
    
    @NotBlank(message = "Job role is required")
    @Size(max = 50)
    @Column(name = "job_role")
    private String jobRole;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Worker Photo (captured from camera)
    @Column(name = "worker_photo", columnDefinition = "TEXT")
    private String workerPhoto;

    // Aadhaar Number (optional)
    @Column(name = "aadhaar_number")
    private String aadhaarNumber;

    // Aadhaar Photo (optional upload)
    @Column(name = "aadhaar_photo", columnDefinition = "TEXT")
    private String aadhaarPhoto;

    public enum WorkerStatus {
        ACTIVE, INACTIVE, ON_LEAVE
    }

    public Worker() {
        super();
        this.setRole(Role.WORKER);
        this.setStatus(User.UserStatus.ACTIVE);
        this.createdAt = LocalDateTime.now();
        this.joiningDate = LocalDate.now();
    }
    
    public Worker(String name, String phoneNumber, String jobRole) {
        super();
        this.setName(name);
        this.setPhoneNumber(phoneNumber);
        this.setRole(Role.WORKER);
        this.setStatus(User.UserStatus.ACTIVE);
        this.jobRole = jobRole;
        this.createdAt = LocalDateTime.now();
        this.joiningDate = LocalDate.now();
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getWorkerPhoto() {
        return workerPhoto;
    }

    public void setWorkerPhoto(String workerPhoto) {
        this.workerPhoto = workerPhoto;
    }

    public String getAadhaarNumber() {
        return aadhaarNumber;
    }

    public void setAadhaarNumber(String aadhaarNumber) {
        this.aadhaarNumber = aadhaarNumber;
    }

    public String getAadhaarPhoto() {
        return aadhaarPhoto;
    }

    public void setAadhaarPhoto(String aadhaarPhoto) {
        this.aadhaarPhoto = aadhaarPhoto;
    }
}
