package com.aroundvan.backend.environment.aqhi;

import com.aroundvan.backend.environment.aqhi.dto.AqhiResponse;
import com.aroundvan.backend.location.Location;
import com.aroundvan.backend.location.neighbourhood.Neighbourhood;
import com.aroundvan.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AqhiService {

    private final AqhiReadingRepository aqhiReadingRepository;
    private final AqhiRegionRepository aqhiRegionRepository;
    private final AqhiRegionLocator aqhiRegionLocator;

    @Transactional(readOnly = true)
    public AqhiResponse getCurrentAqhiForUser(User user) {
        Location homeLocation = user.getHomeLocation();

        if (homeLocation == null) {
            throw new IllegalStateException("Set your home location before requesting air quality");
        }

        Neighbourhood neighbourhood = homeLocation.getNeighbourhood();

        AqhiRegion region = resolveRegion(homeLocation, neighbourhood);

        AqhiReading reading = aqhiReadingRepository
                .findFirstByRegionLocationIdOrderByObservedAtDesc(region.getLocationId())
                .orElseThrow(() -> new IllegalStateException(
                        "No air quality reading available yet for " + region.getName()
                ));

        return toResponse(reading, neighbourhood);
    }

    public AqhiRiskLevel getUserAqhiRiskLevel(User user) {
        return getCurrentAqhiForUser(user).riskLevel();
    }

    private AqhiRegion resolveRegion(Location homeLocation, Neighbourhood neighbourhood) {
        if (neighbourhood != null && neighbourhood.getAqhiRegion() != null) {
            return neighbourhood.getAqhiRegion();
        }

        return aqhiRegionLocator
                .findNearestRegion(
                        aqhiRegionRepository.findAll(),
                        homeLocation.getLatitude(),
                        homeLocation.getLongitude()
                )
                .orElseThrow(() -> new IllegalStateException(
                        "No air quality region available for your location"
                ));
    }

    private AqhiResponse toResponse(AqhiReading reading, Neighbourhood neighbourhood) {
        AqhiRiskLevel riskLevel = AqhiRiskLevel.fromValue(reading.getValue());

        return new AqhiResponse(
                reading.getRegion().getLocationId(),
                reading.getRegion().getName(),
                neighbourhood != null ? neighbourhood.getName() : null,
                reading.getValue(),
                riskLevel,
                riskLevel.getLabel(),
                riskLevel.getHealthMessage(),
                reading.getObservedAt()
        );
    }
}
