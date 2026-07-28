package com.aroundvan.backend.gas.geocoding.nominatim;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;

@Component
public class NominatimClient {

    private static final ParameterizedTypeReference<List<NominatimSearchResult>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient nominatimRestClient;
    private final NominatimProperties properties;

    public NominatimClient(
            RestClient nominatimRestClient,
            NominatimProperties properties
    ) {
        this.nominatimRestClient = nominatimRestClient;
        this.properties = properties;
    }

    public Optional<Coordinates> geocode(String address) {
        try {
            List<NominatimSearchResult> results = nominatimRestClient
                    .get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder
                                .path("/search")
                                .queryParam("q", address)
                                .queryParam("format", "jsonv2")
                                .queryParam("limit", 1)
                                .queryParam("countrycodes", properties.countryCodesOrDefault())
                                .queryParam("addressdetails", 0);

                        if (properties.hasEmail()) {
                            builder.queryParam("email", properties.email());
                        }

                        return builder.build();
                    })
                    .retrieve()
                    .body(RESPONSE_TYPE);

            if (results == null || results.isEmpty()) {
                return Optional.empty();
            }

            NominatimSearchResult top = results.getFirst();
            if (top.lat() == null || top.lon() == null) {
                return Optional.empty();
            }

            return Optional.of(new Coordinates(
                    Double.parseDouble(top.lat()),
                    Double.parseDouble(top.lon())
            ));
        } catch (RestClientException | NumberFormatException exception) {
            return Optional.empty();
        }
    }

    public record Coordinates(double latitude, double longitude) {
    }
}
