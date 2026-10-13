package com.vcube.rideflow.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vcube.rideflow.entity.Vehicle;
import com.vcube.rideflow.service.VehicleService;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

	private final VehicleService vehicleService;

	public VehicleController(VehicleService vehicleService) {
		this.vehicleService = vehicleService;
	}

	@PostMapping
	public ResponseEntity<Vehicle> registerVehicle(@RequestBody Vehicle vehicle) {

		Vehicle savedVehicle = vehicleService.registerVehicle(vehicle);

		return ResponseEntity.status(HttpStatus.CREATED).body(savedVehicle);
	}

	@GetMapping
	public ResponseEntity<List<Vehicle>> getAllVehicles() {
		return ResponseEntity.ok(vehicleService.getAllVehicles());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {

		return ResponseEntity.ok(vehicleService.getVehicleById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {

		return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicle));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {

		vehicleService.deleteVehicle(id);

		return ResponseEntity.noContent().build();
	}
}