package com.aroundvan.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "app.cors")
public record AppCorsProperties(
        String allowedOrigins
) {
    public List<String> origins() {
        if (allowedOrigins == null || allowedOrigins.isBlank()) {
            return List.of();
        }

        return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .map(AppCorsProperties::trimTrailingSlash)
                .toList();
    }

    public String frontendBaseUrl() {
        List<String> origins = origins();

        return origins.stream()
                .filter(origin -> origin.startsWith("https://"))
                .findFirst()
                .or(() -> origins.stream().findFirst())
                .orElse("http://localhost:5173");
    }

    private static String trimTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
