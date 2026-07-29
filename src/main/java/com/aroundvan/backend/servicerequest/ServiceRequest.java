package com.aroundvan.backend.servicerequest;

import com.aroundvan.backend.location.Location;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "service_request")
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_key", nullable = false, unique = true, length = 64)
    private String externalKey;

    @Column(name = "request_type", nullable = false)
    private String requestType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ServiceRequestCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ServiceRequestImportance importance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ServiceRequestStatus status;

    private String department;

    @Column(nullable = false, length = 1000)
    private String address;

    @Column(name = "local_area")
    private String localArea;

    private String channel;

    @Column(name = "opened_at", nullable = false)
    private Instant openedAt;

    @Column(name = "last_modified_at")
    private Instant lastModifiedAt;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

    @OneToOne(fetch = FetchType.LAZY)
    private Location location;
}
