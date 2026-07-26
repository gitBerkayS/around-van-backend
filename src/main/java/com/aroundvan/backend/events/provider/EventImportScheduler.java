package com.aroundvan.backend.events.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventImportScheduler {

    private final EventImportService eventImportService;

    @Scheduled(fixedDelay = 15, initialDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void importEvents() {
        try {
            int processedEvents = eventImportService.importEvents();

            log.info("Scheduled Ticketmaster import processed {} events", processedEvents);
        }catch (Exception exception) {
            log.error("Scheduled Ticketmaster import failed", exception);
        }

    }
}
