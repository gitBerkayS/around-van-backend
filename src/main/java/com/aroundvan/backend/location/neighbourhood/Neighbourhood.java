package com.aroundvan.backend.location.neighbourhood;

import com.aroundvan.backend.environment.aqhi.AqhiRegion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Polygon;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Neighbourhood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @JsonIgnore
    @Column(nullable = false, columnDefinition = "geometry(Polygon,4326)")
    private Polygon boundary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Municipality municipality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aqhi_region_location_id")
    private AqhiRegion aqhiRegion;
}
