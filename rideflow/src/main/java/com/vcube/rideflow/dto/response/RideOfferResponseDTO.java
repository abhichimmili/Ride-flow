package com.vcube.rideflow.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.vcube.rideflow.entity.enums.RideOfferStatus;
import com.vcube.rideflow.entity.enums.RideStatus;

public record RideOfferResponseDTO(
        Long offerId,
        Long rideId,
        String pickupAddress,
        String dropoffAddress,
        BigDecimal estimatedFare,
        RideStatus rideStatus,
        RideOfferStatus offerStatus,
        LocalDateTime offeredAt
) {
}
