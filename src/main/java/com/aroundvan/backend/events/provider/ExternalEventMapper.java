package com.aroundvan.backend.events.provider;

import com.aroundvan.backend.events.Event;
import org.springframework.stereotype.Component;

@Component
public class ExternalEventMapper {

    public Event applyToEvent(
            Event event,
            ExternalEventData data
    ) {
        event.setProvider(data.provider());
        event.setExternalId(data.externalId());

        event.setTitle(data.name());
        event.setDateStart(data.startDate());

        event.setExternalUrl(data.url());
        event.setImageUrl(data.imageUrl());

        return event;
    }
}