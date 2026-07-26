package com.aroundvan.backend.environment.aqhi.ec.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AqhiObservationsResponse(List<Feature> features) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Feature(Geometry geometry, Properties properties) {
    }

    // GeoJSON coordinates are long then lat
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Geometry(List<Double> coordinates) {
        public Double longitude() {
            return coordinates == null || coordinates.size() < 2 ? null : coordinates.get(0);
        }

        public Double latitude() {
            return coordinates == null || coordinates.size() < 2 ? null : coordinates.get(1);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Properties(String location_id, String location_name_en, Double aqhi, Instant observation_datetime, Boolean latest) {
    }
}
