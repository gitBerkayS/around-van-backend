package com.aroundvan.backend.events;

import com.aroundvan.backend.events.dto.EventResponse;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDateStart(),
                event.getDateEnd(),
                event.getExternalUrl(),
                event.getImageUrl(),
                event.getProvider()
        );
    }
}