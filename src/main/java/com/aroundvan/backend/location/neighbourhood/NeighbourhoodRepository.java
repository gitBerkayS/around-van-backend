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
                    SELECT id
                    FROM neighbourhood
                    WHERE ST_Contains(
                            ST_CollectionExtract(boundary, 3),
                            ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)
                    )
                    LIMIT 1
                    """,
            nativeQuery = true
    )
    Optional<Long> findIdByCoordinates(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude
    );

    @Query(
            value = """
                    SELECT name
                    FROM neighbourhood
                    WHERE ST_Contains(
                            ST_CollectionExtract(boundary, 3),
                            ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)
                    )
                    LIMIT 1
                    """,
            nativeQuery = true
    )
    Optional<String> findNameByCoordinates(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude
    );
}
