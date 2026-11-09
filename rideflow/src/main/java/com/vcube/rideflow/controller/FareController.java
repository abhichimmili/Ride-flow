package com.vcube.rideflow.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.vcube.rideflow.dto.response.FareEstimateDTO;
import com.vcube.rideflow.entity.enums.VehicleType;
import com.vcube.rideflow.service.FareService;

@RestController
@RequestMapping("/api/fares")
public class FareController {

    private final FareService fareService;

    public FareController(FareService fareService) {
        this.fareService = fareService;
    }

    @GetMapping("/estimate")
    public ResponseEntity<List<FareEstimateDTO>> estimateAllFares(
            @RequestParam BigDecimal distanceKm) {

        return ResponseEntity.ok(
                fareService.estimateAllFares(distanceKm));
    }

    @GetMapping("/estimate/{vehicleType}")
    public ResponseEntity<FareEstimateDTO> estimateFare(
            @PathVariable VehicleType vehicleType,
            @RequestParam BigDecimal distanceKm) {

        return ResponseEntity.ok(
                fareService.estimateFare(vehicleType, distanceKm));
    }
}