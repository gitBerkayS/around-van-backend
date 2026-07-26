package com.aroundvan.backend.events.provider;

import java.time.Instant;

public record ExternalEventData(EventProvider provider, String externalId, String name, String description, String url, Instant startDate, Instant endDate, Instant publishedDate, String imageUrl, String venueName, String city, Double latitude, Double longitude) {
}
