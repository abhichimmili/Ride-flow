package com.vcube.rideflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vcube.rideflow.entity.DriverProfile;
import com.vcube.rideflow.entity.Ride;
import com.vcube.rideflow.entity.enums.RideStatus;

public interface RideRepository extends JpaRepository<Ride, Long> {

	// Find all rides requested by a particular customer
	List<Ride> findByCustomerIdOrderByRequestedAtDesc(Long customerId);

	// Find a particular ride belonging to a particular customer
	Optional<Ride> findByIdAndCustomerId(Long id, Long customerId);

	// Find rides by status
	List<Ride> findByStatus(RideStatus status);

	// Check whether a ride belongs to a particular customer
	boolean existsByIdAndCustomerId(Long id, Long customerId);

	@Modifying
	@Query("""
			UPDATE Ride r
			SET r.driver = :driver,
			    r.status = :assignedStatus
			WHERE r.id = :rideId
			  AND r.status = :requestedStatus
			  AND r.driver IS NULL
			""")
	int acceptRide(@Param("rideId") Long rideId, @Param("driver") DriverProfile driver,
			@Param("requestedStatus") RideStatus requestedStatus, @Param("assignedStatus") RideStatus assignedStatus);
}
