package com.aroundvan.backend.location.neighbourhood;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NeighbourhoodRepository extends JpaRepository<Neighbourhood, Long> {

    List<Neighbourhood> findAllByAqhiRegionIsNull();

    @Query(
            value = """
                    SELECT *
                    FROM neighbourhood
                    WHERE ST_Contains(
                            boundary,
                            ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)
                    )
                    LIMIT 1
                    """,
            nativeQuery = true
    )
    Optional<Neighbourhood> findByCoordinates(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude
    );
}
