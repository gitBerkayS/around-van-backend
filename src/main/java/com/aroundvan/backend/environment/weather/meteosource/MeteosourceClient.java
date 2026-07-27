package com.aroundvan.backend.environment.weather.meteosource;

import com.aroundvan.backend.environment.weather.meteosource.dto.MeteosourcePointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class MeteosourceClient {

    private final RestClient meteosourceRestClient;
    private final MeteosourceProperties properties;

    public MeteosourcePointResponse fetchCurrentWeather(double latitude, double longitude) {
        if (!properties.isConfigured()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Meteosource API key is not configured (set METEOSOURCE_API_KEY)"
            );
        }

        try {
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
        } catch (RestClientResponseException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Meteosource rejected the request (" + ex.getStatusCode().value() + ")",
                    ex
            );
        } catch (RestClientException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Meteosource is unreachable",
                    ex
            );
        }
    }
}
