package org.example.dataconsumertodb.repository;

import org.example.dataconsumertodb.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
class LocationRepositoryCustomImplTest {

    @Autowired
    private LocationRepository locationRepository;

    private List<Location> locations;

    @BeforeEach
    void setUp() {
        locations = locationRepository.findAll();
        assertFalse(locations.isEmpty());
    }

    @Test
    void testFindOrSave_OnlyExisting() {

        var result = locationRepository.findOrSave(locations);
        assertEquals(new HashSet<>(locations), new HashSet<>(result));

        // Nothing changed in DB
        assertEquals(locations, locationRepository.findAll());
    }

    @Test
    void testFindOrSave_ExistingMixedWithNew() {

        var newLocations = List.of(
                new Location(null, "name-test 1", "region-test 1", "country-test 1"),
                new Location(null, "name-test 2", "region-test 2", "country-test 2"),
                new Location(null, "name-test 3", "region-test 3", "country-test 3")
        );

        var locationsAll = new ArrayList<>(locations);
        locationsAll.addAll(newLocations);

        var result = locationRepository.findOrSave(locationsAll);
        assertEquals(new HashSet<>(locationsAll), new HashSet<>(result));

        // New objects have been added in DB
        assertEquals(new HashSet<>(result), new HashSet<>(locationRepository.findAll()));
    }

    @Test
    void testFindOrSave_OnlyNew() {

        var newLocations = List.of(
                new Location(null, "name-test 1", "region-test 1", "country-test 1"),
                new Location(null, "name-test 2", "region-test 2", "country-test 2"),
                new Location(null, "name-test 3", "region-test 3", "country-test 3")
        );

        var result = locationRepository.findOrSave(newLocations);
        assertEquals(new HashSet<>(newLocations), new HashSet<>(result));

        // New objects have been added to DB
        var allLocationsExpected = new ArrayList<>(locations);
        allLocationsExpected.addAll(result);
        assertEquals(new HashSet<>(allLocationsExpected), new HashSet<>(locationRepository.findAll()));
    }

    @Test
    void testFindExisting_OnlyExisting() {
        var result = locationRepository.findExisting(locations);
        assertEquals(new HashSet<>(locations), new HashSet<>(result));
    }

    @Test
    void testFindExisting_ExistingMixedWithNew() {

        var newLocations = List.of(
                new Location(null, "name-test 1", "region-test 1", "country-test 1"),
                new Location(null, "name-test 2", "region-test 2", "country-test 2"),
                new Location(null, "name-test 3", "region-test 3", "country-test 3")
        );

        var locationsAll = new ArrayList<>(locations);
        locationsAll.addAll(newLocations);

        var result = locationRepository.findExisting(locationsAll);
        assertEquals(new HashSet<>(locations), new HashSet<>(result));
    }

    @Test
    void testFindExisting_OnlyNew() {

        var newLocations = List.of(
                new Location(null, "name-test 1", "region-test 1", "country-test 1"),
                new Location(null, "name-test 2", "region-test 2", "country-test 2"),
                new Location(null, "name-test 3", "region-test 3", "country-test 3")
        );

        var result = locationRepository.findExisting(newLocations);
        assertTrue(result.isEmpty());
    }
}