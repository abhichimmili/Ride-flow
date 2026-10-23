package com.vcube.rideflow.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcube.rideflow.dto.request.RideRequestDTO;
import com.vcube.rideflow.dto.response.RideResponseDTO;
import com.vcube.rideflow.entity.Ride;
import com.vcube.rideflow.entity.User;
import com.vcube.rideflow.entity.enums.RideStatus;
import com.vcube.rideflow.exception.RideNotFoundException;
import com.vcube.rideflow.exception.UserNotFoundException;
import com.vcube.rideflow.repository.RideRepository;
import com.vcube.rideflow.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import com.vcube.rideflow.entity.enums.Role;
import java.util.Locale;

@Service
public class RideService {

	private final RideRepository rideRepository;
	private final UserRepository userRepository;
	private final FareService fareService;

	public RideService(RideRepository rideRepository, UserRepository userRepository, FareService fareService) {
		super();
		this.rideRepository = rideRepository;
		this.userRepository = userRepository;
		this.fareService = fareService;
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

		// Calculate the fare on the server.
		ride.setEstimatedFare(fareService.calculateEstimatedFare(request.getDistanceKm()));

		// New rides begin in the REQUESTED state.
		ride.setStatus(RideStatus.REQUESTED);

		// Driver, vehicle, final fare and timestamps are not supplied by the client.
		Ride savedRide = rideRepository.save(ride);

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

}
