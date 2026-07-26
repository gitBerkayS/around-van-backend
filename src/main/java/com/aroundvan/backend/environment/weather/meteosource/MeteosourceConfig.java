package com.aroundvan.backend.environment.weather.meteosource;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(MeteosourceProperties.class)
public class MeteosourceConfig {

    @Bean("meteosourceRestClient")
    public RestClient meteosourceRestClient(RestClient.Builder builder, MeteosourceProperties properties) {
        return builder
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
