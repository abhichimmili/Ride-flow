
package com.vcube.rideflow.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcube.rideflow.dto.response.RideOfferResponseDTO;
import com.vcube.rideflow.entity.DriverProfile;
import com.vcube.rideflow.entity.Ride;
import com.vcube.rideflow.entity.RideOffer;
import com.vcube.rideflow.entity.enums.RideOfferStatus;
import com.vcube.rideflow.exception.DriverProfileNotFoundException;
import com.vcube.rideflow.repository.DriverProfileRepository;
import com.vcube.rideflow.repository.RideOfferRepository;

@Service
public class RideOfferService {

	private final RideOfferRepository rideOfferRepository;
	private final DriverProfileRepository driverProfileRepository;
	private final RideService rideService;

	public RideOfferService(RideOfferRepository rideOfferRepository, DriverProfileRepository driverProfileRepository,
			RideService rideService) {

		this.rideOfferRepository = rideOfferRepository;
		this.driverProfileRepository = driverProfileRepository;
		this.rideService = rideService;
	}

	private DriverProfile getDriverByEmail(String email) {
		return driverProfileRepository.findByUser_Email(email)
				.orElseThrow(() -> new DriverProfileNotFoundException("Driver profile not found for email: " + email));
	}

	@Transactional(readOnly = true)
	public List<RideOfferResponseDTO> getMyOffers(String email) {

		DriverProfile driver = getDriverByEmail(email);

		return rideOfferRepository.findByDriverIdAndStatusOrderByOfferedAtAsc(driver.getId(), RideOfferStatus.OFFERED)
				.stream().map(this::mapToResponse).toList();
	}

	@Transactional
	public void acceptOffer(Long rideId, String email) {

		DriverProfile driver = getDriverByEmail(email);
		Long driverId = driver.getId();

		RideOffer offer = rideOfferRepository.findByRideIdAndDriverId(rideId, driverId)
				.orElseThrow(() -> new IllegalStateException("No offer exists for this ride and driver"));

		if (offer.getStatus() != RideOfferStatus.OFFERED) {
			throw new IllegalStateException("This ride offer is no longer available");
		}

		// Atomically assign the ride. Throws if another driver already won.
		rideService.acceptRide(rideId, driverId);

		offer.setStatus(RideOfferStatus.ACCEPTED);
		offer.setRespondedAt(LocalDateTime.now());
		rideOfferRepository.save(offer);

		// Cancel all remaining offers for this ride.
		rideOfferRepository.cancelPendingOffers(
		        rideId,
		        RideOfferStatus.OFFERED,
		        RideOfferStatus.CANCELLED,
		        LocalDateTime.now()
		);
	}

	@Transactional
	public void rejectOffer(Long rideId, String email) {

		DriverProfile driver = getDriverByEmail(email);
		Long driverId = driver.getId();

		RideOffer offer = rideOfferRepository.findByRideIdAndDriverId(rideId, driverId)
				.orElseThrow(() -> new IllegalStateException("No offer exists for this ride and driver"));

		if (offer.getStatus() != RideOfferStatus.OFFERED) {
			throw new IllegalStateException("This ride offer is no longer available");
		}

		offer.setStatus(RideOfferStatus.REJECTED);
		offer.setRespondedAt(LocalDateTime.now());

		rideOfferRepository.save(offer);
	}

	private RideOfferResponseDTO mapToResponse(RideOffer offer) {

		Ride ride = offer.getRide();

		return new RideOfferResponseDTO(offer.getId(), ride.getId(), ride.getPickupAddress(), ride.getDropoffAddress(),
				ride.getEstimatedFare(), ride.getStatus(), offer.getStatus(), offer.getOfferedAt());
	}
}
