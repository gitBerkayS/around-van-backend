package com.aroundvan.backend.events.ticketmaster;

import com.aroundvan.backend.events.provider.EventProvider;
import com.aroundvan.backend.events.provider.ExternalEventData;
import com.aroundvan.backend.events.ticketmaster.dto.TicketmasterEventsResponse;
import com.aroundvan.backend.events.ticketmaster.dto.TicketmasterEventsResponse.Image;
import com.aroundvan.backend.events.ticketmaster.dto.TicketmasterEventsResponse.TicketmasterEvent;
import com.aroundvan.backend.events.ticketmaster.dto.TicketmasterEventsResponse.Venue;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Component
public class TicketmasterEventMapper {

    public List<ExternalEventData> toExternalEvents(
            TicketmasterEventsResponse response
    ) {
        if (response.embedded() == null
                || response.embedded().events() == null) {
            return List.of();
        }

        return response.embedded()
                .events()
                .stream()
                .map(this::toExternalEvent)
                .toList();
    }

    public ExternalEventData toExternalEvent(
            TicketmasterEvent event
    ) {
        Venue venue = getFirstVenue(event);

        Double latitude = null;
        Double longitude = null;

        if (venue != null && venue.location() != null) {
            latitude = parseCoordinate(
                    venue.location().latitude()
            );

            longitude = parseCoordinate(
                    venue.location().longitude()
            );
        }

        if (Double.valueOf(0).equals(latitude)
                && Double.valueOf(0).equals(longitude)) {
            latitude = null;
            longitude = null;
        }

        return new ExternalEventData(
                EventProvider.TICKETMASTER,
                event.id(),
                event.name(),
                event.url(),
                getStartDate(event),
                getBestImageUrl(event.images()),
                venue != null ? venue.name() : null,
                getCityName(venue),
                latitude,
                longitude
        );
    }

    private Instant getStartDate(TicketmasterEvent event) {
        if (event.dates() == null
                || event.dates().start() == null) {
            return null;
        }

        return event.dates().start().dateTime();
    }

    private Venue getFirstVenue(TicketmasterEvent event) {
        if (event.embedded() == null
                || event.embedded().venues() == null
                || event.embedded().venues().isEmpty()) {
            return null;
        }

        return event.embedded().venues().getFirst();
    }

    private String getCityName(Venue venue) {
        if (venue == null || venue.city() == null) {
            return null;
        }

        return venue.city().name();
    }

    private String getBestImageUrl(List<Image> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }

        return images.stream()
                .filter(image -> !image.fallback())
                .max(Comparator.comparingInt(Image::width))
                .or(() -> images.stream()
                        .max(Comparator.comparingInt(Image::width))
                )
                .map(Image::url)
                .orElse(null);
    }

    private Double parseCoordinate(String coordinate) {
        if (coordinate == null || coordinate.isBlank()) {
            return null;
        }

        try {
            return Double.parseDouble(coordinate);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}