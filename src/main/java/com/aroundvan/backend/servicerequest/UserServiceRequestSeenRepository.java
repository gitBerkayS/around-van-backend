package com.aroundvan.backend.servicerequest;

import com.aroundvan.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Set;

public interface UserServiceRequestSeenRepository
        extends JpaRepository<UserServiceRequestSeen, UserServiceRequestSeenId> {

    boolean existsByUserAndServiceRequest(User user, ServiceRequest serviceRequest);

    @Query("""
            SELECT s.serviceRequest.id FROM UserServiceRequestSeen s
            WHERE s.user = :user
              AND s.serviceRequest.id IN :ids
            """)
    Set<Long> findSeenServiceRequestIds(
            @Param("user") User user,
            @Param("ids") Collection<Long> ids
    );
}
