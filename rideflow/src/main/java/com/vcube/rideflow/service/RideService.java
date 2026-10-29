package com.vcube.rideflow.service;

import java.util.List;
import java.util.Locale;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcube.rideflow.dto.request.RideRequestDTO;
import com.vcube.rideflow.dto.response.NearbyDriverResponseDTO;
import com.vcube.rideflow.dto.response.RideResponseDTO;
import com.vcube.rideflow.entity.DriverProfile;
import com.vcube.rideflow.entity.Ride;
import com.vcube.rideflow.entity.User;
import com.vcube.rideflow.entity.enums.AvailabilityStatus;
import com.vcube.rideflow.entity.enums.RideStatus;
import com.vcube.rideflow.entity.enums.Role;
import com.vcube.rideflow.exception.DriverProfileNotFoundException;
import com.vcube.rideflow.exception.RideNotFoundException;
import com.vcube.rideflow.exception.UserNotFoundException;
import com.vcube.rideflow.repository.DriverProfileRepository;
import com.vcube.rideflow.repository.RideRepository;
import com.vcube.rideflow.repository.UserRepository;

@Service
public class RideService {

	private final RideRepository rideRepository;
	private final UserRepository userRepository;
	private final DriverProfileRepository driverProfileRepository;
	private final DriverProfileService driverProfileService;
	private final RideDispatchService rideDispatchService;

	private static final double DISPATCH_RADIUS_KM = 5.0;
	private final FareService fareService;
	private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

	public RideService(RideRepository rideRepository, UserRepository userRepository, FareService fareService,
			DriverProfileRepository driverProfileRepository, DriverProfileService driverProfileService,
			RideDispatchService rideDispatchService) {
		super();
		this.rideRepository = rideRepository;
		this.userRepository = userRepository;
		this.fareService = fareService;
		this.driverProfileRepository = driverProfileRepository;
		this.driverProfileService = driverProfileService;
		this.rideDispatchService = rideDispatchService;
	}

	// 1. Create a ride for the authenticated customer
	@Transactional
	public RideResponseDTO createRide(String customerEmail, RideRequestDTO request) {

		User customer = userRepository.findByEmail(customerEmail.trim().toLowerCase(Locale.ROOT))
				.orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));

		if (!customer.getActive()) {
			throw new IllegalStateException("Inactive users cannot request rides");
		}

		if (customer.getRole() != Role.CUSTOMER) {
			throw new AccessDeniedException("Only customers can request rides");
		}

		Ride ride = new Ride();

		ride.setCustomer(customer);
		ride.setPickupAddress(request.getPickupAddress().trim());
		ride.setDropoffAddress(request.getDropoffAddress().trim());
		ride.setDistanceKm(request.getDistanceKm());
		// Create pickup location using longitude (X) and latitude (Y).
		Point pickupLocation = geometryFactory
				.createPoint(new Coordinate(request.getPickupLongitude(), request.getPickupLatitude()));

		ride.setPickupLocation(pickupLocation);

		// Calculate the fare on the server.
		ride.setEstimatedFare(fareService.calculateEstimatedFare(request.getDistanceKm()));

		// New rides begin in the REQUESTED state.
		ride.setStatus(RideStatus.REQUESTED);

		// Driver, vehicle, final fare and timestamps are not supplied by the client.
		// Save the ride first. It remains REQUESTED and unassigned.
		Ride savedRide = rideRepository.save(ride);

		// Find eligible drivers near the pickup location.
		// DriverProfileService already sorts the results by distance.
		List<NearbyDriverResponseDTO> nearbyDrivers = driverProfileService.findNearbyAvailableDrivers(
				request.getPickupLatitude(), request.getPickupLongitude(), DISPATCH_RADIUS_KM);

		// Create ride offers for the nearby drivers.
		rideDispatchService.dispatchRide(savedRide.getId(), nearbyDrivers);

		return mapToResponse(savedRide);
	}

	// 2. Get a ride only if it belongs to the authenticated customer
	@Transactional(readOnly = true)
	public RideResponseDTO getRideById(Long rideId, String customerEmail) {

		User customer = findCustomerByEmail(customerEmail);

		Ride ride = rideRepository.findByIdAndCustomerId(rideId, customer.getId())
				.orElseThrow(() -> new RideNotFoundException("Ride not found with ID: " + rideId));

		return mapToResponse(ride);
	}

	// 3. Get the authenticated customer's ride history
	@Transactional(readOnly = true)
	public List<RideResponseDTO> getMyRides(String customerEmail) {

		User customer = findCustomerByEmail(customerEmail);

		return rideRepository.findByCustomerIdOrderByRequestedAtDesc(customer.getId()).stream().map(this::mapToResponse)
				.toList();
	}

	// 4. Cancel an eligible ride belonging to the authenticated customer
	@Transactional
	public RideResponseDTO cancelRide(Long rideId, String customerEmail) {

		User customer = findCustomerByEmail(customerEmail);

		Ride ride = rideRepository.findByIdAndCustomerId(rideId, customer.getId())
				.orElseThrow(() -> new RideNotFoundException("Ride not found with ID: " + rideId));

		if (ride.getStatus() != RideStatus.REQUESTED) {
			throw new IllegalStateException("Only rides with REQUESTED status can be cancelled");
		}

		ride.setStatus(RideStatus.CANCELLED);

		Ride cancelledRide = rideRepository.save(ride);

		return mapToResponse(cancelledRide);
	}

	private User findCustomerByEmail(String email) {

		return userRepository.findByEmail(email.trim().toLowerCase(Locale.ROOT))
				.orElseThrow(() -> new UserNotFoundException("User not found"));
	}

	private RideResponseDTO mapToResponse(Ride ride) {

		return new RideResponseDTO(ride.getId(), ride.getCustomer().getId(), ride.getCustomer().getFullName(),
				ride.getDriver() != null ? ride.getDriver().getId() : null,
				ride.getVehicle() != null ? ride.getVehicle().getId() : null, ride.getPickupAddress(),
				ride.getDropoffAddress(), ride.getDistanceKm(), ride.getEstimatedFare(), ride.getFinalFare(),
				ride.getStatus(), ride.getRequestedAt(), ride.getCompletedAt());
	}

	@Transactional
	public void acceptRide(Long rideId, Long driverId) {

		DriverProfile driver = driverProfileRepository.findById(driverId)
				.orElseThrow(() -> new DriverProfileNotFoundException("Driver not found: " + driverId));

		if (driver.getAvailabilityStatus() != AvailabilityStatus.ONLINE) {
			throw new IllegalStateException("Driver is not available to accept rides");
		}

		int updated = rideRepository.acceptRide(rideId, driver, RideStatus.REQUESTED, RideStatus.DRIVER_ASSIGNED);

		if (updated == 0) {
			throw new IllegalStateException("Ride has already been accepted or is no longer available");
		}

		driver.setAvailabilityStatus(AvailabilityStatus.ON_TRIP);
		driverProfileRepository.save(driver);
	}

}
