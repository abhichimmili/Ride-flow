package com.vcube.rideflow.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.vcube.rideflow.dto.request.RideRequestDTO;
import com.vcube.rideflow.dto.response.NearbyDriverResponseDTO;
import com.vcube.rideflow.dto.response.RideResponseDTO;
import com.vcube.rideflow.entity.DriverProfile;
import com.vcube.rideflow.entity.Ride;
import com.vcube.rideflow.entity.User;
import com.vcube.rideflow.entity.enums.AvailabilityStatus;
import com.vcube.rideflow.entity.enums.RideOfferStatus;
import com.vcube.rideflow.entity.enums.RideStatus;
import com.vcube.rideflow.entity.enums.Role;
import com.vcube.rideflow.exception.DriverProfileNotFoundException;
import com.vcube.rideflow.exception.RideAssignmentConflictException;
import com.vcube.rideflow.exception.RideNotFoundException;
import com.vcube.rideflow.exception.UserNotFoundException;
import com.vcube.rideflow.repository.DriverProfileRepository;
import com.vcube.rideflow.repository.RideOfferRepository;
import com.vcube.rideflow.repository.RideRepository;
import com.vcube.rideflow.repository.UserRepository;

@Service
public class RideService {

	private final RideRepository rideRepository;
	private final UserRepository userRepository;
	private final DriverProfileRepository driverProfileRepository;
	private final DriverProfileService driverProfileService;
	private final RideDispatchService rideDispatchService;
	private final RideOfferRepository rideOfferRepository;

	private static final double DISPATCH_RADIUS_KM = 5.0;
	private final FareService fareService;
	private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

	public RideService(RideRepository rideRepository, UserRepository userRepository, FareService fareService,
			DriverProfileRepository driverProfileRepository, DriverProfileService driverProfileService,
			RideDispatchService rideDispatchService, RideOfferRepository rideOfferRepository) {
		super();
		this.rideRepository = rideRepository;
		this.userRepository = userRepository;
		this.fareService = fareService;
		this.driverProfileRepository = driverProfileRepository;
		this.driverProfileService = driverProfileService;
		this.rideDispatchService = rideDispatchService;
		this.rideOfferRepository = rideOfferRepository;
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

		// Create drop-off location.
		Point dropoffLocation = geometryFactory
				.createPoint(new Coordinate(request.getDropoffLongitude(), request.getDropoffLatitude()));

		ride.setDropoffLocation(dropoffLocation);

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
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Only rides with REQUESTED status can be cancelled");
		}

		ride.setStatus(RideStatus.CANCELLED);

		Ride cancelledRide = rideRepository.save(ride);

		rideOfferRepository.cancelPendingOffers(rideId, RideOfferStatus.OFFERED, RideOfferStatus.CANCELLED,
				LocalDateTime.now());

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

		// 1. Load the driver profile.
		DriverProfile driver = driverProfileRepository.findById(driverId)
				.orElseThrow(() -> new IllegalArgumentException("Driver profile not found"));

		// 2. Atomically claim the driver.
		int driverUpdated = driverProfileRepository.claimDriverForRide(driverId, AvailabilityStatus.ONLINE,
				AvailabilityStatus.ON_TRIP);

		if (driverUpdated != 1) {
			throw new RideAssignmentConflictException("Driver is no longer available");
		}

		// 3. Atomically claim the ride.
		int rideUpdated = rideRepository.acceptRide(rideId, driver, RideStatus.REQUESTED, RideStatus.DRIVER_ASSIGNED);

		if (rideUpdated != 1) {
			// Throwing a runtime exception rolls back the transaction.
			throw new RideAssignmentConflictException("Ride has already been assigned or is no longer available");
		}
	}

	@Transactional
	public void markDriverArriving(Long rideId, String driverEmail) {

		transitionDriverRide(rideId, driverEmail, RideStatus.DRIVER_ASSIGNED, RideStatus.DRIVER_ARRIVING);
	}

	@Transactional
	public void markDriverArrived(Long rideId, String driverEmail) {

		transitionDriverRide(rideId, driverEmail, RideStatus.DRIVER_ARRIVING, RideStatus.DRIVER_ARRIVED);
	}

	@Transactional
	public void startRide(Long rideId, String driverEmail) {

		transitionDriverRide(rideId, driverEmail, RideStatus.DRIVER_ARRIVED, RideStatus.STARTED);
	}

	@Transactional
	public void completeRide(Long rideId, String driverEmail) {

		DriverProfile driver = findDriverByEmail(driverEmail);

		int rideUpdated = rideRepository.completeRide(rideId, driver.getId(), RideStatus.STARTED, RideStatus.COMPLETED,
				LocalDateTime.now());

		if (rideUpdated != 1) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Only your assigned STARTED ride can be completed");
		}

		int driverUpdated = driverProfileRepository.restoreDriverAvailability(driver.getId(),
				AvailabilityStatus.ON_TRIP, AvailabilityStatus.ONLINE);

		if (driverUpdated != 1) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Driver availability could not be restored");
		}
	}
	
	

	private DriverProfile findDriverByEmail(String email) {

		return driverProfileRepository.findByUser_Email(email).orElseThrow(
				() -> new DriverProfileNotFoundException("Driver profile not found for authenticated user"));
	}

	private void transitionDriverRide(Long rideId, String driverEmail, RideStatus expectedStatus,
			RideStatus newStatus) {

		DriverProfile driver = findDriverByEmail(driverEmail);

		int updated = rideRepository.transitionRideStatus(rideId, driver.getId(), expectedStatus, newStatus);

		if (updated != 1) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Ride is not assigned to you or the requested transition is invalid");
		}
	}

}
