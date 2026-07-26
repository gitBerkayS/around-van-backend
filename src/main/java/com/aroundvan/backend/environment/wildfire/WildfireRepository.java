package com.aroundvan.backend.environment.wildfire;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WildfireRepository extends JpaRepository<Wildfire, Long> {

    Optional<Wildfire> findByFireNumber(String fireNumber);

    List<Wildfire> findAllByStatusNot(String status);
}
