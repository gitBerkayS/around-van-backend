package com.aroundvan.backend.servicerequest.vancouver;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(Vancouver311Properties.class)
public class Vancouver311Config {

    @Bean("vancouver311RestClient")
    public RestClient vancouver311RestClient(
            RestClient.Builder builder,
            Vancouver311Properties properties
    ) {
        return builder
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
