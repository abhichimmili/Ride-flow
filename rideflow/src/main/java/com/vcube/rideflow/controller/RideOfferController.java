package com.vcube.rideflow.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vcube.rideflow.dto.response.RideOfferResponseDTO;
import com.vcube.rideflow.service.RideOfferService;

@RestController
@RequestMapping("/api/driver/offers")
public class RideOfferController {

	private final RideOfferService rideOfferService;

	public RideOfferController(RideOfferService rideOfferService) {
		this.rideOfferService = rideOfferService;
	}

	@GetMapping
	public List<RideOfferResponseDTO> getMyOffers(Authentication authentication) {

		return rideOfferService.getMyOffers(authentication.getName());
	}

	@PutMapping("/{rideId}/accept")
	public void acceptOffer(@PathVariable Long rideId, Authentication authentication) {

		rideOfferService.acceptOffer(rideId, authentication.getName());
	}

	@PutMapping("/{rideId}/reject")
	public void rejectOffer(@PathVariable Long rideId, Authentication authentication) {

		rideOfferService.rejectOffer(rideId, authentication.getName());
	}
}