package com.aroundvan.backend.environment.wildfire.fireweather.dto;

import com.aroundvan.backend.environment.wildfire.fireweather.FireDangerClass;

import java.time.Instant;

public record FireWeatherResponse(
        String stationCode,
        String stationName,
        String fireCentre,
        Double latitude,
        Double longitude,
        Double distanceKm,
        Integer dangerRating,
        FireDangerClass dangerClass,
        String dangerLabel,
        Double temperature,
        Double relativeHumidity,
        Double windSpeedKmh,
        Double windDirection,
        Double precipitationMm,
        Double fineFuelMoistureCode,
        Double duffMoistureCode,
        Double droughtCode,
        Double initialSpreadIndex,
        Double buildUpIndex,
        Double fireWeatherIndex,
        Instant observedAt
) {
}
