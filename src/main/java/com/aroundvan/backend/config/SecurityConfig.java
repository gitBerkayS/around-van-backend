package com.aroundvan.backend.config;

import com.aroundvan.backend.usage.UsageTrackingFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UsageTrackingFilter usageTrackingFilter
    ) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/actuator/health",
                                "/error"
                        ).permitAll()
                        .requestMatchers(weatherCurrentWithCoordinates())
                        .permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/events/upcoming/near",
                                "/api/wildfires/active/near",
                                "/api/wildfires/fire-weather/near",
                                "/api/aqhi/current",
                                "/api/weather/current",
                                "/api/gas/near",
                                "/api/gas/cheapest",
                                "/api/service-requests/important/near",
                                "/api/service-requests/near"
                        ).authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/gas/import").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/service-requests/*/seen").authenticated()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/events/upcoming",
                                "/api/events/past",
                                "/api/wildfires/active"
                        ).permitAll()
                        .requestMatchers("/api/users/**").authenticated()
                        .anyRequest().denyAll()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(Customizer.withDefaults())
                )
                .addFilterAfter(
                        usageTrackingFilter,
                        BearerTokenAuthenticationFilter.class
                )
                .build();
    }

    private static RequestMatcher weatherCurrentWithCoordinates() {
        return (HttpServletRequest request) ->
                HttpMethod.GET.matches(request.getMethod())
                        && "/api/weather/current".equals(request.getRequestURI())
                        && request.getParameter("latitude") != null
                        && request.getParameter("longitude") != null;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
