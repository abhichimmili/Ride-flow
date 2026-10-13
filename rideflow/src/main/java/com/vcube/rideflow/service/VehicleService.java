package com.vcube.rideflow.service;

import java.util.List;

import com.vcube.rideflow.dto.request.VehicleRequestDTO;
import com.vcube.rideflow.dto.response.VehicleResponseDTO;

public interface VehicleService {
	VehicleResponseDTO registerVehicle(VehicleRequestDTO vehicle);

	List<VehicleResponseDTO> getAllVehicles();

	VehicleResponseDTO getVehicleById(Long id);

	VehicleResponseDTO updateVehicle(Long id, VehicleRequestDTO vehicle);

	void deleteVehicle(Long id);
}
