package com.vcube.rideflow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vcube.rideflow.service.RideService;

@RestController
@RequestMapping("/api/driver/rides")
public class DriverRideController {

    private final RideService rideService;

    public DriverRideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PatchMapping("/{rideId}/arriving")
    public ResponseEntity<Void> markArriving(
            @PathVariable Long rideId,
            Authentication authentication) {

        rideService.markDriverArriving(
                rideId,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{rideId}/arrived")
    public ResponseEntity<Void> markArrived(
            @PathVariable Long rideId,
            Authentication authentication) {

        rideService.markDriverArrived(
                rideId,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{rideId}/start")
    public ResponseEntity<Void> startRide(
            @PathVariable Long rideId,
            Authentication authentication) {

        rideService.startRide(
                rideId,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{rideId}/complete")
    public ResponseEntity<Void> completeRide(
            @PathVariable Long rideId,
            Authentication authentication) {

        rideService.completeRide(
                rideId,
                authentication.getName()
        );

        return ResponseEntity.noContent().build();
    }
}