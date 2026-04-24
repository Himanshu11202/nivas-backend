package com.society.dto;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String name;
    private String role;
    String flatNumber;
    String status;

    public AuthResponse(String token, Long id, String email, String name, String role, String flatNumber, String status) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.flatNumber = flatNumber;
        this.status = status;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFlatNumber() { return flatNumber; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
