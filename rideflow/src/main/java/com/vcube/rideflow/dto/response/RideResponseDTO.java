package com.vcube.rideflow.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.vcube.rideflow.entity.enums.RideStatus;

import lombok.Data;

@Data
public class RideResponseDTO {
	private Long id;
	private Long customerId;
	private String customerName;

	private Long driverId;
	private Long vehicleId;

	private String pickupAddress;
	private String dropoffAddress;

	private BigDecimal distanceKm;
	private BigDecimal estimatedFare;
	private BigDecimal finalFare;

	private RideStatus status;

	private LocalDateTime requestedAt;
	private LocalDateTime completedAt;

	public RideResponseDTO() {
	}

	public RideResponseDTO(Long id, Long customerId, String customerName, Long driverId, Long vehicleId,
			String pickupAddress, String dropoffAddress, BigDecimal distanceKm, BigDecimal estimatedFare,
			BigDecimal finalFare, RideStatus status, LocalDateTime requestedAt, LocalDateTime completedAt) {

		this.id = id;
		this.customerId = customerId;
		this.customerName = customerName;
		this.driverId = driverId;
		this.vehicleId = vehicleId;
		this.pickupAddress = pickupAddress;
		this.dropoffAddress = dropoffAddress;
		this.distanceKm = distanceKm;
		this.estimatedFare = estimatedFare;
		this.finalFare = finalFare;
		this.status = status;
		this.requestedAt = requestedAt;
		this.completedAt = completedAt;
	}

}
