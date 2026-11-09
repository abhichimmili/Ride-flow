package com.vcube.rideflow.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.vcube.rideflow.dto.response.FareEstimateDTO;
import com.vcube.rideflow.entity.VehicleFare;
import com.vcube.rideflow.entity.enums.VehicleType;
import com.vcube.rideflow.repository.VehicleFareRepository;

@Service
public class FareService {

	private final VehicleFareRepository vehicleFareRepository;

	public FareService(VehicleFareRepository vehicleFareRepository) {
		this.vehicleFareRepository = vehicleFareRepository;
	}

	@Transactional(readOnly = true)
	public List<FareEstimateDTO> estimateAllFares(BigDecimal distanceKm) {

		validateDistance(distanceKm);

		return vehicleFareRepository.findAllByActiveTrueOrderByVehicleTypeAsc().stream()
				.map(fare -> calculateFare(fare, distanceKm)).toList();
	}

	@Transactional(readOnly = true)
	public FareEstimateDTO estimateFare(VehicleType vehicleType, BigDecimal distanceKm) {

		validateDistance(distanceKm);

		VehicleFare fare = vehicleFareRepository.findByVehicleTypeAndActiveTrue(vehicleType)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"No active fare configured for " + vehicleType));

		return calculateFare(fare, distanceKm);
	}

	private FareEstimateDTO calculateFare(VehicleFare fare, BigDecimal distanceKm) {

		BigDecimal distanceFare = distanceKm.multiply(fare.getPerKmRate());

		BigDecimal calculatedFare = fare.getBaseFare().add(distanceFare);

		BigDecimal estimatedFare = calculatedFare.max(fare.getMinimumFare()).setScale(2, RoundingMode.HALF_UP);

		return new FareEstimateDTO(fare.getVehicleType(), distanceKm, fare.getBaseFare(), fare.getPerKmRate(),
				fare.getMinimumFare(), estimatedFare);
	}

	private void validateDistance(BigDecimal distanceKm) {

		if (distanceKm == null || distanceKm.signum() <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Distance must be greater than zero");
		}
	}
}