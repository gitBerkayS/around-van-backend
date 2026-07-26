package com.aroundvan.backend.environment.weather;

import com.aroundvan.backend.common.TtlCache;
import com.aroundvan.backend.environment.weather.dto.WeatherResponse;
import com.aroundvan.backend.environment.weather.meteosource.MeteosourceClient;
import com.aroundvan.backend.environment.weather.meteosource.MeteosourceProperties;
import com.aroundvan.backend.environment.weather.meteosource.dto.MeteosourcePointResponse;
import com.aroundvan.backend.location.Location;
import com.aroundvan.backend.location.neighbourhood.Neighbourhood;
import com.aroundvan.backend.location.neighbourhood.NeighbourhoodService;
import com.aroundvan.backend.user.User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class WeatherService {

    private static final double COORDINATE_PRECISION = 100.0;

    private final MeteosourceClient meteosourceClient;
    private final MeteosourceProperties meteosourceProperties;
    private final NeighbourhoodService neighbourhoodService;

    private final TtlCache<String, WeatherResponse> forecastCache;

    public WeatherService(
            MeteosourceClient meteosourceClient,
            MeteosourceProperties meteosourceProperties,
            NeighbourhoodService neighbourhoodService
    ) {
        this.meteosourceClient = meteosourceClient;
        this.meteosourceProperties = meteosourceProperties;
        this.neighbourhoodService = neighbourhoodService;
        this.forecastCache = new TtlCache<>(
                Duration.ofMinutes(meteosourceProperties.cacheMinutesOrDefault())
        );
    }

    public WeatherResponse getCurrentWeatherForUser(User user) {
        Location homeLocation = user.getHomeLocation();

        if (homeLocation == null) {
            throw new IllegalStateException("Set your home location before requesting weather");
        }

        return getCurrentWeather(homeLocation.getLatitude(), homeLocation.getLongitude());
    }

    public WeatherResponse getCurrentWeather(double latitude, double longitude) {
        double roundedLatitude = round(latitude);
        double roundedLongitude = round(longitude);

        String cacheKey = roundedLatitude + "," + roundedLongitude;

        Optional<WeatherResponse> cached = forecastCache.get(cacheKey);

        if (cached.isPresent()) {
            return cached.get();
        }

        MeteosourcePointResponse response =
                meteosourceClient.fetchCurrentWeather(roundedLatitude, roundedLongitude);

        if (response == null || response.current() == null) {
            throw new IllegalStateException("Weather is unavailable for this location");
        }

        WeatherResponse weather = toResponse(response, roundedLatitude, roundedLongitude);

        forecastCache.put(cacheKey, weather);

        return weather;
    }

    private WeatherResponse toResponse(
            MeteosourcePointResponse response,
            double latitude,
            double longitude
    ) {
        MeteosourcePointResponse.Current current = response.current();
        MeteosourcePointResponse.Wind wind = current.wind();
        MeteosourcePointResponse.Precipitation precipitation = current.precipitation();

        String neighbourhoodName = neighbourhoodService
                .findByCoordinates(latitude, longitude)
                .map(Neighbourhood::getName)
                .orElse(null);

        return new WeatherResponse(
                latitude,
                longitude,
                neighbourhoodName,
                current.summary(),
                current.icon(),
                current.iconNumber(),
                current.temperature(),
                current.feelsLike(),
                current.humidity(),
                current.pressure(),
                current.uvIndex(),
                wind != null ? wind.speed() : null,
                wind != null ? wind.angle() : null,
                wind != null ? wind.dir() : null,
                precipitation != null ? precipitation.total() : null,
                precipitation != null ? precipitation.type() : null,
                current.cloudCoverPercent(),
                response.units() != null ? response.units() : meteosourceProperties.unitsOrDefault(),
                Instant.now()
        );
    }

    private double round(double coordinate) {
        return Math.round(coordinate * COORDINATE_PRECISION) / COORDINATE_PRECISION;
    }
}
