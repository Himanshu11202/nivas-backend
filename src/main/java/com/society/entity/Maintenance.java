package com.society.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance")
public class Maintenance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Amount is required")
    @Column(name = "amount")
    private BigDecimal amount;

    @NotNull(message = "Due date is required")
    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "month")
    private Integer month;

    @Column(name = "year")
    private Integer year;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "society_id")
    private Long societyId;

    @Column(name = "flat_number")
    private String flatNumber;

    @Column(name = "resident_name")
    private String residentName;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "society_name")
    private String societyName;

    public enum PaymentStatus {
        PENDING, PAID, OVERDUE
    }

    public Maintenance() {
        this.createdAt = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getSocietyId() { return societyId; }
    public void setSocietyId(Long societyId) { this.societyId = societyId; }

    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }

    public String getResidentName() { return residentName; }
    public void setResidentName(String residentName) { this.residentName = residentName; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getSocietyName() { return societyName; }
    public void setSocietyName(String societyName) { this.societyName = societyName; }
}
