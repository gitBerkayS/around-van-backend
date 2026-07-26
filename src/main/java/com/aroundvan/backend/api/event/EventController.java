package com.aroundvan.backend.api.event;

import com.aroundvan.backend.events.EventService;
import com.aroundvan.backend.events.dto.EventResponse;
import com.aroundvan.backend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    @GetMapping("/upcoming")
    public List<EventResponse> getUpcomingEvents() {
        return eventService.getAllUpcomingEvents();
    }

    @GetMapping("/upcoming/near")
    public List<EventResponse> getAllEventsNearby() {
        return eventService.getUpcomingEventsNearbyForUser(
                userService.getCurrentUser()
        );
    }

    @GetMapping("/past")
    public List<EventResponse> getPastEvents() {
        return eventService.getAllPastEvents();
    }
}