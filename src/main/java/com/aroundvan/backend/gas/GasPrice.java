package com.aroundvan.backend.gas;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(
        name = "gas_price",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_gas_price_station_fuel",
                columnNames = {"station_id", "fuel_type"}
        )
)
public class GasPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id", nullable = false)
    private GasStation station;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false, length = 20)
    private FuelType fuelType;

    @Column(nullable = false, precision = 6, scale = 3)
    private BigDecimal price;

    @Column(nullable = false)
    private Instant observedAt;
}
