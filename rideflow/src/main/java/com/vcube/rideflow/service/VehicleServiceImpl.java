package com.vcube.rideflow.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vcube.rideflow.dto.request.VehicleRequestDTO;
import com.vcube.rideflow.dto.response.VehicleResponseDTO;
import com.vcube.rideflow.entity.DriverProfile;
import com.vcube.rideflow.entity.Vehicle;
import com.vcube.rideflow.exception.DriverProfileNotFoundException;
import com.vcube.rideflow.exception.DuplicateVehicleException;
import com.vcube.rideflow.exception.VehicleNotFoundException;
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

    // 1. Register a vehicle
    @Override
    public VehicleResponseDTO registerVehicle(VehicleRequestDTO request) {

        if (vehicleRepository.existsByRegistrationNumber(
                request.getRegistrationNumber())) {

        	throw new DuplicateVehicleException(
                    "Registration number already exists"
            );
        }

        DriverProfile driver = driverProfileRepository
                .findById(request.getDriverId())
                .orElseThrow(() -> new DriverProfileNotFoundException(
                        "Driver profile not found"));

        Vehicle vehicle = new Vehicle();

        vehicle.setDriver(driver);
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setBrand(request.getBrand());
        vehicle.setModel(request.getModel());
        vehicle.setColor(request.getColor());
        vehicle.setVehicleType(request.getVehicleType());

        // Newly registered vehicles require verification.
        vehicle.setVerified(false);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return mapToResponse(savedVehicle);
    }

    // 2. Get a vehicle by ID
    @Override
    @Transactional(readOnly = true)
    public VehicleResponseDTO getVehicleById(Long id) {

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(
                        "Vehicle not found with ID: " + id));

        return mapToResponse(vehicle);
    }

    // 3. Get all vehicles
    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponseDTO> getAllVehicles() {

        return vehicleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 4. Update vehicle details
    @Override
    public VehicleResponseDTO updateVehicle(
            Long id,
            VehicleRequestDTO updatedVehicle) {

        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(
                        "Vehicle not found with ID: " + id));

        // Update registration number
        if (updatedVehicle.getRegistrationNumber() != null
                && !updatedVehicle.getRegistrationNumber()
                        .equals(existingVehicle.getRegistrationNumber())) {

            if (vehicleRepository.existsByRegistrationNumber(
                    updatedVehicle.getRegistrationNumber())) {

                throw new DuplicateVehicleException(
                        "Vehicle registration number already exists");
            }

            existingVehicle.setRegistrationNumber(
                    updatedVehicle.getRegistrationNumber());
        }

        // Update brand
        if (updatedVehicle.getBrand() != null) {

            if (updatedVehicle.getBrand().isBlank()) {
                throw new IllegalArgumentException(
                        "Brand cannot be blank");
            }

            existingVehicle.setBrand(updatedVehicle.getBrand());
        }

        // Update model
        if (updatedVehicle.getModel() != null) {

            if (updatedVehicle.getModel().isBlank()) {
                throw new IllegalArgumentException(
                        "Model cannot be blank");
            }

            existingVehicle.setModel(updatedVehicle.getModel());
        }

        // Update color
        if (updatedVehicle.getColor() != null) {
            existingVehicle.setColor(updatedVehicle.getColor());
        }

        // Update vehicle type
        if (updatedVehicle.getVehicleType() != null) {
            existingVehicle.setVehicleType(
                    updatedVehicle.getVehicleType());
        }

        Vehicle savedVehicle = vehicleRepository.save(existingVehicle);

        return mapToResponse(savedVehicle);
    }

    // 5. Delete a vehicle
    @Override
    public void deleteVehicle(Long id) {

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(
                        "Vehicle not found with ID: " + id));

        vehicleRepository.delete(vehicle);
    }

    // Convert Vehicle entity into VehicleResponseDTO
    private VehicleResponseDTO mapToResponse(Vehicle vehicle) {

        return new VehicleResponseDTO(
                vehicle.getId(),
                vehicle.getDriver().getId(),
                vehicle.getRegistrationNumber(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getColor(),
                vehicle.getVehicleType(),
                vehicle.getVerified()
        );
    }
}