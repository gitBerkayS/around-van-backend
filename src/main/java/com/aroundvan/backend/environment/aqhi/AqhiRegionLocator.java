package com.aroundvan.backend.environment.aqhi;

import com.aroundvan.backend.location.LocationCoordinates;
import com.aroundvan.backend.location.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AqhiRegionLocator {

    private final LocationService locationService;

    public Optional<AqhiRegion> findNearestRegion(List<AqhiRegion> regions, double latitude, double longitude) {
        LocationCoordinates target = new LocationCoordinates(longitude, latitude);

        return regions.stream()
                .filter(region -> region.getLatitude() != null && region.getLongitude() != null)
                .min(Comparator.comparingDouble(region ->
                        locationService.calculateDistanceBetweenPointsInKm(target, new LocationCoordinates(region.getLongitude(), region.getLatitude()))
                ));
    }
}


