package com.aroundvan.backend.events.ticketmaster.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TicketmasterEventsResponse(

        @JsonProperty("_embedded")
        Embedded embedded,

        Page page
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Embedded(
            List<TicketmasterEvent> events
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TicketmasterEvent(
            String id,
            String name,
            String url,
            String info,
            String pleaseNote,
            Dates dates,
            Sales sales,
            List<Image> images,

            @JsonProperty("_embedded")
            EventEmbedded embedded
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Dates(
            Start start,
            End end
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Start(
            LocalDate localDate,
            LocalTime localTime,
            Instant dateTime
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record End(
            LocalDate localDate,
            LocalTime localTime,
            Instant dateTime
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Sales(
            @JsonProperty("public")
            PublicSales publicSales
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PublicSales(
            Instant startDateTime,
            Instant endDateTime
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Image(
            String url,
            String ratio,
            int width,
            int height,
            boolean fallback
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EventEmbedded(
            List<Venue> venues
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Venue(
            String id,
            String name,
            City city,
            State state,
            Country country,
            Location location
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record City(
            String name
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record State(
            String name,
            String stateCode
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Country(
            String name,
            String countryCode
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Location(
            String longitude,
            String latitude
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Page(
            int size,
            int totalElements,
            int totalPages,
            int number
    ) {
    }
}