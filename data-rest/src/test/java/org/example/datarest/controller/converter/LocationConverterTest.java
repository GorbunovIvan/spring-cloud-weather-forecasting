package org.example.datarest.controller.converter;

import org.example.datarest.model.Location;
import org.example.datarest.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class LocationConverterTest {

    @Autowired
    private LocationConverter locationConverter;

    @MockBean
    private LocationService locationService;

    private List<Location> locations;

    @BeforeEach
    void setUp() {

        locations = List.of(
                new Location(1, "name 1 test", "region 1 test", "country 1 test"),
                new Location(2, "name 2 test", "region 2 test", "country 2 test"),
                new Location(3, "name 3 test", "region 3 test", "country 3 test"),
                new Location(4, "name 4 test", "region 4 test", "country 4 test")
        );

        when(locationService.getByName("")).thenReturn(null);

        for (var location : locations) {
            when(locationService.getByName(location.getName())).thenReturn(location);
            when(locationService.getByName(location.getName().toLowerCase())).thenReturn(location);
            when(locationService.getByName(location.getName().toUpperCase())).thenReturn(location);
        }
    }

    @Test
    void testConvert() {
        for (var location : locations) {
            var result = locationConverter.convert(location.getName());
            assertEquals(location, result);
            verify(locationService, times(1)).getByName(location.getName());
        }
        verify(locationService, times(locations.size())).getByName(anyString());
    }

    @Test
    void testConvert_Null() {
        var result = locationConverter.convert("");
        assertNull(result);
        verify(locationService, times(1)).getByName("");
    }
}