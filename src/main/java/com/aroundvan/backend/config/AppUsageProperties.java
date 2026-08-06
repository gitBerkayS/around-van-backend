package com.aroundvan.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.usage")
public record AppUsageProperties(
        Duration activityLogInterval
) {
    public AppUsageProperties {
        if (activityLogInterval == null) {
            activityLogInterval = Duration.ofMinutes(5);
        }
    }
}
