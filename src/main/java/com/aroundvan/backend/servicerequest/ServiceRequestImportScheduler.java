package com.aroundvan.backend.servicerequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceRequestImportScheduler {

    private final ServiceRequestImportService serviceRequestImportService;

    @Scheduled(fixedDelay = 6, initialDelay = 2, timeUnit = TimeUnit.HOURS)
    public void importOpenServiceRequests() {
        try {
            int processed = serviceRequestImportService.importOpenServiceRequests();
            log.info("Scheduled Vancouver 311 import processed {} open service requests", processed);
        } catch (Exception exception) {
            log.error("Scheduled Vancouver 311 import failed", exception);
        }
    }
}
