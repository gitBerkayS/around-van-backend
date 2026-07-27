package com.aroundvan.backend.gas;

import com.aroundvan.backend.gas.dto.GasStationResponse;
import com.aroundvan.backend.location.Location;
import com.aroundvan.backend.location.LocationService;
import com.aroundvan.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GasService {

    private static final double DEFAULT_SEARCH_RADIUS_KM = 25.0;

    private final GasPriceRepository gasPriceRepository;
    private final LocationService locationService;

    @Transactional(readOnly = true)
    public List<GasStationResponse> getNearestStations(User user, FuelType fuelType, Integer limit) {
        requireHomeLocation(user);

        FuelType resolvedFuel = resolveFuelType(user, fuelType);
        int resolvedLimit = resolveLimit(limit);

        return pricedStationsNearUser(user, resolvedFuel, DEFAULT_SEARCH_RADIUS_KM)
                .stream()
                .sorted(Comparator.comparingDouble(GasStationResponse::distanceKm))
                .limit(resolvedLimit)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GasStationResponse> getCheapestStations(User user, FuelType fuelType, Integer limit) {
        requireHomeLocation(user);

        FuelType resolvedFuel = resolveFuelType(user, fuelType);
        int resolvedLimit = resolveLimit(limit);

        return pricedStationsNearUser(user, resolvedFuel, DEFAULT_SEARCH_RADIUS_KM)
                .stream()
                .sorted(Comparator
                        .comparing(GasStationResponse::price)
                        .thenComparingDouble(GasStationResponse::distanceKm))
                .limit(resolvedLimit)
                .toList();
    }

    private List<GasStationResponse> pricedStationsNearUser(
            User user,
            FuelType fuelType,
            double radiusKm
    ) {
        return gasPriceRepository.findAllByFuelTypeWithStationLocation(fuelType)
                .stream()
                .map(price -> toResponse(price, user))
                .filter(response -> response.distanceKm() != null && response.distanceKm() <= radiusKm)
                .toList();
    }

    private GasStationResponse toResponse(GasPrice price, User user) {
        GasStation station = price.getStation();
        Location location = station.getLocation();

        Double distanceKm = null;
        Double latitude = null;
        Double longitude = null;

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            double distance = locationService.calculateDistanceFromUserInKm(user, location);
            distanceKm = Math.round(distance * 10.0) / 10.0;
        }

        return new GasStationResponse(
                station.getId(),
                station.getName(),
                station.getAddress(),
                station.getPostalCodePrefix(),
                latitude,
                longitude,
                distanceKm,
                price.getFuelType(),
                price.getPrice(),
                price.getObservedAt()
        );
    }

    private FuelType resolveFuelType(User user, FuelType override) {
        if (override != null) {
            return override;
        }

        return user.getPreferredFuelType() != null
                ? user.getPreferredFuelType()
                : FuelType.REGULAR;
    }

    private int resolveLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return 5;
        }

        return Math.min(limit, 25);
    }

    private void requireHomeLocation(User user) {
        if (user.getHomeLocation() == null) {
            throw new IllegalStateException("Set your home location before requesting gas stations");
        }
    }
}
