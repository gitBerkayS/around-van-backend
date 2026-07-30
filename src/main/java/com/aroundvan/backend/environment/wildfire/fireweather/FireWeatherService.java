package com.aroundvan.backend.environment.wildfire.fireweather;

import com.aroundvan.backend.common.TtlCache;
import com.aroundvan.backend.environment.wildfire.fireweather.dto.BcwsDailyListResponse;
import com.aroundvan.backend.environment.wildfire.fireweather.dto.FireWeatherResponse;
import com.aroundvan.backend.location.Location;
import com.aroundvan.backend.location.LocationCoordinates;
import com.aroundvan.backend.location.LocationService;
import com.aroundvan.backend.user.User;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FireWeatherService {

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMddHH");

    private final BcwsFireWeatherClient bcwsFireWeatherClient;
    private final BcwsFireWeatherProperties properties;
    private final LocationService locationService;

    private final TtlCache<String, List<FireWeatherResponse>> readingCache;

    public FireWeatherService(
            BcwsFireWeatherClient bcwsFireWeatherClient,
            BcwsFireWeatherProperties properties,
            LocationService locationService
    ) {
        this.bcwsFireWeatherClient = bcwsFireWeatherClient;
        this.properties = properties;
        this.locationService = locationService;
        this.readingCache = new TtlCache<>(
                Duration.ofMinutes(properties.cacheMinutesOrDefault())
        );
    }

    public List<FireWeatherResponse> getFireWeatherNearbyForUser(
            User user,
            Integer radiusKm,
            Integer limit
    ) {
        Location homeLocation = user.getHomeLocation();

        if (homeLocation == null) {
            throw new IllegalStateException("Set your home location before requesting fire weather");
        }

        int radius = clampRadius(radiusKm);

        List<FireWeatherResponse> readings = getFireWeather(
                homeLocation.getLatitude(),
                homeLocation.getLongitude(),
                radius
        );

        if (limit == null || limit <= 0) {
            return readings;
        }

        return readings.stream().limit(limit).toList();
    }

    private List<FireWeatherResponse> getFireWeather(double latitude, double longitude, int radiusKm) {
        String cacheKey = latitude + "," + longitude + "," + radiusKm;

        Optional<List<FireWeatherResponse>> cached = readingCache.get(cacheKey);

        if (cached.isPresent()) {
            return cached.get();
        }

        List<BcwsDailyListResponse.Daily> dailies =
                bcwsFireWeatherClient.fetchRecentDailies(latitude, longitude, radiusKm);

        List<FireWeatherResponse> readings = latestPerStation(dailies)
                .stream()
                .map(daily -> toResponse(daily, latitude, longitude))
                .sorted(Comparator.comparingDouble(reading ->
                        reading.distanceKm() == null ? Double.MAX_VALUE : reading.distanceKm()
                ))
                .toList();

        if (!readings.isEmpty()) {
            readingCache.put(cacheKey, readings);
        }

        return readings;
    }

    private List<BcwsDailyListResponse.Daily> latestPerStation(
            List<BcwsDailyListResponse.Daily> dailies
    ) {
        Map<String, BcwsDailyListResponse.Daily> newestByStation = new LinkedHashMap<>();

        for (BcwsDailyListResponse.Daily daily : dailies) {
            if (daily.stationCode() == null || daily.weatherTimestamp() == null) {
                continue;
            }

            newestByStation.merge(
                    daily.stationCode(),
                    daily,
                    (existing, candidate) ->
                            candidate.weatherTimestamp().compareTo(existing.weatherTimestamp()) > 0
                                    ? candidate
                                    : existing
            );
        }

        return List.copyOf(newestByStation.values());
    }

    private FireWeatherResponse toResponse(
            BcwsDailyListResponse.Daily daily,
            double latitude,
            double longitude
    ) {
        BcwsDailyListResponse.Geometry geometry = daily.geometry();

        Double stationLatitude = geometry != null ? geometry.latitude() : null;
        Double stationLongitude = geometry != null ? geometry.longitude() : null;

        Double distanceKm = null;

        if (stationLatitude != null && stationLongitude != null) {
            double distance = locationService.calculateDistanceBetweenPointsInKm(
                    new LocationCoordinates(longitude, latitude),
                    new LocationCoordinates(stationLongitude, stationLatitude)
            );

            distanceKm = Math.round(distance * 10.0) / 10.0;
        }

        FireDangerClass dangerClass = FireDangerClass.fromRating(daily.dangerForest());

        return new FireWeatherResponse(
                daily.stationCode(),
                daily.stationName(),
                daily.fireCentre(),
                stationLatitude,
                stationLongitude,
                distanceKm,
                daily.dangerForest(),
                dangerClass,
                dangerClass != null ? dangerClass.getLabel() : null,
                daily.temperature(),
                daily.relativeHumidity(),
                daily.windSpeed(),
                daily.windDirection(),
                daily.precipitation(),
                daily.fineFuelMoistureCode(),
                daily.duffMoistureCode(),
                daily.droughtCode(),
                daily.initialSpreadIndex(),
                daily.buildUpIndex(),
                daily.fireWeatherIndex(),
                toInstant(daily.weatherTimestamp())
        );
    }

    private Instant toInstant(String weatherTimestamp) {
        try {
            return LocalDateTime.parse(weatherTimestamp, TIMESTAMP)
                    .atZone(BcwsFireWeatherClient.BCWS_ZONE)
                    .toInstant();
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private int clampRadius(Integer radiusKm) {
        if (radiusKm == null || radiusKm <= 0) {
            return properties.defaultRadiusKmOrDefault();
        }

        return Math.min(radiusKm, properties.maxRadiusKmOrDefault());
    }
}
