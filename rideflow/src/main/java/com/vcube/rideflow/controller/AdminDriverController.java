package com.vcube.rideflow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vcube.rideflow.dto.request.LicenseVerificationRequestDTO;
import com.vcube.rideflow.dto.response.DriverProfileResponseDTO;
import com.vcube.rideflow.service.DriverProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/drivers")
public class AdminDriverController {

    private final DriverProfileService driverProfileService;

    public AdminDriverController(
            DriverProfileService driverProfileService) {

        this.driverProfileService = driverProfileService;
    }

    @PatchMapping("/{driverProfileId}/license-verification")
    public ResponseEntity<DriverProfileResponseDTO> verifyDriverLicense(
            @PathVariable Long driverProfileId,
            @Valid @RequestBody LicenseVerificationRequestDTO request) {

        DriverProfileResponseDTO response =
                driverProfileService.updateLicenseVerification(
                        driverProfileId,
                        request.verified()
                );

        return ResponseEntity.ok(response);
    }
}