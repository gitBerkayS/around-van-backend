package com.aroundvan.backend.gas.geocoding.nominatim;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nominatim")
public record NominatimProperties(
        String baseUrl,
        String email,
        String userAgent,
        String countryCodes
) {
    public String baseUrlOrDefault() {
        return baseUrl == null || baseUrl.isBlank()
                ? "https://nominatim.openstreetmap.org"
                : baseUrl;
    }

    public String userAgentOrDefault() {
        return userAgent == null || userAgent.isBlank()
                ? "around-van-backend/1.0"
                : userAgent;
    }

    public String countryCodesOrDefault() {
        return countryCodes == null || countryCodes.isBlank()
                ? "ca"
                : countryCodes;
    }

    public boolean hasEmail() {
        return email != null && !email.isBlank();
    }
}
