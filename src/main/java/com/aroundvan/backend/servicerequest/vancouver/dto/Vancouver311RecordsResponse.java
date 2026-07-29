package com.aroundvan.backend.servicerequest.vancouver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Vancouver311RecordsResponse(
        @JsonProperty("total_count") Long totalCount,
        List<Vancouver311Record> results
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Vancouver311Record(
            String department,
            @JsonProperty("service_request_type") String serviceRequestType,
            String status,
            @JsonProperty("closure_reason") String closureReason,
            @JsonProperty("service_request_open_timestamp") Instant serviceRequestOpenTimestamp,
            @JsonProperty("service_request_close_date") String serviceRequestCloseDate,
            @JsonProperty("last_modified_timestamp") Instant lastModifiedTimestamp,
            String address,
            @JsonProperty("local_area") String localArea,
            String channel,
            Double latitude,
            Double longitude
    ) {
    }
}
