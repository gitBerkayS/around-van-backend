package com.aroundvan.backend.environment.wildfire.fireweather;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(BcwsFireWeatherProperties.class)
public class BcwsFireWeatherConfig {

    @Bean("bcwsFireWeatherRestClient")
    public RestClient bcwsFireWeatherRestClient(
            RestClient.Builder builder,
            BcwsFireWeatherProperties properties
    ) {
        return builder
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
