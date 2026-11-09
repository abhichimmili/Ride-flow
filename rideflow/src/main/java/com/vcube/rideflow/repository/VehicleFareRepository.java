package com.vcube.rideflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vcube.rideflow.entity.VehicleFare;
import com.vcube.rideflow.entity.enums.VehicleType;

public interface VehicleFareRepository extends JpaRepository<VehicleFare, Long> {

	Optional<VehicleFare> findByVehicleTypeAndActiveTrue(VehicleType vehicleType);

	List<VehicleFare> findAllByActiveTrueOrderByVehicleTypeAsc();
}