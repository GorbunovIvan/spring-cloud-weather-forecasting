package org.example.dataconsumertodb.repository;

import org.example.dataconsumertodb.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Integer>, LocationRepositoryCustom {
}
