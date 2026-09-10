package com.aroundvan.backend.gas;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(
        name = "gas_price_daily",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_gas_price_daily_station_fuel_date",
                columnNames = {"station_id", "fuel_type", "price_date"}
        )
)
public class GasPriceDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id", nullable = false)
    private GasStation station;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false, length = 20)
    private FuelType fuelType;

    @Column(name = "price_date", nullable = false)
    private LocalDate priceDate;

    @Column(nullable = false, precision = 6, scale = 3)
    private BigDecimal price;

    @Column(nullable = false)
    private Instant observedAt;
}
