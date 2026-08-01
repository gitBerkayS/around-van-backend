package com.aroundvan.backend.mail;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(ResendProperties.class)
public class ResendConfig {

    @Bean("resendRestClient")
    public RestClient resendRestClient(
            RestClient.Builder builder,
            ResendProperties properties
    ) {
        RestClient.Builder clientBuilder = builder
                .baseUrl(properties.baseUrlOrDefault())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "around-van-backend/1.0");

        if (properties.apiKey() != null && !properties.apiKey().isBlank()) {
            clientBuilder.defaultHeader(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + properties.apiKey()
            );
        }

        return clientBuilder.build();
    }
}
