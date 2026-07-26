package com.aroundvan.backend.environment.weather.dto;

import java.time.Instant;

public record WeatherResponse(
        double latitude,
        double longitude,
        String neighbourhood,
        String summary,
        String icon,
        Integer iconNumber,
        Double temperature,
        Double feelsLike,
        Double humidity,
        Double pressure,
        Double uvIndex,
        Double windSpeed,
        Integer windAngle,
        String windDirection,
        Double precipitation,
        String precipitationType,
        Integer cloudCoverPercent,
        String units,
        Instant fetchedAt
) {
}
