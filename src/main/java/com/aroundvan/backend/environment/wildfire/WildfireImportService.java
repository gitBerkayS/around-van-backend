package com.aroundvan.backend.environment.wildfire;

import com.aroundvan.backend.environment.wildfire.bcws.BcwsWildfireClient;
import com.aroundvan.backend.environment.wildfire.bcws.BcwsWildfireMapper;
import com.aroundvan.backend.environment.wildfire.bcws.dto.BcwsFireQueryResponse;
import com.aroundvan.backend.location.Location;
import com.aroundvan.backend.location.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WildfireImportService {

    private final BcwsWildfireClient bcwsWildfireClient;
    private final BcwsWildfireMapper bcwsWildfireMapper;
    private final WildfireRepository wildfireRepository;
    private final LocationService locationService;

    @Transactional
    public int importActiveWildfires() {
        List<BcwsFireQueryResponse.Attributes> activeFires =
                bcwsWildfireClient.fetchActiveFires();

        Instant syncedAt = Instant.now();
        Set<String> importedFireNumbers = new HashSet<>();
        List<Wildfire> wildfiresToSave = new ArrayList<>();

        for (BcwsFireQueryResponse.Attributes attributes : activeFires) {
            String fireNumber = attributes.fireNumber();

            if (fireNumber == null || fireNumber.isBlank()) {
                continue;
            }

            if (!importedFireNumbers.add(fireNumber)) {
                continue;
            }

            Wildfire wildfire = wildfireRepository
                    .findByFireNumber(fireNumber)
                    .orElseGet(Wildfire::new);

            bcwsWildfireMapper.applyToWildfire(wildfire, attributes, syncedAt);

            Location location = locationService.resolveWildfireLocation(
                    wildfire.getLocation(),
                    attributes.latitude(),
                    attributes.longitude()
            );

            wildfire.setLocation(location);

            wildfiresToSave.add(wildfire);
        }

        wildfireRepository.saveAll(wildfiresToSave);

        extinguishFiresMissingFromFeed(importedFireNumbers, syncedAt);

        return wildfiresToSave.size();
    }

    private void extinguishFiresMissingFromFeed(Set<String> importedFireNumbers, Instant syncedAt) {
        List<Wildfire> extinguished = wildfireRepository
                .findAllByStatusNot(WildfireStatus.OUT)
                .stream()
                .filter(wildfire -> !importedFireNumbers.contains(wildfire.getFireNumber()))
                .peek(wildfire -> {
                    wildfire.setStatus(WildfireStatus.OUT);
                    wildfire.setLastSyncedAt(syncedAt);

                    if (wildfire.getFireOutDate() == null) {
                        wildfire.setFireOutDate(syncedAt);
                    }
                })
                .toList();

        wildfireRepository.saveAll(extinguished);
    }
}
