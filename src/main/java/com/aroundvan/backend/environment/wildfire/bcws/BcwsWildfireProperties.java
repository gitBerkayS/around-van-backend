package com.aroundvan.backend.environment.wildfire.bcws;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bcws.wildfire")
public record BcwsWildfireProperties(
        String baseUrl,
        Integer pageSize
) {
    public int pageSizeOrDefault() {
        return pageSize == null || pageSize <= 0 ? 1000 : pageSize;
    }
}
