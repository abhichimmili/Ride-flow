package com.vcube.rideflow.entity;

import java.time.LocalDateTime;

import com.vcube.rideflow.entity.enums.VehicleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "vehicles",
	uniqueConstraints= {
			@jakarta.persistence.UniqueConstraint(
					name="uk_vehicle_registration_number",
					columnNames="registration_number"
			)
	}
)
@Data
public class Vehicle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "driver_profile_id", nullable = false)
	private DriverProfile driver;

	@Column(name = "registration_number", nullable = false, unique = true)
	private String registrationNumber;

	@Column(nullable = false)
	private String brand;

	@Column(nullable = false)
	private String model;

	private String color;

	@Enumerated(EnumType.STRING)
	@Column(name = "vehicle_type", nullable = false)
	private VehicleType vehicleType;

	@Column(nullable = false)
	private Boolean verified = false;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

}
