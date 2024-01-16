package org.example.dataconsumertodb.repository;

import org.example.dataconsumertodb.model.Location;
import org.example.dataconsumertodb.model.WeatherForDay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
class WeatherRepositoryCustomImplTest {

    @Autowired
    private WeatherRepository weatherRepository;

    private List<WeatherForDay> weatherEntities;

    @BeforeEach
    void setUp() {
        weatherEntities = weatherRepository.findAll();
        assertFalse(weatherEntities.isEmpty());
    }

    @Test
    void testFindExisting_OnlyExisting() {
        var result = weatherRepository.findExisting(weatherEntities);
        assertEquals(new HashSet<>(weatherEntities), new HashSet<>(result));
    }

    @Test
    void testFindExisting_ExistingMixedWithNew() {

        var newWeatherEntities = List.of(
                new WeatherForDay(null, LocalDate.MIN, 4.1, new Location(null, "name 1 test", "region 1 test", "country 1 test")),
                new WeatherForDay(null, LocalDate.now(), -2.3, new Location(null, "name 2 test", "region 2 test", "country 2 test")),
                new WeatherForDay(null, LocalDate.MAX, 19.0, new Location(null, "name 3 test", "region 3 test", "country 3 test"))
        );

        var weatherEntitiesAll = new ArrayList<>(weatherEntities);
        weatherEntitiesAll.addAll(newWeatherEntities);

        var result = weatherRepository.findExisting(weatherEntitiesAll);
        assertEquals(new HashSet<>(weatherEntities), new HashSet<>(result));
    }

    @Test
    void testFindExisting_OnlyNew() {

        var newWeatherEntities = List.of(
                new WeatherForDay(null, LocalDate.MIN, 4.1, new Location(null, "name 1 test", "region 1 test", "country 1 test")),
                new WeatherForDay(null, LocalDate.now(), -2.3, new Location(null, "name 2 test", "region 2 test", "country 2 test")),
                new WeatherForDay(null, LocalDate.MAX, 19.0, new Location(null, "name 3 test", "region 3 test", "country 3 test"))
        );

        var result = weatherRepository.findExisting(newWeatherEntities);
        assertTrue(result.isEmpty());
    }
}