package com.aroundvan.backend.servicerequest;

import com.aroundvan.backend.location.neighbourhood.Neighbourhood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    Optional<ServiceRequest> findByExternalKey(String externalKey);

    List<ServiceRequest> findAllByStatus(ServiceRequestStatus status);

    @Query("""
            SELECT sr FROM ServiceRequest sr
            LEFT JOIN FETCH sr.location loc
            LEFT JOIN FETCH loc.neighbourhood
            WHERE sr.status = :status
              AND sr.importance <> :hidden
              AND sr.category IN :categories
              AND sr.location IS NOT NULL
            """)
    List<ServiceRequest> findOpenVisibleWithLocation(
            @Param("status") ServiceRequestStatus status,
            @Param("hidden") ServiceRequestImportance hidden,
            @Param("categories") Collection<ServiceRequestCategory> categories
    );

    @Query("""
            SELECT sr FROM ServiceRequest sr
            LEFT JOIN FETCH sr.location loc
            LEFT JOIN FETCH loc.neighbourhood nh
            WHERE sr.status = :status
              AND sr.importance = :importance
              AND sr.category IN :categories
              AND nh = :neighbourhood
            """)
    List<ServiceRequest> findOpenImportantInNeighbourhood(
            @Param("status") ServiceRequestStatus status,
            @Param("importance") ServiceRequestImportance importance,
            @Param("categories") Collection<ServiceRequestCategory> categories,
            @Param("neighbourhood") Neighbourhood neighbourhood
    );
}
