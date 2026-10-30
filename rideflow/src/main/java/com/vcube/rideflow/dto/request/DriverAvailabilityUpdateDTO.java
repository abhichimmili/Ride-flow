package com.vcube.rideflow.dto.request;

import com.vcube.rideflow.entity.enums.AvailabilityStatus;

import jakarta.validation.constraints.NotNull;

public record DriverAvailabilityUpdateDTO(

		@NotNull(message = "Availability status is required") 
		AvailabilityStatus status

) {
}