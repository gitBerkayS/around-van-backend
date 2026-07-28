package com.aroundvan.backend.gas.geocoding.nominatim;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NominatimSearchResult(
        String lat,
        String lon
) {
}
