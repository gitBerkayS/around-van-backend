package com.aroundvan.backend.environment.weather.meteosource;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "meteosource")
public record MeteosourceProperties(
        String baseUrl,
        String apiKey,
        String units,
        String language,
        Integer cacheMinutes
) {
    public String unitsOrDefault() {
        return units == null || units.isBlank() ? "metric" : units;
    }

    public String languageOrDefault() {
        return language == null || language.isBlank() ? "en" : language;
    }

    public int cacheMinutesOrDefault() {
        return cacheMinutes == null || cacheMinutes <= 0 ? 15 : cacheMinutes;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }
}
