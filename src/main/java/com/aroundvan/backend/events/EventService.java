package com.aroundvan.backend.events;

import com.aroundvan.backend.events.dto.EventResponse;
import com.aroundvan.backend.location.LocationService;
import com.aroundvan.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventService {

    private static final ZoneId VANCOUVER_ZONE =
            ZoneId.of("America/Vancouver");

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final LocationService locationService;


    public Instant getStartOfToday() {
        return LocalDate.now(VANCOUVER_ZONE).atStartOfDay(VANCOUVER_ZONE).toInstant();
    }

    public List<Event> findAllUpcomingEvents() {
        return eventRepository.findAllByDateStartGreaterThanEqual(getStartOfToday());
    }

    public List<EventResponse> getAllUpcomingEvents() {
        return findAllUpcomingEvents().stream().map(eventMapper::toResponse).toList();
    }

    public List<EventResponse> getUpcomingEventsNearbyForUser(User user) {

        return findAllUpcomingEvents()
                .stream()
                .filter(event -> event.getLocation() != null)
                .sorted(Comparator.comparingDouble(event-> locationService.calculateDistanceFromUserInKm(user, event.getLocation()))).map(eventMapper::toResponse).toList();
    }

    public List<EventResponse> getAllPastEvents() {
        return eventRepository.findAllByDateStartBefore(getStartOfToday())
                .stream()
                .map(eventMapper::toResponse).toList();
    }


}