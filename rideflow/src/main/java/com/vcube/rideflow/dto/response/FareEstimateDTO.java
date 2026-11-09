package com.vcube.rideflow.dto.response;

import java.math.BigDecimal;

import com.vcube.rideflow.entity.enums.VehicleType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FareEstimateDTO {

    private VehicleType vehicleType;

    private BigDecimal distanceKm;

    private BigDecimal baseFare;

    private BigDecimal perKmRate;

    private BigDecimal minimumFare;

    private BigDecimal estimatedFare;
}