package com.vcube.rideflow.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.vcube.rideflow.dto.request.RideRequestDTO;
import com.vcube.rideflow.dto.response.RideResponseDTO;
import com.vcube.rideflow.service.RideService;

@RestController
@RequestMapping("/api/rides")
public class RideController {

	private final RideService rideService;

	public RideController(RideService rideService) {
		this.rideService = rideService;
	}

	// 1. Create a ride request
	@PostMapping
	public ResponseEntity<RideResponseDTO> createRide(@Valid @RequestBody RideRequestDTO request,
			Authentication authentication) {

		String customerEmail = authentication.getName();

		RideResponseDTO response = rideService.createRide(customerEmail, request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	// 2. Get a ride by ID
	@GetMapping("/{id}")
	public ResponseEntity<RideResponseDTO> getRideById(@PathVariable Long id, Authentication authentication) {

		String customerEmail = authentication.getName();

		RideResponseDTO response = rideService.getRideById(id, customerEmail);

		return ResponseEntity.ok(response);
	}

	// 3. Get the authenticated customer's ride history
	@GetMapping("/my-rides")
	public ResponseEntity<List<RideResponseDTO>> getMyRides(Authentication authentication) {

		String customerEmail = authentication.getName();

		List<RideResponseDTO> rides = rideService.getMyRides(customerEmail);

		return ResponseEntity.ok(rides);
	}

	// 4. Cancel an eligible ride
	@PatchMapping("/{id}/cancel")
	public ResponseEntity<RideResponseDTO> cancelRide(@PathVariable Long id, Authentication authentication) {

		String customerEmail = authentication.getName();

		RideResponseDTO response = rideService.cancelRide(id, customerEmail);

		return ResponseEntity.ok(response);
	}
}
