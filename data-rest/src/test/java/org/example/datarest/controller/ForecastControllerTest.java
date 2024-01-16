package org.example.datarest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.datarest.model.Location;
import org.example.datarest.model.WeatherForDay;
import org.example.datarest.model.WeatherForDayDto;
import org.example.datarest.service.LocationService;
import org.example.datarest.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ForecastControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private WeatherService weatherService;
    @MockBean
    private LocationService locationService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<WeatherForDay> weatherEntities;
    private List<WeatherForDayDto> weatherDtoEntities;

    private final String baseURL = "/api/v1/weather";

    @BeforeEach
    void setUp() {

        objectMapper.findAndRegisterModules();

        List<Location> locations = List.of(
                new Location(1, "name 1 test", "region 1 test", "country 1 test"),
                new Location(2, "name 2 test", "region 2 test", "country 2 test"),
                new Location(3, "name 3 test", "region 3 test", "country 3 test"),
                new Location(4, "name 4 test", "region 4 test", "country 4 test")
        );

        weatherEntities = List.of(
                new WeatherForDay(1L, LocalDate.now().plusDays(0), -1.0, locations.get(2)),
                new WeatherForDay(2L, LocalDate.now().plusDays(1), -2.0, locations.get(0)),
                new WeatherForDay(3L, LocalDate.now().plusDays(2), 3.0, locations.get(1)),
                new WeatherForDay(4L, LocalDate.now().minusDays(1), 5.1, locations.get(0)),
                new WeatherForDay(5L, LocalDate.now().plusDays(0), 3.2, locations.get(3)),
                new WeatherForDay(6L, LocalDate.now().minusDays(1), 12.1, locations.get(1)),
                new WeatherForDay(7L, LocalDate.now().minusDays(2), 0.0, locations.get(1)),
                new WeatherForDay(8L, LocalDate.now().plusDays(1), -43.0, locations.get(3)),
                new WeatherForDay(9L, LocalDate.now().plusDays(0), 18.1, locations.get(1)),
                new WeatherForDay(10L, LocalDate.now().plusDays(1), 19.0, locations.get(2))
        );

        weatherDtoEntities = new ArrayList<>();
        for (var day : weatherEntities.stream().map(WeatherForDay::getDay).distinct().toList()) {
            for (var location : locations) {
                weatherDtoEntities.add(new WeatherForDayDto(day, location));
            }
        }

        // Mocking
        when(weatherService.forecastWeatherForLocationAndDay(new WeatherForDayDto())).thenReturn(null);
        for (var weather : weatherDtoEntities) {
            when(weatherService.forecastWeatherForLocationAndDay(weather)).thenReturn(weather);
        }

        when(locationService.getByName("")).thenReturn(null);
        for (var location : locations) {
            when(locationService.getByName(location.getName())).thenReturn(location);
        }
    }

    @Test
    void testForecastByLocationAndDay() throws Exception {

        for (var weather : weatherDtoEntities) {

            var jsonResponse = mvc.perform(MockMvcRequestBuilders.get(baseURL + "/forecast")
                        .param("location", weather.getLocation().getName())
                        .param("day", weather.getDay().format(DateTimeFormatter.ISO_DATE)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            var result = objectMapper.readValue(jsonResponse, WeatherForDayDto.class);

            assertEquals(result, weather);
            verify(weatherService, times(1)).forecastWeatherForLocationAndDay(weather);
        }
    }
}