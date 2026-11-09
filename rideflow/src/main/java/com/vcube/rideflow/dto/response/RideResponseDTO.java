package com.vcube.rideflow.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.vcube.rideflow.entity.enums.RideStatus;
import com.vcube.rideflow.entity.enums.VehicleType;

import jakarta.validation.constraints.NotNull;

public record RideResponseDTO(Long id, Long customerId, String customerName,

		Long driverId, Long vehicleId,

		String pickupAddress, String dropoffAddress,

		BigDecimal distanceKm, BigDecimal estimatedFare, BigDecimal finalFare,

		RideStatus status,

		LocalDateTime requestedAt, LocalDateTime completedAt) {

}
