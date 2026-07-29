package com.aroundvan.backend.servicerequest.vancouver;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vancouver.opendata")
public record Vancouver311Properties(
        String baseUrl,
        String dataset,
        Integer pageSize
) {
    public int pageSizeOrDefault() {
        return pageSize == null || pageSize <= 0 ? 100 : pageSize;
    }

    public String datasetOrDefault() {
        return dataset == null || dataset.isBlank()
                ? "3-1-1-service-requests"
                : dataset;
    }
}
