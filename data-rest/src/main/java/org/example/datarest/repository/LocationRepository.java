package org.example.datarest.repository;

import org.example.datarest.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    Optional<Location> findByNameIgnoreCase(String name);
}
