package com.vcube.rideflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vcube.rideflow.entity.DriverProfile;
import com.vcube.rideflow.entity.enums.AvailabilityStatus;

@Repository
public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {

	boolean existsByUser_Id(Long userId);

	boolean existsByLicenseNumber(String licenseNumber);

	@Query(value = """
			SELECT
			    dp.id AS driver_profile_id,
			    dp.user_id AS user_id,
			    dp.license_number AS license_number,
			    dp.license_verified AS license_verified,
			    dp.availability_status AS availability_status,
			    ST_Distance(
			        dp.location,
			        ST_SetSRID(
			            ST_MakePoint(:longitude, :latitude),
			            4326
			        )::geography
			    ) / 1000.0 AS distance_km
			FROM driver_profiles dp
			JOIN users u ON u.id = dp.user_id
			WHERE dp.location IS NOT NULL
			  AND dp.availability_status = 'ONLINE'
			  AND dp.license_verified = TRUE
			  AND u.active = TRUE
			  AND ST_DWithin(
			        dp.location,
			        ST_SetSRID(
			            ST_MakePoint(:longitude, :latitude),
			            4326
			        )::geography,
			        :radiusMeters
			  )
			ORDER BY distance_km ASC
			""", nativeQuery = true)
	List<Object[]> findNearbyAvailableDrivers(@Param("latitude") double latitude, @Param("longitude") double longitude,
			@Param("radiusMeters") double radiusMeters);

	@Modifying
	@Query(value = """
			UPDATE driver_profiles
			SET location =
			    ST_SetSRID(
			        ST_MakePoint(:longitude, :latitude),
			        4326
			    )::geography
			WHERE id = :driverProfileId
			""", nativeQuery = true)
	int updateDriverLocation(@Param("driverProfileId") Long driverProfileId, @Param("latitude") double latitude,
			@Param("longitude") double longitude);
	
	@Modifying
	@Query("""
	    UPDATE DriverProfile d
	    SET d.availabilityStatus = :newStatus
	    WHERE d.id = :driverId
	      AND d.availabilityStatus = :expectedStatus
	      AND d.licenseVerified = true
	""")
	int changeAvailabilityIfCurrentStatusMatches(
	        @Param("driverId") Long driverId,
	        @Param("expectedStatus") AvailabilityStatus expectedStatus,
	        @Param("newStatus") AvailabilityStatus newStatus
	);
	
	//For concurrency of driver assignment
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
	    UPDATE DriverProfile d
	    SET d.availabilityStatus = :onTrip
	    WHERE d.id = :driverId
	      AND d.availabilityStatus = :online
	      AND d.licenseVerified = true
	""")
	int claimDriverForRide(
	        @Param("driverId") Long driverId,
	        @Param("online") AvailabilityStatus online,
	        @Param("onTrip") AvailabilityStatus onTrip
	);
	
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
	    UPDATE DriverProfile d
	    SET d.availabilityStatus = :onlineStatus
	    WHERE d.id = :driverId
	      AND d.availabilityStatus = :onTripStatus
	""")
	int restoreDriverAvailability(
	        @Param("driverId") Long driverId,
	        @Param("onTripStatus") AvailabilityStatus onTripStatus,
	        @Param("onlineStatus") AvailabilityStatus onlineStatus
	);
	
	Optional<DriverProfile> findByUser_Email(String email);
}
