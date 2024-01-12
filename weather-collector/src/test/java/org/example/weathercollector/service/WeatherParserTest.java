package org.example.weathercollector.service;

import org.example.weathercollector.model.Location;
import org.example.weathercollector.model.WeatherForDay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class WeatherParserTest {

    @Autowired
    private WeatherParser weatherParser;

    @MockBean
    private RestTemplate restTemplate;
    @MockBean
    private WeatherPublisher weatherPublisher;

    private Map<Location, ResponseEntity<WeatherForDay>> mockedLocationResponsesOfWeather;

    @BeforeEach
    void setUp() {

        var location = new Location("name-test", "region-test", "country-test");
        WeatherForDay weatherForDayToMock = new WeatherForDay(null, location, 5.2);

        mockedLocationResponsesOfWeather = new HashMap<>();
        mockedLocationResponsesOfWeather.put(new Location("200"), ResponseEntity.ok(weatherForDayToMock));
        mockedLocationResponsesOfWeather.put(new Location("404"), ResponseEntity.notFound().build());
        mockedLocationResponsesOfWeather.put(new Location("200-null"), ResponseEntity.ok(null));
        mockedLocationResponsesOfWeather.put(new Location("400"), ResponseEntity.badRequest().build());

        for (var entry : mockedLocationResponsesOfWeather.entrySet()) {
            when(restTemplate.getForEntity(weatherParser.getURLToParseFrom(entry.getKey().getName()), WeatherForDay.class)).thenReturn(entry.getValue());
        }
    }

    @Test
    void testParseWeatherForDayByLocation() {
        for (var entry : mockedLocationResponsesOfWeather.entrySet()) {
            var result = weatherParser.parseWeatherForDayByLocation(entry.getKey());
            assertEquals(entry.getValue().getBody(), result);
            verify(restTemplate, atLeast(1)).getForEntity(weatherParser.getURLToParseFrom(entry.getKey().getName()), WeatherForDay.class);
        }
    }

    @Test
    void testGetLocations() {
        var result = weatherParser.getLocations();
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}