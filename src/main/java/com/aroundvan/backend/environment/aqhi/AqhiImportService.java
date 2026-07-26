package com.aroundvan.backend.environment.aqhi;

import com.aroundvan.backend.environment.aqhi.ec.EnvironmentCanadaAqhiClient;
import com.aroundvan.backend.environment.aqhi.ec.dto.AqhiObservationsResponse;
import com.aroundvan.backend.location.neighbourhood.Neighbourhood;
import com.aroundvan.backend.location.neighbourhood.NeighbourhoodRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AqhiImportService {

    private final EnvironmentCanadaAqhiClient environmentCanadaAqhiClient;
    private final AqhiRegionRepository aqhiRegionRepository;
    private final AqhiReadingRepository aqhiReadingRepository;
    private final NeighbourhoodRepository neighbourhoodRepository;
    private final AqhiRegionLocator aqhiRegionLocator;

    @Transactional
    public int importLatestReadings() {
        List<AqhiObservationsResponse.Feature> observations =
                environmentCanadaAqhiClient.fetchLatestObservations();

        Instant fetchedAt = Instant.now();
        int storedReadings = 0;

        for (AqhiObservationsResponse.Feature observation : observations) {
            AqhiObservationsResponse.Properties properties = observation.properties();

            if (properties == null
                    || properties.location_id() == null
                    || properties.aqhi() == null
                    || properties.observation_datetime() == null) {
                continue;
            }

            AqhiRegion region = upsertRegion(properties, observation.geometry());

            boolean alreadyStored = aqhiReadingRepository
                    .findByRegionLocationIdAndObservedAt(
                            region.getLocationId(),
                            properties.observation_datetime()
                    )
                    .isPresent();

            if (alreadyStored) {
                continue;
            }

            AqhiReading reading = new AqhiReading();
            reading.setRegion(region);
            reading.setValue(properties.aqhi());
            reading.setObservedAt(properties.observation_datetime());
            reading.setFetchedAt(fetchedAt);

            aqhiReadingRepository.save(reading);

            storedReadings++;
        }

        linkUnassignedNeighbourhoods();

        return storedReadings;
    }

    private AqhiRegion upsertRegion(AqhiObservationsResponse.Properties properties, AqhiObservationsResponse.Geometry geometry) {
        AqhiRegion region = aqhiRegionRepository
                .findById(properties.location_id())
                .orElseGet(AqhiRegion::new);

        region.setLocationId(properties.location_id());
        region.setName(properties.location_name_en() != null
                ? properties.location_name_en()
                : properties.location_id());

        if (geometry != null) {
            region.setLatitude(geometry.latitude());
            region.setLongitude(geometry.longitude());
        }

        return aqhiRegionRepository.save(region);
    }

    //each neighbourhood inherits the region reporting closest to its boundary centroid.
    private void linkUnassignedNeighbourhoods() {
        List<Neighbourhood> unassigned = neighbourhoodRepository.findAllByAqhiRegionIsNull();

        if (unassigned.isEmpty()) {
            return;
        }

        List<AqhiRegion> regions = aqhiRegionRepository.findAll();

        for (Neighbourhood neighbourhood : unassigned) {
            if (neighbourhood.getBoundary() == null) {
                continue;
            }

            Point centroid = neighbourhood.getBoundary().getCentroid();

            aqhiRegionLocator.findNearestRegion(regions, centroid.getY(), centroid.getX()).ifPresent(neighbourhood::setAqhiRegion);
        }

        neighbourhoodRepository.saveAll(unassigned);
    }
}
