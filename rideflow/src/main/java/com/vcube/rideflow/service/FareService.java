package com.vcube.rideflow.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FareService {

	private final BigDecimal baseFare;
	private final BigDecimal perKmRate;

	public FareService(@Value("${ride.fare.base}") BigDecimal baseFare,
			@Value("${ride.fare.per-km}") BigDecimal perKmRate) {
		if (baseFare.signum() < 0 || perKmRate.signum() < 0) {
			throw new IllegalArgumentException("Fare configuration values cannot be negative");
		}
		this.baseFare = baseFare;
		this.perKmRate = perKmRate;
	}

	public BigDecimal calculateEstimatedFare(BigDecimal distanceKm) {

		if (distanceKm == null || distanceKm.signum() <= 0) {
			throw new IllegalArgumentException("Distance must be greater than zero");
		}

		BigDecimal distanceFare = distanceKm.multiply(perKmRate);

		return baseFare.add(distanceFare).setScale(2, RoundingMode.HALF_UP);

	}
}
