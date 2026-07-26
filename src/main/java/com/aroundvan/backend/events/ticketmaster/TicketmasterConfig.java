package com.aroundvan.backend.events.ticketmaster;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(TicketmasterProperties.class)
public class TicketmasterConfig {
    @Bean("ticketmasterRestClient")
    public RestClient ticketmasterRestClient(RestClient.Builder builder, TicketmasterProperties properties) {
        return builder
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
