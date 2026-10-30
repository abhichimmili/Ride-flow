package com.vcube.rideflow.dto.request;

import jakarta.validation.constraints.NotNull;

public record LicenseVerificationRequestDTO(

		@NotNull(message = "Verification decision is required") Boolean verified

) {
}