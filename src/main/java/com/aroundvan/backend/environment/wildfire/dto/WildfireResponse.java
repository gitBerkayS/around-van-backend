package com.aroundvan.backend.environment.wildfire.dto;

import java.time.Instant;

public record WildfireResponse(
        Long id,
        String fireNumber,
        String incidentName,
        String geographicDescription,
        String neighbourhood,
        Double latitude,
        Double longitude,
        Double distanceKm,
        Double sizeHectares,
        String status,
        String cause,
        String responseType,
        String fireType,
        Instant ignitionDate,
        boolean fireOfNote,
        String fireUrl,
        Instant lastSyncedAt
) {
}
