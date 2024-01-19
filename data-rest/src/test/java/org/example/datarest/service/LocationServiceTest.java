package org.example.datarest.service;

import org.example.datarest.model.Location;
import org.example.datarest.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class LocationServiceTest {

    @Autowired
    private LocationService locationService;

    @MockBean
    private LocationRepository locationRepository;

    private List<Location> locations;

    @BeforeEach
    void setUp() {

        locations = List.of(
                new Location(1, "name 1 test", "region 1 test", "country 1 test"),
                new Location(2, "name 2 test", "region 2 test", "country 2 test"),
                new Location(3, "name 3 test", "region 3 test", "country 3 test"),
                new Location(4, "name 4 test", "region 4 test", "country 4 test")
        );

        when(locationRepository.findByNameIgnoreCase("")).thenReturn(Optional.empty());

        for (var location : locations) {
            when(locationRepository.findByNameIgnoreCase(location.getName())).thenReturn(Optional.of(location));
            when(locationRepository.findByNameIgnoreCase(location.getName().toLowerCase())).thenReturn(Optional.of(location));
            when(locationRepository.findByNameIgnoreCase(location.getName().toUpperCase())).thenReturn(Optional.of(location));
        }
    }

    @Test
    void testGetByName() {
        for (var location : locations) {
            var result = locationService.getByName(location.getName());
            assertEquals(location, result);
            verify(locationRepository, times(1)).findByNameIgnoreCase(location.getName());
        }
    }

    @Test
    void testGetByName_NotFound() {
        var result = locationService.getByName("");
        assertNull(result);
        verify(locationRepository, times(1)).findByNameIgnoreCase("");
    }
}