package com.aroundvan.backend.environment.aqhi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AqhiRegionRepository extends JpaRepository<AqhiRegion, String> {
}
