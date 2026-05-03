package com.society.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "societies")
public class Society {

    public enum SubscriptionStatus {
        ACTIVE,
        EXPIRED,
        BLOCKED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String societyCode;

    @Column(nullable = false)
    private String name;

    @Column
    private String location;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "subscription_status")
    private SubscriptionStatus subscriptionStatus = SubscriptionStatus.ACTIVE;

    @Column(name = "last_payment_date")
    private LocalDateTime lastPaymentDate;

    @Column(name = "subscription_expiry_date")
    private LocalDateTime subscriptionExpiryDate;

    @Column(name = "total_flats")
    private Integer totalFlats = 0;

    @Column(name = "total_residents")
    private Integer totalResidents = 0;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // Set default subscription expiry to 30 days from now
        if (subscriptionExpiryDate == null) {
            subscriptionExpiryDate = LocalDateTime.now().plusDays(30);
        }
    }

    // Constructors
    public Society() {}

    public Society(String societyCode, String name, String location) {
        this.societyCode = societyCode;
        this.name = name;
        this.location = location;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSocietyCode() {
        return societyCode;
    }

    public void setSocietyCode(String societyCode) {
        this.societyCode = societyCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public LocalDateTime getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(LocalDateTime lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public LocalDateTime getSubscriptionExpiryDate() {
        return subscriptionExpiryDate;
    }

    public void setSubscriptionExpiryDate(LocalDateTime subscriptionExpiryDate) {
        this.subscriptionExpiryDate = subscriptionExpiryDate;
    }

    public Integer getTotalFlats() {
        return totalFlats;
    }

    public void setTotalFlats(Integer totalFlats) {
        this.totalFlats = totalFlats;
    }

    public Integer getTotalResidents() {
        return totalResidents;
    }

    public void setTotalResidents(Integer totalResidents) {
        this.totalResidents = totalResidents;
    }
}
