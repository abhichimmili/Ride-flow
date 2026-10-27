package com.vcube.rideflow.dto.response;

public record NearbyDriverResponseDTO(
        Long driverProfileId,
        Long userId,
        String licenseNumber,
        Boolean licenseVerified,
        String availabilityStatus,
        Double distanceKm
) {
}
