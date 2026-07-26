package com.aroundvan.backend.events;

import com.aroundvan.backend.events.provider.EventProvider;
import com.aroundvan.backend.location.neighbourhood.Neighbourhood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByDateStartGreaterThanEqual(Instant dateStart);

    List<Event> findAllByDateStartBefore(Instant dateStart);

    Optional<Event> findByProviderAndExternalId(
            EventProvider provider,
            String externalId
    );

    List<Event> findAllByLocationNeighbourhood(
            Neighbourhood neighbourhood
    );
}