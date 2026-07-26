package com.aroundvan.backend.events.ticketmaster;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ticketmaster")
public record TicketmasterProperties(String baseUrl, String apiKey) {
}
