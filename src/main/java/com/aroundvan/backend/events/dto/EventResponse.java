package com.aroundvan.backend.events.dto;

import com.aroundvan.backend.events.provider.EventProvider;

import java.time.Instant;

public record EventResponse(
        long id,
        String title,
        String description,
        Instant dateStart,
        Instant dateEnd,
        String externalUrl,
        String imageUrl,
        EventProvider provider
) {
}