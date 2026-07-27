package com.aroundvan.backend.gas;

import com.aroundvan.backend.gas.dto.GasImportRequest;
import com.aroundvan.backend.location.Location;
import com.aroundvan.backend.location.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class GasImportService {

    private final GasStationRepository gasStationRepository;
    private final GasPriceRepository gasPriceRepository;
    private final LocationService locationService;

    @Transactional
    public int importStations(GasImportRequest request) {
        String postalPrefix = LocationService.normalizePostalCodePrefix(request.postalCodePrefix());
        Instant syncedAt = Instant.now();
        int imported = 0;

        for (GasImportRequest.Station stationData : request.stations()) {
            String externalKey = buildExternalKey(
                    postalPrefix,
                    stationData.name(),
                    stationData.address()
            );

            GasStation station = gasStationRepository
                    .findByExternalKey(externalKey)
                    .orElseGet(GasStation::new);

            station.setExternalKey(externalKey);
            station.setName(stationData.name().trim());
            station.setAddress(stationData.address().trim());
            station.setPostalCodePrefix(postalPrefix);
            station.setLastSyncedAt(syncedAt);

            if (stationData.latitude() != null && stationData.longitude() != null) {
                Location location = locationService.resolveGasStationLocation(
                        station.getLocation(),
                        stationData.latitude(),
                        stationData.longitude()
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
            imported++;
        }

        return imported;
    }

    private String buildExternalKey(String postalPrefix, String name, String address) {
        String raw = postalPrefix + "|" + name.trim().toLowerCase() + "|" + address.trim().toLowerCase();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }
}
