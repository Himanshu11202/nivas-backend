package com.society.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "flats")
public class Flat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Flat number is required")
    @Column(name = "flat_number", unique = true)
    private String flatNumber;

    @NotBlank(message = "Wing is required")
    @Column(name = "wing")
    private String wing;

    @NotBlank(message = "Floor is required")
    @Column(name = "floor")
    private String floor;

    public Flat() {}

    public Flat(String flatNumber, String wing, String floor) {
        this.flatNumber = flatNumber;
        this.wing = wing;
        this.floor = floor;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }

    public String getWing() { return wing; }
    public void setWing(String wing) { this.wing = wing; }

    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }
}
