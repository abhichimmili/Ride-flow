package com.vcube.rideflow.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcube.rideflow.dto.response.NearbyDriverResponseDTO;
import com.vcube.rideflow.entity.DriverProfile;
import com.vcube.rideflow.entity.Ride;
import com.vcube.rideflow.entity.RideOffer;
import com.vcube.rideflow.entity.enums.RideStatus;
import com.vcube.rideflow.exception.DriverProfileNotFoundException;
import com.vcube.rideflow.repository.DriverProfileRepository;
import com.vcube.rideflow.repository.RideOfferRepository;
import com.vcube.rideflow.repository.RideRepository;

@Service
public class RideDispatchService {

	private final RideRepository rideRepository;
	private final RideOfferRepository rideOfferRepository;
	private final DriverProfileRepository driverProfileRepository;

	public RideDispatchService(RideRepository rideRepository, RideOfferRepository rideOfferRepository,
			DriverProfileRepository driverProfileRepository) {
		this.rideRepository = rideRepository;
		this.rideOfferRepository = rideOfferRepository;
		this.driverProfileRepository = driverProfileRepository;
	}

	@Transactional
	public void dispatchRide(Long rideId, List<NearbyDriverResponseDTO> nearbyDrivers) {

		Ride ride = rideRepository.findById(rideId)
				.orElseThrow(() -> new IllegalArgumentException("Ride not found: " + rideId));

		if (ride.getStatus() != RideStatus.REQUESTED) {
			throw new IllegalStateException("Only REQUESTED rides can be dispatched");
		}

		if (nearbyDrivers == null || nearbyDrivers.isEmpty()) {
			return;
		}

		for (NearbyDriverResponseDTO nearbyDriver : nearbyDrivers) {

			rideOfferRepository.insertOfferIfAbsent(rideId, nearbyDriver.driverProfileId());
		}
	}
}