
package com.vcube.rideflow.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.vcube.rideflow.dto.request.DriverProfileRequestDTO;
import com.vcube.rideflow.dto.response.DriverProfileResponseDTO;
import com.vcube.rideflow.dto.response.NearbyDriverResponseDTO;
import com.vcube.rideflow.entity.DriverProfile;
import com.vcube.rideflow.entity.User;
import com.vcube.rideflow.entity.enums.AvailabilityStatus;
import com.vcube.rideflow.entity.enums.Role;
import com.vcube.rideflow.exception.DriverProfileAlreadyExistsException;
import com.vcube.rideflow.exception.DriverProfileNotFoundException;
import com.vcube.rideflow.exception.DuplicateLicenseException;
import com.vcube.rideflow.exception.InvalidUserRoleException;
import com.vcube.rideflow.exception.UserNotFoundException;
import com.vcube.rideflow.repository.DriverProfileRepository;
import com.vcube.rideflow.repository.UserRepository;

@Service
public class DriverProfileService {

	private final DriverProfileRepository driverProfileRepository;
	private final UserRepository userRepository;

	public DriverProfileService(DriverProfileRepository driverProfileRepository, UserRepository userRepository) {
		this.driverProfileRepository = driverProfileRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public DriverProfileResponseDTO createDriverProfile(DriverProfileRequestDTO request) {

		Long userId = request.getUserId();

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

		if (user.getRole() != Role.DRIVER) {
			throw new InvalidUserRoleException("User must have the DRIVER role to create a driver profile");
		}

		if (driverProfileRepository.existsByUser_Id(userId)) {
			throw new DriverProfileAlreadyExistsException("Driver profile already exists for this user");
		}

		String licenseNumber = request.getLicenseNumber().trim();

		if (driverProfileRepository.existsByLicenseNumber(licenseNumber)) {
			throw new DuplicateLicenseException("License number is already registered");
		}

		DriverProfile profile = new DriverProfile();
		profile.setUser(user);
		profile.setLicenseNumber(licenseNumber);
		profile.setLicenseVerified(false);
		profile.setAvailabilityStatus(AvailabilityStatus.OFFLINE);

		DriverProfile savedProfile = driverProfileRepository.save(profile);

		return mapToResponse(savedProfile);
	}

	@Transactional(readOnly = true)
	public List<DriverProfileResponseDTO> getAllDriverProfiles() {
		return driverProfileRepository.findAll().stream().map(this::mapToResponse).toList();
	}

	@Transactional(readOnly = true)
	public DriverProfileResponseDTO getDriverProfileById(Long id) {
		DriverProfile profile = driverProfileRepository.findById(id)
				.orElseThrow(() -> new DriverProfileNotFoundException("Driver profile not found with ID: " + id));

		return mapToResponse(profile);
	}

	@Transactional
	public void updateMyLocation(String authenticatedEmail, double latitude, double longitude) {

		if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90) {

			throw new IllegalArgumentException("Invalid latitude.");
		}

		if (!Double.isFinite(longitude) || longitude < -180 || longitude > 180) {

			throw new IllegalArgumentException("Invalid longitude.");
		}

		DriverProfile profile = driverProfileRepository.findByUser_Email(authenticatedEmail)
				.orElseThrow(() -> new DriverProfileNotFoundException("Driver profile not found."));

		int updatedRows = driverProfileRepository.updateDriverLocation(profile.getId(), latitude, longitude);

		if (updatedRows != 1) {
			throw new DriverProfileNotFoundException("Driver profile not found.");
		}
	}

	@Transactional
	public DriverProfileResponseDTO updateMyAvailability(String authenticatedEmail,
			AvailabilityStatus requestedStatus) {

		if (requestedStatus == AvailabilityStatus.ON_TRIP) {
			throw new IllegalArgumentException("ON_TRIP status is managed by the ride lifecycle.");
		}

		DriverProfile driver = driverProfileRepository.findByUser_Email(authenticatedEmail)
				.orElseThrow(() -> new DriverProfileNotFoundException("Driver profile not found."));

		AvailabilityStatus currentStatus = driver.getAvailabilityStatus();

		if (currentStatus == requestedStatus) {
			return mapToResponse(driver);
		}

		if (currentStatus == AvailabilityStatus.ON_TRIP) {
			throw new IllegalStateException("Availability cannot be changed during an active ride.");
		}

		if (requestedStatus == AvailabilityStatus.ONLINE && !driver.getLicenseVerified()) {

			throw new IllegalStateException("Your license must be verified before going online.");
		}

		int updated = driverProfileRepository.changeAvailabilityIfCurrentStatusMatches(driver.getId(), currentStatus,
				requestedStatus);

		if (updated != 1) {
			throw new IllegalStateException("Your availability changed concurrently. Please try again.");
		}

		driver.setAvailabilityStatus(requestedStatus);

		return mapToResponse(driver);
	}

	@Transactional
	public DriverProfileResponseDTO updateLicenseVerification(Long driverProfileId, boolean verified) {

		DriverProfile profile = driverProfileRepository.findById(driverProfileId).orElseThrow(
				() -> new DriverProfileNotFoundException("Driver profile not found with ID: " + driverProfileId));

		// Update the verification decision.
		profile.setLicenseVerified(verified);

		// Prevent an unverified driver from remaining available.
		if (!verified && profile.getAvailabilityStatus() == AvailabilityStatus.ONLINE) {

			profile.setAvailabilityStatus(AvailabilityStatus.OFFLINE);
		}

		DriverProfile updatedProfile = driverProfileRepository.save(profile);

		return mapToResponse(updatedProfile);
	}

	@Transactional
	public DriverProfileResponseDTO updateDriverProfile(Long id, DriverProfileRequestDTO request) {

		DriverProfile existingProfile = driverProfileRepository.findById(id)
				.orElseThrow(() -> new DriverProfileNotFoundException("Driver profile not found with ID: " + id));

		String licenseNumber = request.getLicenseNumber().trim();

		if (!existingProfile.getLicenseNumber().equals(licenseNumber)
				&& driverProfileRepository.existsByLicenseNumber(licenseNumber)) {
			throw new DuplicateLicenseException("License number is already registered");
		}

		existingProfile.setLicenseNumber(licenseNumber);

		DriverProfile updatedProfile = driverProfileRepository.save(existingProfile);

		return mapToResponse(updatedProfile);
	}

	@Transactional
	public void deleteDriverProfile(Long id) {
		DriverProfile existingProfile = driverProfileRepository.findById(id)
				.orElseThrow(() -> new DriverProfileNotFoundException("Driver profile not found with ID: " + id));

		driverProfileRepository.delete(existingProfile);
	}

	private DriverProfileResponseDTO mapToResponse(DriverProfile profile) {

		return new DriverProfileResponseDTO(profile.getId(), profile.getUser().getId(), profile.getLicenseNumber(),
				profile.getLicenseVerified(), profile.getAvailabilityStatus());
	}

	@Transactional
	public DriverProfileResponseDTO convertCustomerToDriver(DriverProfileRequestDTO request) {

		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new UserNotFoundException("User not found"));

		if (user.getRole() != Role.CUSTOMER) {
			throw new InvalidUserRoleException("Only customers can be converted to drivers");
		}

		user.setRole(Role.DRIVER);
		userRepository.save(user);

		return createDriverProfile(request);
	}

	@Transactional(readOnly = true)
	public List<NearbyDriverResponseDTO> findNearbyAvailableDrivers(double latitude, double longitude,
			double radiusKm) {

		if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90) {
			throw new IllegalArgumentException("Latitude must be between -90 and 90");
		}

		if (!Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
			throw new IllegalArgumentException("Longitude must be between -180 and 180");
		}

		if (!Double.isFinite(radiusKm) || radiusKm <= 0 || radiusKm > 100) {
			throw new IllegalArgumentException("Radius must be greater than 0 and at most 100 km");
		}

		double radiusMeters = radiusKm * 1000.0;

		List<Object[]> rows = driverProfileRepository.findNearbyAvailableDrivers(latitude, longitude, radiusMeters);

		List<NearbyDriverResponseDTO> results = new ArrayList<>();

		for (Object[] row : rows) {
			results.add(new NearbyDriverResponseDTO(((Number) row[0]).longValue(), ((Number) row[1]).longValue(),
					(String) row[2], (Boolean) row[3], row[4].toString(), ((Number) row[5]).doubleValue()));
		}

		return results.stream().sorted(Comparator.comparingDouble(NearbyDriverResponseDTO::distanceKm)).toList();
	}
}
