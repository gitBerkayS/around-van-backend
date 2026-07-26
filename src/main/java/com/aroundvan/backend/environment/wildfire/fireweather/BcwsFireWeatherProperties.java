package com.aroundvan.backend.environment.wildfire.fireweather;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bcws.fire-weather")
public record BcwsFireWeatherProperties(
        String baseUrl,
        Integer defaultRadiusKm,
        Integer maxRadiusKm,
        Integer lookbackDays,
        Integer pageRowCount,
        Integer cacheMinutes
) {
    public int defaultRadiusKmOrDefault() {
        return defaultRadiusKm == null || defaultRadiusKm <= 0 ? 100 : defaultRadiusKm;
    }

    public int maxRadiusKmOrDefault() {
        return maxRadiusKm == null || maxRadiusKm <= 0 ? 300 : maxRadiusKm;
    }

    public int lookbackDaysOrDefault() {
        return lookbackDays == null || lookbackDays <= 0 ? 3 : lookbackDays;
    }

    public int pageRowCountOrDefault() {
        return pageRowCount == null || pageRowCount <= 0 ? 200 : pageRowCount;
    }

    public int cacheMinutesOrDefault() {
        return cacheMinutes == null || cacheMinutes <= 0 ? 15 : cacheMinutes;
    }
}
