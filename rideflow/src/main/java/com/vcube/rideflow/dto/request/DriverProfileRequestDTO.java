package com.vcube.rideflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DriverProfileRequestDTO {

	@NotNull(message = "User ID is required")
	@Positive(message = "User ID must be positive")
	private Long userId;

	@NotBlank(message = "License number is required")
	@Size(max = 50, message = "License number cannot exceed 50 characters")
	private String licenseNumber;

}