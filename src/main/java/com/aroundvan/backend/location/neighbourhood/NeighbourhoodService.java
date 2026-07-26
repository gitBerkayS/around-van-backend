package com.aroundvan.backend.location.neighbourhood;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NeighbourhoodService {

    private final NeighbourhoodRepository neighbourhoodRepository;

    // Finds the neighbourhood whose boundary polygon contains the point.
    // Points outside every seeded boundary (e.g. venues in other
    // municipalities) return empty rather than a made-up neighbourhood.
    public Optional<Neighbourhood> findByCoordinates(
            double latitude,
            double longitude
    ) {
        return neighbourhoodRepository.findByCoordinates(latitude, longitude);
    }
}
