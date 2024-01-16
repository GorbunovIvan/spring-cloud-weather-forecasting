package org.example.datarest.repository;

import org.example.datarest.model.Location;
import org.example.datarest.model.WeatherForDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WeatherRepository extends JpaRepository<WeatherForDay, Long> {

    List<WeatherForDay> findAllByLocation(@Param("location") Location location);

    // In fact, the result should always contain at most 1 element
    List<WeatherForDay> findAllByLocationAndDay(@Param("location") Location location,
                                                @Param("day") LocalDate day);
}
