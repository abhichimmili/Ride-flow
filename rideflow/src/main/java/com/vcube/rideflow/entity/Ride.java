package com.vcube.rideflow.entity;

import java.math.BigDecimal;
import org.locationtech.jts.geom.Point;
import java.time.LocalDateTime;

import com.vcube.rideflow.entity.enums.RideStatus;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "rides")
@Data
public class Ride {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Customer who requested the ride
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_id", nullable = false)
	private User customer;

	// Driver will be assigned later
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "driver_id")
	private DriverProfile driver;

	// Vehicle will be assigned later
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle_id")
	private Vehicle vehicle;

	@Column(name = "pickup_address", nullable = false, length = 255)
	private String pickupAddress;

	@Column(name = "dropoff_address", nullable = false, length = 255)
	private String dropoffAddress;

	@Column(name = "distance_km", nullable = false, precision = 8, scale = 2)
	private BigDecimal distanceKm;

	@Column(name = "estimated_fare", nullable = false, precision = 10, scale = 2)
	private BigDecimal estimatedFare;

	@Column(name = "final_fare", precision = 10, scale = 2)
	private BigDecimal finalFare;

	@Column(name = "pickup_location", columnDefinition = "geography(Point, 4326)")
	private Point pickupLocation;

	@Column(name = "dropoff_location", columnDefinition = "geography(Point, 4326)")
	private Point dropoffLocation;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private RideStatus status;

	@Column(name = "requested_at", nullable = false, updatable = false)
	private LocalDateTime requestedAt;

	@Column(name = "completed_at")
	private LocalDateTime completedAt;

	@PrePersist
	protected void onCreate() {
		if (requestedAt == null) {
			requestedAt = LocalDateTime.now();
		}

		if (status == null) {
			status = RideStatus.REQUESTED;
		}
	}
}
