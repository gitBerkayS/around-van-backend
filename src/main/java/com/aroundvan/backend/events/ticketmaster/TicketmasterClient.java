package com.aroundvan.backend.events.ticketmaster;

import com.aroundvan.backend.events.ticketmaster.dto.TicketmasterEventsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class TicketmasterClient {

    private final RestClient ticketmasterRestClient;
    private final TicketmasterProperties properties;

    public TicketmasterEventsResponse getVancouverEvents() {
        TicketmasterEventsResponse response = ticketmasterRestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/events.json")
                        .queryParam("apikey", properties.apiKey())
                        .queryParam("city", "Vancouver")
                        .queryParam("countryCode", "CA")
                        .queryParam("sort", "date,asc")
                        .queryParam("size", 200)
                        .build()
                )
                .retrieve()
                .body(TicketmasterEventsResponse.class);

        if (response == null) {
            throw new IllegalStateException(
                    "Ticketmaster returned an empty response"
            );
        }

        return response;
    }
}