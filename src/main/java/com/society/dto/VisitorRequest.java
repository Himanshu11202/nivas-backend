package com.society.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class VisitorRequest {
    
    @NotBlank(message = "Visitor name is required")
    @Size(max = 100)
    private String visitorName;
    
    @NotBlank(message = "Phone number is required")
    @Size(max = 15)
    private String visitorPhone;
    
    @NotBlank(message = "Flat number is required")
    private String flatNumber;
    
    @NotBlank(message = "Purpose is required")
    @Size(max = 200)
    private String purpose;
    
    @Size(max = 20)
    private String vehicleNumber;
    
    private String visitorPhoto;
    
    // Getters and Setters
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
}
