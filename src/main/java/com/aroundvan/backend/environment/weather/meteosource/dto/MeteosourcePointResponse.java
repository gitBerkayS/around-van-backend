package com.aroundvan.backend.environment.weather.meteosource.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MeteosourcePointResponse(

        Double elevation,

        String timezone,

        String units,

        Current current
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Current(

            String icon,

            @JsonProperty("icon_num")
            Integer iconNumber,

            String summary,

            Double temperature,

            @JsonProperty("feels_like")
            Double feelsLike,

            @JsonProperty("dew_point")
            Double dewPoint,

            Double humidity,

            Double pressure,

            Double visibility,

            @JsonProperty("uv_index")
            Double uvIndex,

            Wind wind,

            Precipitation precipitation,

            @JsonProperty("cloud_cover")
            Object cloudCover
    ) {

        public Integer cloudCoverPercent() {
            Object value = cloudCover instanceof Map<?, ?> breakdown
                    ? breakdown.get("total")
                    : cloudCover;

            return value instanceof Number percent ? percent.intValue() : null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Wind(
            Double speed,
            Double gusts,
            Integer angle,
            String dir
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Precipitation(
            Double total,
            String type
    ) {
    }
}
