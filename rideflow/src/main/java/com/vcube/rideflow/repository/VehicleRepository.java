package com.vcube.rideflow.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.vcube.rideflow.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
	boolean existsByRegistrationNumber(String registrationNumber);
	Optional<Vehicle> findByRegistrationNumber(String registrationNumber);
}