package org.example.dataconsumertodb.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.log4j.Log4j2;
import org.example.dataconsumertodb.model.Location;
import org.example.dataconsumertodb.model.WeatherForDay;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
public class WeatherRepositoryCustomImpl implements WeatherRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public List<WeatherForDay> findExisting(List<WeatherForDay> weatherEntities) {

        List<LocalDate> days = weatherEntities.stream().map(WeatherForDay::getDay).distinct().toList();
        List<Location> locations = weatherEntities.stream().map(WeatherForDay::getLocation).distinct().collect(Collectors.toList());

        var wereTransientLocations = locations.removeIf(l -> l.getId() == null);
        if (wereTransientLocations) {
            log.warn("In order to find the existing weatherForDay objects, objects with transient locations objects were transmitted!!!");
        }

        var weatherEntitiesFound = entityManager.createQuery("FROM WeatherForDay " +
                        "WHERE day IN :days " +
                        "AND location IN :locations", WeatherForDay.class)
                .setParameter("days", days)
                .setParameter("locations", locations)
                .getResultList();

        weatherEntitiesFound.retainAll(weatherEntities);
        return weatherEntitiesFound;
    }
}
