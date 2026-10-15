
package com.vcube.rideflow.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vcube.rideflow.dto.request.DriverProfileRequestDTO;
import com.vcube.rideflow.dto.response.DriverProfileResponseDTO;
import com.vcube.rideflow.service.DriverProfileService;

@RestController
@RequestMapping("/api/driver-profiles")
public class DriverProfileController {

	private final DriverProfileService driverProfileService;

	public DriverProfileController(DriverProfileService driverProfileService) {
		this.driverProfileService = driverProfileService;
	}

	@PostMapping
	public ResponseEntity<DriverProfileResponseDTO> createDriverProfile(
			@Valid @RequestBody DriverProfileRequestDTO request) {

		DriverProfileResponseDTO response = driverProfileService.createDriverProfile(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<DriverProfileResponseDTO>> getAllDriverProfiles() {

		return ResponseEntity.ok(driverProfileService.getAllDriverProfiles());
	}

	@GetMapping("/{id}")
	public ResponseEntity<DriverProfileResponseDTO> getDriverProfileById(@PathVariable Long id) {

		return ResponseEntity.ok(driverProfileService.getDriverProfileById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<DriverProfileResponseDTO> updateDriverProfile(@PathVariable Long id,
			@Valid @RequestBody DriverProfileRequestDTO request) {

		return ResponseEntity.ok(driverProfileService.updateDriverProfile(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteDriverProfile(@PathVariable Long id) {

		driverProfileService.deleteDriverProfile(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/convert/{userId}")
	public ResponseEntity<DriverProfileResponseDTO> convertCustomerToDriver(@PathVariable Long userId,
			@Valid @RequestBody DriverProfileRequestDTO request) {

		if (!userId.equals(request.getUserId())) {
			throw new IllegalArgumentException("Path userId and request userId must match");
		}

		return ResponseEntity.ok(driverProfileService.convertCustomerToDriver(request));
	}
}
