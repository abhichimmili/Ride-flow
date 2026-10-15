package com.vcube.rideflow.dto.response;

import com.vcube.rideflow.entity.enums.AvailabilityStatus;

import lombok.Data;

@Data
public class DriverProfileResponseDTO {

	private Long id;
	private Long userId;
	private String licenseNumber;
	private boolean licenseVerified;
	private AvailabilityStatus availabilityStatus;

	public DriverProfileResponseDTO(Long id, Long userId, String licenseNumber, boolean licenseVerified,
			AvailabilityStatus availabilityStatus) {
		this.id = id;
		this.userId = userId;
		this.licenseNumber = licenseNumber;
		this.licenseVerified = licenseVerified;
		this.availabilityStatus = availabilityStatus;
	}
}