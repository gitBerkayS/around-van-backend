package com.aroundvan.backend.environment.wildfire.fireweather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BcwsDailyListResponse(

        Integer totalRowCount,

        List<Daily> collection
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Daily(

            String recordType,

            String stationName,

            String stationCode,

            String stationAcronym,

            String fireCentre,

            String weatherTimestamp,

            Double temperature,

            Double relativeHumidity,

            Double windSpeed,

            Double windDirection,

            Double precipitation,

            Integer dangerForest,

            Double fineFuelMoistureCode,

            Double duffMoistureCode,

            Double droughtCode,

            Double initialSpreadIndex,

            Double buildUpIndex,

            Double fireWeatherIndex,

            Double grasslandCuring,

            Double elevation,

            Geometry geometry
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Geometry(
            List<Double> coordinates
    ) {
        public Double longitude() {
            return coordinates == null || coordinates.size() < 2 ? null : coordinates.get(0);
        }

        public Double latitude() {
            return coordinates == null || coordinates.size() < 2 ? null : coordinates.get(1);
        }
    }
}
