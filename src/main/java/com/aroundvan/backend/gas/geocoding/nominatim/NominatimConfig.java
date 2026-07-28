package com.aroundvan.backend.gas.geocoding.nominatim;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(NominatimProperties.class)
public class NominatimConfig {

    @Bean("nominatimRestClient")
    public RestClient nominatimRestClient(
            RestClient.Builder builder,
            NominatimProperties properties
    ) {
        return builder
                .baseUrl(properties.baseUrlOrDefault())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, properties.userAgentOrDefault())
                .build();
    }
}
