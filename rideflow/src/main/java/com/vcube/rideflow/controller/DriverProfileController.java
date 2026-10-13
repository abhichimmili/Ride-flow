
package com.vcube.rideflow.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.vcube.rideflow.entity.DriverProfile;
import com.vcube.rideflow.service.DriverProfileService;

@RestController
@RequestMapping("/api/driver-profiles")
public class DriverProfileController {

	private final DriverProfileService driverProfileService;

	public DriverProfileController(DriverProfileService driverProfileService) {
		this.driverProfileService = driverProfileService;
	}

	@PostMapping("/user/{userId}")
	public DriverProfile createDriverProfile(@PathVariable Long userId, @RequestBody DriverProfile profile) {
		return driverProfileService.createDriverProfile(userId, profile);
	}

	@GetMapping
	public List<DriverProfile> getAllDriverProfiles() {
		return driverProfileService.getAllDriverProfiles();
	}

	@GetMapping("/{id}")
	public DriverProfile getDriverProfileById(@PathVariable Long id) {
		return driverProfileService.getDriverProfileById(id);
	}

	@PutMapping("/{id}")
	public DriverProfile updateDriverProfile(@PathVariable Long id, @RequestBody DriverProfile profile) {
		return driverProfileService.updateDriverProfile(id, profile);
	}

	@DeleteMapping("/{id}")
	public void deleteDriverProfile(@PathVariable Long id) {
		driverProfileService.deleteDriverProfile(id);
	}
}
