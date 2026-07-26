package com.aroundvan.backend.api.weather;

import com.aroundvan.backend.environment.weather.WeatherService;
import com.aroundvan.backend.environment.weather.dto.WeatherResponse;
import com.aroundvan.backend.user.User;
import com.aroundvan.backend.user.UserService;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Validated
public class WeatherController {

    private final WeatherService weatherService;
    private final UserService userService;

    @GetMapping("/current")
    public WeatherResponse getCurrentWeather(
            @RequestParam(required = false)
            @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,

            @RequestParam(required = false)
            @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude
    ) {
        if (latitude != null && longitude != null) {
            return weatherService.getCurrentWeather(latitude, longitude);
        }

        if (latitude != null || longitude != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide both latitude and longitude, or neither"
            );
        }

        User user = userService.getCurrentUser();

        if (user.getHomeLocation() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Set your home location before requesting weather"
            );
        }

        return weatherService.getCurrentWeatherForUser(user);
    }
}
