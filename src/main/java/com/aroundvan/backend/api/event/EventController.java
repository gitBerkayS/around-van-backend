package com.aroundvan.backend.api.event;

import com.aroundvan.backend.events.EventService;
import com.aroundvan.backend.events.dto.EventResponse;
import com.aroundvan.backend.user.User;
import com.aroundvan.backend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        User user = userService.getCurrentUser();

        if (user.getHomeLocation() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Set your home location before requesting nearby events");
        }

        return eventService.getUpcomingEventsNearbyForUser(user);
    }

    @GetMapping("/past")
    public List<EventResponse> getPastEvents() {
        return eventService.getAllPastEvents();
    }
}
