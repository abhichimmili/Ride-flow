package com.vcube.rideflow.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DriverRegistrationRequestDTO {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 150, message = "Email cannot exceed 150 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72, message = "Password must contain 8 to 72 characters")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "[0-9]{10}", message = "Phone number must contain 10 digits")
    private String phone;

    @NotBlank(message = "License number is required")
    @Size(max = 50, message = "License number cannot exceed 50 characters")
    private String licenseNumber;
}