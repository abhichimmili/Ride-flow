package com.vcube.rideflow.entity;

import java.time.LocalDateTime;

import com.vcube.rideflow.entity.enums.AvailabilityStatus;
import org.locationtech.jts.geom.Point;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
@Table(name = "driver_profiles", uniqueConstraints = {
		@UniqueConstraint(name = "uk_driver_license", columnNames = "license_number") })
public class DriverProfile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Column(name = "license_number", nullable = false, unique = true)
	private String licenseNumber;

	@Column(name = "license_verified", nullable = false)
	private Boolean licenseVerified;

	@Enumerated(EnumType.STRING)
	@Column(name = "availability_status", nullable = false)
	private AvailabilityStatus availabilityStatus;
	
	@Column(name = "location", columnDefinition = "geography(Point, 4326)")
	private Point location;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public DriverProfile() {
	}
}
