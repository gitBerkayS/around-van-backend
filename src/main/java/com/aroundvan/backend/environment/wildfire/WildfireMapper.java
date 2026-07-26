package com.aroundvan.backend.environment.wildfire;

import com.aroundvan.backend.environment.wildfire.dto.WildfireResponse;
import com.aroundvan.backend.location.Location;
import org.springframework.stereotype.Component;

@Component
public class WildfireMapper {

    public WildfireResponse toResponse(Wildfire wildfire, Double distanceKm) {
        Location location = wildfire.getLocation();

        return new WildfireResponse(
                wildfire.getId(),
                wildfire.getFireNumber(),
                wildfire.getIncidentName(),
                wildfire.getGeographicDescription(),
                location != null && location.getNeighbourhood() != null
                        ? location.getNeighbourhood().getName()
                        : null,
                location != null ? location.getLatitude() : null,
                location != null ? location.getLongitude() : null,
                distanceKm == null ? null : Math.round(distanceKm * 10.0) / 10.0,
                wildfire.getCurrentSizeHectares(),
                wildfire.getStatus(),
                wildfire.getCause(),
                wildfire.getResponseType(),
                wildfire.getFireType(),
                wildfire.getIgnitionDate(),
                wildfire.isFireOfNote(),
                wildfire.getFireUrl(),
                wildfire.getLastSyncedAt()
        );
    }
}
