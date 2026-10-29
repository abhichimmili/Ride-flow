package com.vcube.rideflow.entity;

import java.time.LocalDateTime;

import com.vcube.rideflow.entity.enums.RideOfferStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "ride_offers", uniqueConstraints = {
		@UniqueConstraint(name = "uk_ride_offer_ride_driver", columnNames = { "ride_id", "driver_id" }) })
public class RideOffer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "ride_id", nullable = false)
	private Ride ride;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "driver_id", nullable = false)
	private DriverProfile driver;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RideOfferStatus status = RideOfferStatus.OFFERED;

	@Column(nullable = false)
	private LocalDateTime offeredAt = LocalDateTime.now();

	private LocalDateTime respondedAt;

	public Long getId() {
		return id;
	}

	public Ride getRide() {
		return ride;
	}

	public void setRide(Ride ride) {
		this.ride = ride;
	}

	public DriverProfile getDriver() {
		return driver;
	}

	public void setDriver(DriverProfile driver) {
		this.driver = driver;
	}

	public RideOfferStatus getStatus() {
		return status;
	}

	public void setStatus(RideOfferStatus status) {
		this.status = status;
	}

	public LocalDateTime getOfferedAt() {
		return offeredAt;
	}

	public LocalDateTime getRespondedAt() {
		return respondedAt;
	}

	public void setRespondedAt(LocalDateTime respondedAt) {
		this.respondedAt = respondedAt;
	}
}