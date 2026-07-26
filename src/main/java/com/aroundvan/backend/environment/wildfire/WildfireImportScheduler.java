package com.aroundvan.backend.environment.wildfire;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class WildfireImportScheduler {

    private final WildfireImportService wildfireImportService;

    @Scheduled(fixedDelay = 15, initialDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void importActiveWildfires() {
        try {
            int processedWildfires = wildfireImportService.importActiveWildfires();

            log.info("Scheduled BCWS wildfire import processed {} active fires", processedWildfires);
        } catch (Exception exception) {
            log.error("Scheduled BCWS wildfire import failed", exception);
        }
    }
}
