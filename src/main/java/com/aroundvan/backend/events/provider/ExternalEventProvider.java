package com.aroundvan.backend.events.provider;

import java.util.List;

public interface ExternalEventProvider {

    List<ExternalEventData> fetchEvents();
}