package com.aroundvan.backend.environment.aqhi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface AqhiReadingRepository extends JpaRepository<AqhiReading, Long> {

    Optional<AqhiReading> findFirstByRegionLocationIdOrderByObservedAtDesc(String locationId);

    Optional<AqhiReading> findByRegionLocationIdAndObservedAt(String locationId, Instant observedAt);
}
