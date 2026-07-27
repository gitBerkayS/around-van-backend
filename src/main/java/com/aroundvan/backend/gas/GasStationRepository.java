package com.aroundvan.backend.gas;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GasStationRepository extends JpaRepository<GasStation, Long> {

    Optional<GasStation> findByExternalKey(String externalKey);
}
