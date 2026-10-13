package com.vcube.rideflow.service;

import java.util.List;

import com.vcube.rideflow.entity.Vehicle;

public interface VehicleService {
	Vehicle registerVehicle(Vehicle vehicle);

	List<Vehicle> getAllVehicles();

	Vehicle getVehicleById(Long id);

	Vehicle updateVehicle(Long id, Vehicle vehicle);

	void deleteVehicle(Long id);
}
