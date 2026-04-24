package com.society.dto;

import com.society.entity.Complaint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ComplaintRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 200)
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private Complaint.ComplaintCategory category;
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Complaint.ComplaintCategory getCategory() { return category; }
    public void setCategory(Complaint.ComplaintCategory category) { this.category = category; }
}
