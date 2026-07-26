package com.aroundvan.backend.events;

import com.aroundvan.backend.events.provider.EventProvider;
import com.aroundvan.backend.location.Location;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
@Entity
@Table(name = "event", uniqueConstraints = {
                @UniqueConstraint(name = "event_provider_external_id", columnNames = {"provider", "external_id"
                })
        }
)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String description;

    private Instant publishedDate;

    private Instant dateEnd;

    private Instant dateStart;

    @OneToOne(fetch = FetchType.LAZY)
    private Location location;

    @Column(name = "external_id")
    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private EventProvider provider;

    @Column(name = "external_url")
    private String externalUrl;

    @Column(name = "image_url")
    private String imageUrl;
}
