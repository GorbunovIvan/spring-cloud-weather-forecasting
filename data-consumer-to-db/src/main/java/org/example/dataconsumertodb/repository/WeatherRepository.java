package org.example.dataconsumertodb.repository;

import org.example.dataconsumertodb.model.WeatherForDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRepository extends JpaRepository<WeatherForDay, Long>, WeatherRepositoryCustom {
}
