package com.vcube.rideflow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vcube.rideflow.entity.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
	boolean existsByRegistrationNumber(String registrationNumber);

	Optional<Vehicle> findByRegistrationNumber(String registrationNumber);
}