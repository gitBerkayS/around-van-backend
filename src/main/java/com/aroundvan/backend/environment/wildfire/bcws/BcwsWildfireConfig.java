package com.aroundvan.backend.environment.wildfire.bcws;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(BcwsWildfireProperties.class)
public class BcwsWildfireConfig {

    @Bean("bcwsWildfireRestClient")
    public RestClient bcwsWildfireRestClient(
            RestClient.Builder builder,
            BcwsWildfireProperties properties
    ) {
        return builder
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
