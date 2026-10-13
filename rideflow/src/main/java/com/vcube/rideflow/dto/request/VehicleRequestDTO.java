package com.vcube.rideflow.dto.request;

import com.vcube.rideflow.entity.enums.VehicleType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VehicleRequestDTO {
	@NotNull(message = "Driver ID is required")
	private Long driverId;

	@NotBlank(message = "Registration number is required")
	@Size(max = 20, message = "Registration number cannot exceed 20 characters")
	private String registrationNumber;

	@NotBlank(message = "Brand is required")
	@Size(max = 50, message = "Brand cannot exceed 50 characters")
	private String brand;

	@NotBlank(message = "Model is required")
	@Size(max = 50, message = "Model cannot exceed 50 characters")
	private String model;

	@Size(max = 30, message = "Color cannot exceed 30 characters")
	private String color;

	@NotNull(message = "Vehicle type is required")
	private VehicleType vehicleType;

}
