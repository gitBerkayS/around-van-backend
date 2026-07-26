package com.aroundvan.backend.events.provider;

import com.aroundvan.backend.events.Event;
import com.aroundvan.backend.events.EventRepository;
import com.aroundvan.backend.location.Location;
import com.aroundvan.backend.location.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventImportService {

    private final List<ExternalEventProvider> eventProviders;

    private final EventRepository eventRepository;

    private final ExternalEventMapper externalEventMapper;

    private final LocationService locationService;

    @Transactional
    public int importEvents() {
        List<Event> eventsToSave = new ArrayList<>();

        for (ExternalEventProvider provider : eventProviders) {
            List<ExternalEventData> externalEvents =
                    provider.fetchEvents();

            for (ExternalEventData externalEvent : externalEvents) {
                Event event = eventRepository
                        .findByProviderAndExternalId(
                                externalEvent.provider(),
                                externalEvent.externalId()
                        )
                        .orElseGet(Event::new);

                externalEventMapper.applyToEvent(
                        event,
                        externalEvent
                );

                Location location = locationService.resolveEventLocation(
                        event.getLocation(),
                        externalEvent.latitude(),
                        externalEvent.longitude()
                );

                event.setLocation(location);

                eventsToSave.add(event);
            }
        }

        eventRepository.saveAll(eventsToSave);

        return eventsToSave.size();
    }
}