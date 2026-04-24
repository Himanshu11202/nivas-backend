package com.society.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("GUARD")
public class Guard extends User {
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "shift")
    private String shift;
    
    @Column(name = "salary")
    private Double salary;
    
    @Column(name = "hire_date")
    private LocalDateTime hireDate;
    
    public Guard() {
        super();
        this.setRole(Role.GUARD);
        this.setStatus(UserStatus.ACTIVE);
    }
    
    public Guard(String name, String email, String phoneNumber, String address, String shift, Double salary) {
        super();
        this.setName(name);
        this.setEmail(email);
        this.setPhoneNumber(phoneNumber);
        this.setRole(Role.GUARD);
        this.setStatus(UserStatus.ACTIVE);
        this.address = address;
        this.shift = shift;
        this.salary = salary;
        this.hireDate = LocalDateTime.now();
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getShift() {
        return shift;
    }
    
    public void setShift(String shift) {
        this.shift = shift;
    }
    
    public Double getSalary() {
        return salary;
    }
    
    public void setSalary(Double salary) {
        this.salary = salary;
    }
    
    public LocalDateTime getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(LocalDateTime hireDate) {
        this.hireDate = hireDate;
    }
}
