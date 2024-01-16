package org.example.dataconsumertodb.service;

import org.example.dataconsumertodb.model.Location;
import org.example.dataconsumertodb.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class LocationServiceTest {

    @Autowired
    private LocationService locationService;

    @SpyBean
    private LocationRepository locationRepository;

    private List<Location> locations;

    @BeforeEach
    void setUp() {
        locations = locationRepository.findAll();
        assertFalse(locations.isEmpty());
    }

    @Test
    void testFindOrSave_SingleExisting() {

        for (var location : locations) {

            var result = locationService.findOrSave(location);
            assertEquals(location, result);

            verify(locationRepository, times(1)).findOne(Example.of(location));
            verify(locationRepository, never()).save(any(Location.class));
        }
    }

    @Test
    void testFindOrSave_SingleNew() {

        var newLocation = new Location(null, "name-test 1", "region-test 1", "country-test 1");

        var result = locationService.findOrSave(newLocation);
        assertNotNull(newLocation.getId());
        assertEquals(newLocation, result);

        verify(locationRepository, times(1)).findOne(Example.of(newLocation));
        verify(locationRepository, times(1)).save(newLocation);
    }

    @Test
    void testFindOrSave_SeveralExisting() {
        var result = locationService.findOrSave(locations);
        assertEquals(locations, result);
        verify(locationRepository, times(1)).findOrSave(locations);
    }

    @Test
    void testFindOrSave_SeveralExistingMixedWithNew() {

        var newLocations = List.of(
                new Location(null, "name-test 1", "region-test 1", "country-test 1"),
                new Location(null, "name-test 2", "region-test 2", "country-test 2"),
                new Location(null, "name-test 3", "region-test 3", "country-test 3")
        );

        var locationsAll = new ArrayList<>(locations);
        locationsAll.addAll(newLocations);

        var result = locationService.findOrSave(locationsAll);
        assertEquals(locationsAll, result);
        verify(locationRepository, times(1)).findOrSave(locationsAll);
    }

    @Test
    void testFindOrSave_SeveralNew() {

        var newLocations = List.of(
                new Location(null, "name-test 1", "region-test 1", "country-test 1"),
                new Location(null, "name-test 2", "region-test 2", "country-test 2"),
                new Location(null, "name-test 3", "region-test 3", "country-test 3")
        );

        var result = locationService.findOrSave(newLocations);
        assertEquals(newLocations, result);
        verify(locationRepository, times(1)).findOrSave(newLocations);
    }

    @Test
    void testCreate() {

        var newLocation = new Location(null, "name-test 1", "region-test 1", "country-test 1");

        var result = locationService.create(newLocation);
        assertNotNull(newLocation.getId());
        assertEquals(newLocation, result);

        verify(locationRepository, times(1)).save(newLocation);
    }
}