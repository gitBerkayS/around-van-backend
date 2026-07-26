package com.aroundvan.backend.environment.aqhi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class AqhiImportScheduler {

    private final AqhiImportService aqhiImportService;

    // ec publishes one observation per region per hour
    @Scheduled(fixedDelay = 20, initialDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void importLatestReadings() {
        try {
            int storedReadings = aqhiImportService.importLatestReadings();

            log.info("Scheduled AQHI import stored {} new readings", storedReadings);
        } catch (Exception exception) {
            log.error("Scheduled AQHI import failed", exception);
        }
    }
}
