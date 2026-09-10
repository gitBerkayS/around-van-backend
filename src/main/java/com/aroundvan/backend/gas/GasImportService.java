package com.aroundvan.backend.gas;

import com.aroundvan.backend.gas.dto.GasImportRequest;
import com.aroundvan.backend.gas.dto.GasImportResult;
import com.aroundvan.backend.gas.geocoding.nominatim.NominatimClient;
import com.aroundvan.backend.location.Location;
import com.aroundvan.backend.location.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HexFormat;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class GasImportService {

    private static final Pattern GLUED_CITY = Pattern.compile(
            "(?i)([^\\s,])((?:North |West )?Vancouver|Burnaby|Richmond|Coquitlam|Port Moody|Port Coquitlam),\\s*BC"
    );

    private static final ZoneId VANCOUVER_ZONE = ZoneId.of("America/Vancouver");

    private final GasStationRepository gasStationRepository;
    private final GasPriceRepository gasPriceRepository;
    private final GasPriceDailyRepository gasPriceDailyRepository;
    private final LocationService locationService;
    private final NominatimClient nominatimClient;

    @Transactional
    public GasImportResult importStations(GasImportRequest request) {
        String postalPrefix = LocationService.normalizePostalCodePrefix(request.postalCodePrefix());
        Instant syncedAt = Instant.now();
        int imported = 0;
        int skipped = 0;

        for (GasImportRequest.Station stationData : request.stations()) {
            String name = stationData.name().trim();
            String address = normalizeAddress(stationData.address());

            if (!isVancouverProper(address)) {
                skipped++;
                continue;
            }

            String externalKey = buildExternalKey(name, address);

            GasStation station = gasStationRepository
                    .findByExternalKey(externalKey)
                    .orElseGet(GasStation::new);

            station.setExternalKey(externalKey);
            station.setName(name);
            station.setAddress(address);
            station.setPostalCodePrefix(postalPrefix);
            station.setLastSyncedAt(syncedAt);

            Double latitude = stationData.latitude();
            Double longitude = stationData.longitude();

            if (latitude == null || longitude == null) {
                var geocoded = nominatimClient.geocode(address);
                if (geocoded.isPresent()) {
                    latitude = geocoded.get().latitude();
                    longitude = geocoded.get().longitude();
                }
            }

            if (latitude != null && longitude != null) {
                Location location = locationService.resolveGasStationLocation(
                        station.getLocation(),
                        latitude,
                        longitude
                );
                station.setLocation(location);
            }

            gasStationRepository.save(station);

            GasPrice price = gasPriceRepository
                    .findByStationIdAndFuelType(station.getId(), request.fuelType())
                    .orElseGet(() -> {
                        GasPrice created = new GasPrice();
                        created.setStation(station);
                        created.setFuelType(request.fuelType());
                        return created;
                    });

            price.setPrice(stationData.price());
            price.setObservedAt(syncedAt);

            gasPriceRepository.save(price);
            upsertDailyPrice(station, request.fuelType(), stationData.price(), syncedAt);
            imported++;
        }

        return new GasImportResult(imported, skipped);
    }

    private void upsertDailyPrice(
            GasStation station,
            FuelType fuelType,
            BigDecimal priceValue,
            Instant observedAt
    ) {
        LocalDate priceDate = observedAt.atZone(VANCOUVER_ZONE).toLocalDate();

        GasPriceDaily daily = gasPriceDailyRepository
                .findByStationIdAndFuelTypeAndPriceDate(station.getId(), fuelType, priceDate)
                .orElseGet(() -> {
                    GasPriceDaily created = new GasPriceDaily();
                    created.setStation(station);
                    created.setFuelType(fuelType);
                    created.setPriceDate(priceDate);
                    return created;
                });

        daily.setPrice(priceValue);
        daily.setObservedAt(observedAt);
        gasPriceDailyRepository.save(daily);
    }

    static String normalizeAddress(String address) {
        if (address == null || address.isBlank()) {
            return address;
        }

        String cleaned = address.trim().replaceAll("\\s+", " ");
        return GLUED_CITY.matcher(cleaned).replaceAll("$1, $2, BC");
    }

    static boolean isVancouverProper(String address) {
        if (address == null || address.isBlank()) {
            return false;
        }

        String normalized = address.toLowerCase(Locale.ROOT);

        if (normalized.contains("north vancouver") || normalized.contains("west vancouver")) {
            return false;
        }

        return normalized.contains("vancouver, bc");
    }

    private String buildExternalKey(String name, String address) {
        String raw = name.trim().toLowerCase(Locale.ROOT) + "|" + address.trim().toLowerCase(Locale.ROOT);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }
}
