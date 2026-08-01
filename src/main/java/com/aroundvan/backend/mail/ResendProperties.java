package com.aroundvan.backend.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "resend")
public record ResendProperties(
        String baseUrl,
        String apiKey,
        String from
) {
    public String baseUrlOrDefault() {
        return baseUrl == null || baseUrl.isBlank()
                ? "https://api.resend.com"
                : baseUrl;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank()
                && from != null && !from.isBlank();
    }
}
