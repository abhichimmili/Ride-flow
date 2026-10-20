package com.vcube.rideflow.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RideRequestDTO {
	
    @NotBlank(message = "Pickup address is required")
    @Size(max = 255, message = "Pickup address cannot exceed 255 characters")
    private String pickupAddress;

    @NotBlank(message = "Drop-off address is required")
    @Size(max = 255, message = "Drop-off address cannot exceed 255 characters")
    private String dropoffAddress;

    @NotNull(message = "Distance is required")
    @DecimalMin(value = "0.01", message = "Distance must be greater than zero")
    @DecimalMax(value = "999999.99", message = "Distance is too large")
    private BigDecimal distanceKm;

    public RideRequestDTO() {
    }
}
