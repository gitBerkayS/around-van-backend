package com.aroundvan.backend.servicerequest.dto;

import java.time.Instant;

public record ServiceRequestResponse(
        Long id,
        String requestType,
        String category,
        String importance,
        String status,
        String address,
        String neighbourhood,
        String localArea,
        Double latitude,
        Double longitude,
        Double distanceKm,
        Instant openedAt,
        boolean seen
) {
}
