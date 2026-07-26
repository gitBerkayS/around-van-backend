package com.aroundvan.backend.environment.wildfire.bcws.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BcwsFireQueryResponse(

        List<Feature> features,

        Boolean exceededTransferLimit
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Feature(
            Attributes attributes
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Attributes(

            @JsonProperty("FIRE_NUMBER")
            String fireNumber,

            @JsonProperty("FIRE_ID")
            Integer fireId,

            @JsonProperty("INCIDENT_NAME")
            String incidentName,

            @JsonProperty("GEOGRAPHIC_DESCRIPTION")
            String geographicDescription,

            @JsonProperty("LATITUDE")
            Double latitude,

            @JsonProperty("LONGITUDE")
            Double longitude,

            @JsonProperty("CURRENT_SIZE")
            Double currentSize,

            @JsonProperty("FIRE_STATUS")
            String fireStatus,

            @JsonProperty("FIRE_CAUSE")
            String fireCause,

            @JsonProperty("RESPONSE_TYPE_DESC")
            String responseTypeDescription,

            @JsonProperty("FIRE_CENTRE")
            Integer fireCentre,

            @JsonProperty("ZONE")
            Integer zone,

            @JsonProperty("FIRE_TYPE")
            String fireType,

            // Epoch milliseconds
            @JsonProperty("IGNITION_DATE")
            Long ignitionDate,

            // Epoch milliseconds
            @JsonProperty("FIRE_OUT_DATE")
            Long fireOutDate,

            @JsonProperty("FIRE_URL")
            String fireUrl,

            @JsonProperty("FIRE_OF_NOTE_IND")
            String fireOfNoteIndicator,

            @JsonProperty("WAS_FIRE_OF_NOTE_IND")
            String wasFireOfNoteIndicator
    ) {
    }
}
