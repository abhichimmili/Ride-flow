package com.vcube.rideflow.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcube.rideflow.entity.DriverProfile;
import com.vcube.rideflow.entity.Vehicle;
import com.vcube.rideflow.repository.DriverProfileRepository;
import com.vcube.rideflow.repository.VehicleRepository;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DriverProfileRepository driverProfileRepository;

    public VehicleServiceImpl(
            VehicleRepository vehicleRepository,
            DriverProfileRepository driverProfileRepository) {
        this.vehicleRepository = vehicleRepository;
        this.driverProfileRepository = driverProfileRepository;
    }

    @Override
    public Vehicle registerVehicle(Vehicle vehicle) {

        if (vehicleRepository.existsByRegistrationNumber(
                vehicle.getRegistrationNumber())) {
            throw new IllegalArgumentException(
                "Vehicle registration number already exists");
        }

        if (vehicle.getBrand() == null || vehicle.getBrand().isBlank()) {
            throw new IllegalArgumentException("Brand cannot be blank");
        }

        if (vehicle.getModel() == null || vehicle.getModel().isBlank()) {
            throw new IllegalArgumentException("Model cannot be blank");
        }

        if (vehicle.getVehicleType() == null) {
            throw new IllegalArgumentException("Vehicle type is required");
        }

        if (vehicle.getDriver() == null ||
                vehicle.getDriver().getId() == null) {
            throw new IllegalArgumentException(
                "Driver profile ID is required");
        }

        DriverProfile driver = driverProfileRepository
                .findById(vehicle.getDriver().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Driver profile not found"));

        vehicle.setDriver(driver);

        // New vehicles require verification.
        vehicle.setVerified(false);

        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Vehicle not found with ID: " + id));
    }

    @Override
    public Vehicle updateVehicle(Long id, Vehicle updatedVehicle) {

        Vehicle existingVehicle = getVehicleById(id);

        if (updatedVehicle.getRegistrationNumber() != null
                && !updatedVehicle.getRegistrationNumber()
                        .equals(existingVehicle.getRegistrationNumber())) {

            if (vehicleRepository.existsByRegistrationNumber(
                    updatedVehicle.getRegistrationNumber())) {
                throw new IllegalArgumentException(
                    "Vehicle registration number already exists");
            }

            existingVehicle.setRegistrationNumber(
                updatedVehicle.getRegistrationNumber());
        }

        if (updatedVehicle.getBrand() != null) {
            if (updatedVehicle.getBrand().isBlank()) {
                throw new IllegalArgumentException(
                    "Brand cannot be blank");
            }
            existingVehicle.setBrand(updatedVehicle.getBrand());
        }

        if (updatedVehicle.getModel() != null) {
            if (updatedVehicle.getModel().isBlank()) {
                throw new IllegalArgumentException(
                    "Model cannot be blank");
            }
            existingVehicle.setModel(updatedVehicle.getModel());
        }

        if (updatedVehicle.getColor() != null) {
            existingVehicle.setColor(updatedVehicle.getColor());
        }

        if (updatedVehicle.getVehicleType() != null) {
            existingVehicle.setVehicleType(
                updatedVehicle.getVehicleType());
        }

        return vehicleRepository.save(existingVehicle);
    }

    @Override
    public void deleteVehicle(Long id) {

        Vehicle vehicle = getVehicleById(id);

        vehicleRepository.delete(vehicle);
    }
}