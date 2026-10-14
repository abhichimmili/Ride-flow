
package com.vcube.rideflow.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcube.rideflow.entity.DriverProfile;
import com.vcube.rideflow.entity.User;
import com.vcube.rideflow.entity.enums.AvailabilityStatus;
import com.vcube.rideflow.repository.DriverProfileRepository;
import com.vcube.rideflow.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DriverProfileService {

	private final DriverProfileRepository driverProfileRepository;
	private final UserRepository userRepository;

	public DriverProfileService(DriverProfileRepository driverProfileRepository, UserRepository userRepository) {
		this.driverProfileRepository = driverProfileRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public DriverProfile createDriverProfile(Long userId, DriverProfile profile) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

		if (driverProfileRepository.existsByUser_Id(userId)) {
			throw new IllegalArgumentException("Driver profile already exists for this user");
		}

		profile.setId(null);
		profile.setUser(user);

		if (profile.getLicenseVerified() == null) {
			profile.setLicenseVerified(false);
		}

		if (profile.getAvailabilityStatus() == null) {
			profile.setAvailabilityStatus(AvailabilityStatus.OFFLINE);
		}

		return driverProfileRepository.save(profile);
	}

	@Transactional(readOnly = true)
	public List<DriverProfile> getAllDriverProfiles() {
		return driverProfileRepository.findAll();
	}

	// Get driver profile by ID
	@Transactional(readOnly = true)
	public DriverProfile getDriverProfileById(Long id) {
		return driverProfileRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Driver profile not found with ID: " + id));
	}

	@Transactional
	public DriverProfile updateDriverProfile(Long id, DriverProfile updatedProfile) {

		DriverProfile existingProfile = getDriverProfileById(id);

		existingProfile.setLicenseNumber(updatedProfile.getLicenseNumber());

		existingProfile.setAvailabilityStatus(updatedProfile.getAvailabilityStatus());

		return driverProfileRepository.save(existingProfile);
	}

	@Transactional
	public void deleteDriverProfile(Long id) {
		DriverProfile existingProfile = getDriverProfileById(id);
		driverProfileRepository.delete(existingProfile);
	}
}
