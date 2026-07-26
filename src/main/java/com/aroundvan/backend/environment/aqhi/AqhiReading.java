package com.aroundvan.backend.environment.aqhi;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_aqhi_reading_region_observed_at", columnNames = {"region_location_id", "observed_at"})
)
//fetched api data
public class AqhiReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "region_location_id", nullable = false)
    private AqhiRegion region;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private Instant observedAt;

    private Instant fetchedAt;
}
