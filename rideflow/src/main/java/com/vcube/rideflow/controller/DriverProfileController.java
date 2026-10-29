
package com.vcube.rideflow.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vcube.rideflow.dto.request.DriverLocationUpdateDTO;
import com.vcube.rideflow.dto.request.DriverProfileRequestDTO;
import com.vcube.rideflow.dto.response.DriverProfileResponseDTO;
import com.vcube.rideflow.dto.response.NearbyDriverResponseDTO;
import com.vcube.rideflow.service.DriverProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/driver-profiles")
public class DriverProfileController {

	private final DriverProfileService driverProfileService;

	public DriverProfileController(DriverProfileService driverProfileService) {
		this.driverProfileService = driverProfileService;
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

	@PostMapping("/convert")
	public ResponseEntity<DriverProfileResponseDTO> convertCustomerToDriver(
			@Valid @RequestBody DriverProfileRequestDTO request) {

		return ResponseEntity.ok(driverProfileService.convertCustomerToDriver(request));
	}

	@GetMapping("/nearby")
	public ResponseEntity<List<NearbyDriverResponseDTO>> findNearbyAvailableDrivers(@RequestParam double latitude,
			@RequestParam double longitude, @RequestParam(defaultValue = "5") double radiusKm) {

		return ResponseEntity.ok(driverProfileService.findNearbyAvailableDrivers(latitude, longitude, radiusKm));
	}

	@PutMapping("/{id}/location")
	public ResponseEntity<Void> updateDriverLocation(@PathVariable Long id,
			@Valid @RequestBody DriverLocationUpdateDTO request, Authentication authentication) {

		driverProfileService.updateDriverLocation(id, request.latitude(), request.longitude(),
				authentication.getName());

		return ResponseEntity.noContent().build();
	}
}
