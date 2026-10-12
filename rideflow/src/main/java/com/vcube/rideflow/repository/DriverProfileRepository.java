package com.vcube.rideflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vcube.rideflow.entity.DriverProfile;

@Repository
public interface DriverProfileRepository extends JpaRepository<DriverProfile, Long> {

}
