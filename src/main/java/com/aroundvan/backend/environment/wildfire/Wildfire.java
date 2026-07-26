package com.aroundvan.backend.environment.wildfire;

import com.aroundvan.backend.location.Location;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class Wildfire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String fireNumber;

    private Integer bcFireId;

    private String incidentName;

    @Column(length = 2000)
    private String geographicDescription;

    @OneToOne(fetch = FetchType.LAZY)
    private Location location;

    private Double currentSizeHectares;

    private String status;

    private String cause;

    private String responseType;

    private Integer fireCentreCode;

    private Integer zoneCode;

    private String fireType;

    private Instant ignitionDate;

    private Instant fireOutDate;

    private boolean fireOfNote;

    private boolean wasFireOfNote;

    private String fireUrl;

    private Instant lastSyncedAt;
}