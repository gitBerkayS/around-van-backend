package com.aroundvan.backend.events.provider;

import com.aroundvan.backend.events.Event;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ExternalEventMapper {

    public void applyToEvent(Event event, ExternalEventData data) {
        event.setProvider(data.provider());
        event.setExternalId(data.externalId());

        event.setTitle(data.name());
        event.setDescription(data.description());
        event.setDateStart(data.startDate());
        event.setDateEnd(data.endDate());

        if (data.publishedDate() != null) {
            event.setPublishedDate(data.publishedDate());
        } else if (event.getPublishedDate() == null) {
            event.setPublishedDate(Instant.now());
        }

        event.setExternalUrl(data.url());
        event.setImageUrl(data.imageUrl());
    }
}
