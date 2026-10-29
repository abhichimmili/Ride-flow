package com.vcube.rideflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vcube.rideflow.entity.RideOffer;
import com.vcube.rideflow.entity.enums.RideOfferStatus;

public interface RideOfferRepository extends JpaRepository<RideOffer, Long> {

	List<RideOffer> findByRideId(Long rideId);

	Optional<RideOffer> findByRideIdAndDriverId(Long rideId, Long driverId);

	List<RideOffer> findByDriverIdAndStatus(Long driverId, RideOfferStatus status);

	List<RideOffer> findByDriverIdAndStatusOrderByOfferedAtAsc(Long driverId, RideOfferStatus status);
}