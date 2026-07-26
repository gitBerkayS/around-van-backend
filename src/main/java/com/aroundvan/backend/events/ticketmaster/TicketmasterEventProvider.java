package com.aroundvan.backend.events.ticketmaster;

import com.aroundvan.backend.events.provider.ExternalEventData;
import com.aroundvan.backend.events.provider.ExternalEventProvider;
import com.aroundvan.backend.events.ticketmaster.dto.TicketmasterEventsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TicketmasterEventProvider
        implements ExternalEventProvider {

    private final TicketmasterClient ticketmasterClient;
    private final TicketmasterEventMapper ticketmasterEventMapper;

    @Override
    public List<ExternalEventData> fetchEvents() {
        TicketmasterEventsResponse response =
                ticketmasterClient.getVancouverEvents();

        return ticketmasterEventMapper.toExternalEvents(response);
    }
}