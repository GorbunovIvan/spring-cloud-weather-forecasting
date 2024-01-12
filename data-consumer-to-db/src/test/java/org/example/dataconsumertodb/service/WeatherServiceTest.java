package org.example.dataconsumertodb.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.dataconsumertodb.model.Location;
import org.example.dataconsumertodb.model.WeatherForDay;
import org.example.dataconsumertodb.repository.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class WeatherServiceTest {

    @Autowired
    private WeatherService weatherService;

    @SpyBean
    private LocationService locationService;
    @SpyBean
    private WeatherRepository weatherRepository;

    private List<WeatherForDay> weatherEntities;

    @BeforeEach
    void setUp() {
        weatherEntities = weatherRepository.findAll();
        assertFalse(weatherEntities.isEmpty());
    }

    @Test
    void testCreateIfNotExisting_SingleExisting() {

        for (var weather : weatherEntities) {

            var result = weatherService.createIfNotExisting(weather);
            assertEquals(weather, result);
            assertEquals(weather.getTemperatureCelsius(), result.getTemperatureCelsius());

            verify(locationService, atLeast(1)).findOrSave(weather.getLocation());
            verify(weatherRepository, times(1)).findOne(Example.of(weather));
            verify(weatherRepository, times(1)).existsById(weather.getId()); // Ensuring that "update()" method was invoked
            verify(weatherRepository, times(1)).save(weather);
        }
    }

    @Test
    void testCreateIfNotExisting_SingleNew() {

        var location = new Location(null, "name 1 test", "region 1 test", "country 1 test");
        var weather = new WeatherForDay(null, LocalDate.MIN, 4.1, location);

        var result = weatherService.createIfNotExisting(weather);
        assertEquals(weather, result);
        assertEquals(weather.getTemperatureCelsius(), result.getTemperatureCelsius());

        verify(locationService, times(1)).findOrSave(weather.getLocation());
        verify(weatherRepository, times(1)).findOne(Example.of(weather));
        verify(weatherRepository, never()).existsById(weather.getId()); // Ensuring that "update()" method was not invoked
        verify(weatherRepository, times(1)).save(weather);
    }

    @Test
    void testCreateIfNotExisting_SeveralExisting() {

        weatherEntities = weatherEntities.stream().distinct().toList();

        var result = weatherService.createIfNotExisting(weatherEntities);
        assertEquals(new HashSet<>(weatherEntities), new HashSet<>(result));

        verify(locationService, times(1)).findOrSave(anyList());
        verify(weatherRepository, times(1)).findExisting(weatherEntities);

        // Verifying invocation of "weatherRepository.saveAll()"
        ArgumentCaptor<List<WeatherForDay>> captor = ArgumentCaptor.forClass(List.class);
        verify(weatherRepository, times(1)).saveAll(captor.capture());
        var capturedWeatherEntities = captor.getValue();
        assertEquals(new HashSet<>(weatherEntities), new HashSet<>(capturedWeatherEntities));
    }

    @Test
    void testCreateIfNotExisting_SeveralExistingMixedWithNew() {

        var newWeatherEntities = List.of(
                new WeatherForDay(null, LocalDate.MIN, 4.1, new Location(null, "name 1 test", "region 1 test", "country 1 test")),
                new WeatherForDay(null, LocalDate.now(), -2.3, new Location(null, "name 2 test", "region 2 test", "country 2 test")),
                new WeatherForDay(null, LocalDate.MAX, 19.0, new Location(null, "name 3 test", "region 3 test", "country 3 test"))
        );

        List<WeatherForDay> weatherEntitiesAll = new ArrayList<>(weatherEntities);
        weatherEntitiesAll.addAll(newWeatherEntities);

        weatherEntitiesAll = weatherEntitiesAll.stream().distinct().toList();

        var result = weatherService.createIfNotExisting(weatherEntitiesAll);
        assertEquals(new HashSet<>(weatherEntitiesAll), new HashSet<>(result));

        verify(locationService, times(1)).findOrSave(anyList());
        verify(weatherRepository, times(1)).findExisting(weatherEntitiesAll);

        // Verifying invocation of "weatherRepository.saveAll()"
        ArgumentCaptor<List<WeatherForDay>> captor = ArgumentCaptor.forClass(List.class);
        verify(weatherRepository, times(2)).saveAll(captor.capture());
        var capturedWeatherEntities = captor.getAllValues();
        assertEquals(new HashSet<>(weatherEntities), new HashSet<>(capturedWeatherEntities.get(0)));
        assertEquals(new HashSet<>(newWeatherEntities), new HashSet<>(capturedWeatherEntities.get(1)));
    }

    @Test
    void testCreateIfNotExisting_SeveralNew() {

        var newWeatherEntities = List.of(
                new WeatherForDay(null, LocalDate.MIN, 4.1, new Location(null, "name 1 test", "region 1 test", "country 1 test")),
                new WeatherForDay(null, LocalDate.now(), -2.3, new Location(null, "name 2 test", "region 2 test", "country 2 test")),
                new WeatherForDay(null, LocalDate.MAX, 19.0, new Location(null, "name 3 test", "region 3 test", "country 3 test"))
        );

        var result = weatherService.createIfNotExisting(newWeatherEntities);
        assertEquals(new HashSet<>(newWeatherEntities), new HashSet<>(result));

        verify(locationService, times(1)).findOrSave(anyList());
        verify(weatherRepository, times(1)).findExisting(newWeatherEntities);

        // Verifying invocation of "weatherRepository.saveAll()"
        ArgumentCaptor<List<WeatherForDay>> captor = ArgumentCaptor.forClass(List.class);
        verify(weatherRepository, times(1)).saveAll(captor.capture());
        var capturedWeatherEntities = captor.getValue();
        assertEquals(new HashSet<>(newWeatherEntities), new HashSet<>(capturedWeatherEntities));
    }

    @Test
    void testCreate() {

        var location = new Location(null, "name 1 test", "region 1 test", "country 1 test");
        var weather = new WeatherForDay(null, LocalDate.MIN, 4.1, location);

        var result = weatherService.create(weather);
        assertEquals(weather, result);
        assertEquals(weather.getTemperatureCelsius(), result.getTemperatureCelsius());

        verify(weatherRepository, times(1)).save(weather);
    }

    @Test
    void testCreateAll() {

        var newWeatherEntities = List.of(
                new WeatherForDay(null, LocalDate.MIN, 4.1, new Location(null, "name 1 test", "region 1 test", "country 1 test")),
                new WeatherForDay(null, LocalDate.now(), -2.3, new Location(null, "name 2 test", "region 2 test", "country 2 test")),
                new WeatherForDay(null, LocalDate.MAX, 19.0, new Location(null, "name 3 test", "region 3 test", "country 3 test"))
        );

        var result = weatherService.createAll(newWeatherEntities);
        assertEquals(new HashSet<>(newWeatherEntities), new HashSet<>(result));
        verify(weatherRepository, times(1)).saveAll(newWeatherEntities);
    }

    @Test
    void testUpdate() {

        for (var weather : weatherEntities) {

            long id = weather.getId();

            var result = weatherService.update(id, weather);
            assertEquals(weather, result);

            verify(weatherRepository, times(1)).existsById(id);
            verify(weatherRepository, times(1)).save(weather);
        }
    }

    @Test
    void testUpdate_NonExisting() {

        var location = new Location(null, "name 1 test", "region 1 test", "country 1 test");
        var weather = new WeatherForDay(null, LocalDate.MIN, 4.1, location);

        long id = -1;

        WeatherForDay result = null;

        try {
            result = weatherService.update(id, weather);
        } catch (EntityNotFoundException ignored) {
        }

        assertNull(result);
        verify(weatherRepository, times(1)).existsById(id);
        verify(weatherRepository, never()).save(weather);
    }
}