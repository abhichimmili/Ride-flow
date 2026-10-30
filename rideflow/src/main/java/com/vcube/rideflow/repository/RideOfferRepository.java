package com.vcube.rideflow.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vcube.rideflow.entity.RideOffer;
import com.vcube.rideflow.entity.enums.RideOfferStatus;

public interface RideOfferRepository extends JpaRepository<RideOffer, Long> {

	List<RideOffer> findByRideId(Long rideId);

	Optional<RideOffer> findByRideIdAndDriverId(Long rideId, Long driverId);

	List<RideOffer> findByDriverIdAndStatus(Long driverId, RideOfferStatus status);

	List<RideOffer> findByDriverIdAndStatusOrderByOfferedAtAsc(Long driverId, RideOfferStatus status);

	@Modifying(flushAutomatically = true)
	@Query("""
			    UPDATE RideOffer o
			    SET o.status = :cancelledStatus,
			        o.respondedAt = :respondedAt
			    WHERE o.ride.id = :rideId
			      AND o.status = :offeredStatus
			""")
	int cancelPendingOffers(@Param("rideId") Long rideId, @Param("offeredStatus") RideOfferStatus offeredStatus,
			@Param("cancelledStatus") RideOfferStatus cancelledStatus, @Param("respondedAt") LocalDateTime respondedAt);

	@Modifying
	@Query(value = """
			INSERT INTO ride_offers (
			    ride_id,
			    driver_id,
			    status,
			    offered_at
			)
			VALUES (
			    :rideId,
			    :driverId,
			    'OFFERED',
			    CURRENT_TIMESTAMP
			)
			ON CONFLICT (ride_id, driver_id) DO NOTHING
			""", nativeQuery = true)
	int insertOfferIfAbsent(@Param("rideId") Long rideId, @Param("driverId") Long driverId);
}