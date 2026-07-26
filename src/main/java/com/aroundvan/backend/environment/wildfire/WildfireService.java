package com.aroundvan.backend.environment.wildfire;

import com.aroundvan.backend.environment.wildfire.dto.WildfireResponse;
import com.aroundvan.backend.location.LocationService;
import com.aroundvan.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WildfireService {

    private final WildfireRepository wildfireRepository;
    private final WildfireMapper wildfireMapper;
    private final LocationService locationService;

    public List<WildfireResponse> getActiveWildfires() {
        return findActiveWildfires()
                .stream()
                .map(wildfire -> wildfireMapper.toResponse(wildfire, null))
                .toList();
    }

    public List<WildfireResponse> getActiveWildfiresNearbyForUser(User user, Integer limit) {
        if (user.getHomeLocation() == null) {
            throw new IllegalStateException("Set your home location before requesting nearby wildfires");
        }

        return findActiveWildfires()
                .stream()
                .filter(wildfire -> wildfire.getLocation() != null)
                .map(wildfire -> wildfireMapper.toResponse(
                        wildfire,
                        locationService.calculateDistanceFromUserInKm(user, wildfire.getLocation())
                ))
                .sorted(Comparator.comparingDouble(WildfireResponse::distanceKm))
                .limit(limit == null || limit <= 0 ? Long.MAX_VALUE : limit)
                .toList();
    }

    private List<Wildfire> findActiveWildfires() {
        return wildfireRepository.findAllByStatusNot(WildfireStatus.OUT);
    }
}
