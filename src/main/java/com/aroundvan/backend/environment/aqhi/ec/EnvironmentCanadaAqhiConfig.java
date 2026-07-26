package com.aroundvan.backend.environment.aqhi.ec;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(EnvironmentCanadaAqhiProperties.class)
public class EnvironmentCanadaAqhiConfig {

    @Bean("environmentCanadaAqhiRestClient")
    public RestClient environmentCanadaAqhiRestClient(
            RestClient.Builder builder,
            EnvironmentCanadaAqhiProperties properties
    ) {
        return builder
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
