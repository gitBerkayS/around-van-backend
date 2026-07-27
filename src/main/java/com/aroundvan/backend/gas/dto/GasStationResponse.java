package com.aroundvan.backend.gas.dto;

import com.aroundvan.backend.gas.FuelType;

import java.math.BigDecimal;
import java.time.Instant;

public record GasStationResponse(
        Long id,
        String name,
        String address,
        String postalCodePrefix,
        Double latitude,
        Double longitude,
        Double distanceKm,
        FuelType fuelType,
        BigDecimal price,
        Instant observedAt
) {
}
