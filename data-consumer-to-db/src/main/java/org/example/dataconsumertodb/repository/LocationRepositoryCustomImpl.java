package org.example.dataconsumertodb.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.dataconsumertodb.model.Location;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class LocationRepositoryCustomImpl implements LocationRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    @Override
    public List<Location> findOrSave(List<Location> locations) {

        var locationsFound = findExisting(locations);

        var locationsNotFound = new ArrayList<>(locations);
        locationsNotFound.removeAll(locationsFound);

        var locationsResult = new ArrayList<>(locationsFound);

        for (var location : locationsNotFound) {
            entityManager.persist(location);
            locationsResult.add(location);
        }

        return locationsResult;
    }

    @Transactional
    @Override
    public List<Location> findExisting(List<Location> locations) {

        List<String> names = locations.stream().map(Location::getName).distinct().toList();
        List<String> regions = locations.stream().map(Location::getRegion).distinct().toList();
        List<String> countries = locations.stream().map(Location::getCountry).distinct().toList();

        var locationsFound = entityManager.createQuery("FROM Location " +
                        "WHERE name IN :names " +
                        "AND region IN :regions " +
                        "AND country IN :countries", Location.class)
                .setParameter("names", names)
                .setParameter("regions", regions)
                .setParameter("countries", countries)
                .getResultList();

        locationsFound.retainAll(locations);
        return locationsFound;
    }
}
