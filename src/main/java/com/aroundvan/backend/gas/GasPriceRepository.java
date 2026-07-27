package com.aroundvan.backend.gas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GasPriceRepository extends JpaRepository<GasPrice, Long> {

    Optional<GasPrice> findByStationIdAndFuelType(Long stationId, FuelType fuelType);

    @Query("""
            SELECT gp FROM GasPrice gp
            JOIN FETCH gp.station s
            JOIN FETCH s.location l
            WHERE gp.fuelType = :fuelType
            """)
    List<GasPrice> findAllByFuelTypeWithStationLocation(@Param("fuelType") FuelType fuelType);
}
