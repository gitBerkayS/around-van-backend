package com.aroundvan.backend.gas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface GasPriceDailyRepository extends JpaRepository<GasPriceDaily, Long> {

    Optional<GasPriceDaily> findByStationIdAndFuelTypeAndPriceDate(
            Long stationId,
            FuelType fuelType,
            LocalDate priceDate
    );

    boolean existsByFuelTypeAndPriceDateBetween(
            FuelType fuelType,
            LocalDate startInclusive,
            LocalDate endInclusive
    );

    @Query("""
            SELECT AVG(d.price)
            FROM GasPriceDaily d
            WHERE d.fuelType = :fuelType
              AND d.priceDate = :priceDate
            """)
    Double averagePriceForDate(
            @Param("fuelType") FuelType fuelType,
            @Param("priceDate") LocalDate priceDate
    );

    @Query(value = """
            SELECT AVG(day_avg)
            FROM (
                SELECT AVG(price) AS day_avg
                FROM gas_price_daily
                WHERE fuel_type = :fuelType
                  AND price_date >= :startDate
                  AND price_date <= :endDate
                GROUP BY price_date
            ) daily
            """, nativeQuery = true)
    Double averageOfDailyAverages(
            @Param("fuelType") String fuelType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
