package com.vcube.rideflow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vcube.rideflow.entity.DriverProfile;

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
			WHERE dp.location IS NOT NULL
			  AND dp.availability_status = 'AVAILABLE'
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
	int updateDriverLocation(
	        @Param("driverProfileId") Long driverProfileId,
	        @Param("latitude") double latitude,
	        @Param("longitude") double longitude
	);
}
