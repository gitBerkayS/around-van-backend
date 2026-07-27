package com.aroundvan.backend.gas;

import com.aroundvan.backend.location.Location;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(
        name = "gas_station",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_gas_station_external_key",
                columnNames = "external_key"
        )
)
public class GasStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String externalKey;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 1000)
    private String address;

    @Column(nullable = false, length = 10)
    private String postalCodePrefix;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    private Instant lastSyncedAt;
}
