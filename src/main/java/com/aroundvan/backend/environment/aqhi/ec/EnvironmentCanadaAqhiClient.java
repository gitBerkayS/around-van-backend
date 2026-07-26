package com.aroundvan.backend.environment.aqhi.ec;

import com.aroundvan.backend.environment.aqhi.ec.dto.AqhiObservationsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EnvironmentCanadaAqhiClient {

    private final RestClient environmentCanadaAqhiRestClient;
    private final EnvironmentCanadaAqhiProperties properties;

    public List<AqhiObservationsResponse.Feature> fetchLatestObservations() {
        AqhiObservationsResponse response = environmentCanadaAqhiRestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/collections/aqhi-observations-realtime/items")
                        .queryParam("f", "json")
                        .queryParam("latest", "true")
                        .queryParam("bbox", properties.boundingBox())
                        .queryParam("limit", properties.limitOrDefault())
                        .build()
                )
                .retrieve()
                .body(AqhiObservationsResponse.class);

        return response == null || response.features() == null
                ? List.of()
                : response.features();
    }
}
