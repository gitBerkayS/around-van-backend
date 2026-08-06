package com.aroundvan.backend.config;

import com.aroundvan.backend.usage.UsageTrackingFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AppUsageProperties.class)
public class UsageTrackingConfig {


    @Bean
    FilterRegistrationBean<UsageTrackingFilter> usageTrackingFilterRegistration(
            UsageTrackingFilter filter
    ) {
        FilterRegistrationBean<UsageTrackingFilter> registration =
                new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
