package com.vcube.rideflow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class FareServiceTest {

    private final FareService fareService =
            new FareService(
                    new BigDecimal("50.00"),
                    new BigDecimal("12.00"));

    @Test
    void shouldCalculateEstimatedFare() {

        BigDecimal distance = new BigDecimal("12.50");

        BigDecimal fare =
                fareService.calculateEstimatedFare(distance);

        assertEquals(new BigDecimal("200.00"), fare);
    }
}
