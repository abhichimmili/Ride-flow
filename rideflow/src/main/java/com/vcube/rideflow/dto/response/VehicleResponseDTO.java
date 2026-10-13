package com.vcube.rideflow.dto.response;

import com.vcube.rideflow.entity.enums.VehicleType;

import lombok.Data;

@Data
public class VehicleResponseDTO {
	private Long id;
    private Long driverId;
    private String registrationNumber;
    private String brand;
    private String model;
    private String color;
    private VehicleType vehicleType;
    private boolean verified;
    
    public VehicleResponseDTO() {
    	
    }

	public VehicleResponseDTO(Long id, Long driverId, String registrationNumber, String brand, String model,
			String color, VehicleType vehicleType, boolean verified) {
		super();
		this.id = id;
		this.driverId = driverId;
		this.registrationNumber = registrationNumber;
		this.brand = brand;
		this.model = model;
		this.color = color;
		this.vehicleType = vehicleType;
		this.verified = verified;
	}
    
    
}
