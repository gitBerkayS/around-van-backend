package com.aroundvan.backend.environment.weather.meteosource;

import com.aroundvan.backend.environment.weather.meteosource.dto.MeteosourcePointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class MeteosourceClient {

    private final RestClient meteosourceRestClient;
    private final MeteosourceProperties properties;

    public MeteosourcePointResponse fetchCurrentWeather(double latitude, double longitude) {
        if (!properties.isConfigured()) {
            throw new IllegalStateException("Meteosource API key is not configured");
        }

        return meteosourceRestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/point")
                        .queryParam("lat", latitude)
                        .queryParam("lon", longitude)
                        .queryParam("sections", "current")
                        .queryParam("timezone", "UTC")
                        .queryParam("language", properties.languageOrDefault())
                        .queryParam("units", properties.unitsOrDefault())
                        .queryParam("key", properties.apiKey())
                        .build()
                )
                .retrieve()
                .body(MeteosourcePointResponse.class);
    }
}
