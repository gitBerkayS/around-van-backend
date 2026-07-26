package com.aroundvan.backend.environment.aqhi.ec;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "environment-canada.aqhi")
public record EnvironmentCanadaAqhiProperties(String baseUrl, String boundingBox, Integer limit) {
    public int limitOrDefault() {
        return limit == null || limit <= 0 ? 100 : limit;
    }
}
