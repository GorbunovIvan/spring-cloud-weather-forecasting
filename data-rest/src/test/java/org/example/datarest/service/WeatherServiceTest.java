package org.example.datarest.service;

import org.example.datarest.model.Location;
import org.example.datarest.model.WeatherForDay;
import org.example.datarest.model.WeatherForDayDto;
import org.example.datarest.repository.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class WeatherServiceTest {

    @Autowired
    private WeatherService weatherService;

    @MockBean
    private WeatherRepository weatherRepository;

    private List<WeatherForDay> weatherEntities;
    private List<WeatherForDayDto> weatherDtoEntities;

    @Value("${forecast.rangeOfDaysOfDeviation}")
    private int numberOfDaysOfDeviation;

    @BeforeEach
    void setUp() {

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
        when(weatherRepository.findAllByLocation(null)).thenReturn(Collections.emptyList());
        when(weatherRepository.findAllByLocationAndDay(null, null)).thenReturn(Collections.emptyList());

        for (var location : locations) {

            var weatherEntitiesByLocation = weatherEntities.stream()
                                    .filter(w -> w.getLocation().equals(location))
                                    .toList();

            when(weatherRepository.findAllByLocation(location)).thenReturn(weatherEntitiesByLocation);

            for (var weather : weatherEntitiesByLocation) {
                when(weatherRepository.findAllByLocationAndDay(location, weather.getDay())).thenReturn(List.of(weather));
            }
        }
    }

    @Test
    void testForecastWeatherForLocationAndDay() {

        for (var weather : weatherDtoEntities) {

            var result = weatherService.forecastWeatherForLocationAndDay(weather);

            boolean mustBeResult = weatherService.forecastWeatherForLocationAndDayBasedOnHistory(weather) != null
                                || weatherService.forecastWeatherForLocationAndDayBasedOnPreviousDays(weather) != null;

            if (mustBeResult) {
                assertNotNull(result);
                assertEquals(weather, result);
                assertNotNull(result.getTemperatureCelsius());
            } else {
                assertNull(result);
            }
        }
    }

    @Test
    void testForecastWeatherForLocationAndDayBasedOnHistory() {

        for (var weather : weatherDtoEntities) {

            Mockito.clearInvocations(weatherRepository);

            var result = weatherService.forecastWeatherForLocationAndDayBasedOnHistory(weather);

            WeatherForDayDto resultExpected;

            var resultBasedOnHistoryExactly = weatherService.forecastWeatherForLocationAndDayBasedOnHistoryExactly(weather);
            if (resultBasedOnHistoryExactly != null) {
                resultExpected = resultBasedOnHistoryExactly;
                verify(weatherRepository, never()).findAllByLocation(weather.getLocation());
            } else {

                var dateOfForecast = weather.getDay().minusDays(numberOfDaysOfDeviation);
                var dateBoundSince = dateOfForecast.minusDays(numberOfDaysOfDeviation);
                var dateBoundUntil = dateOfForecast.plusDays(numberOfDaysOfDeviation);

                var weatherDtoEntities = weatherEntities.stream()
                        .filter(w -> w.getLocation().equals(weather.getLocation()))
                        .filter(w -> weatherService.checkWhetherDateIsInRange(w.getDay(), dateOfForecast, dateBoundSince, dateBoundUntil))
                        .map(WeatherForDay::toDto)
                        .toList();

                resultExpected = weatherService.getWeatherForDayDtoAsAverageByTemperature(weatherDtoEntities);
                verify(weatherRepository, times(1)).findAllByLocation(weather.getLocation());
            }

            assertEquals(resultExpected, result);
            verify(weatherRepository, times(2)).findAllByLocationAndDay(weather.getLocation(), weather.getDay());
        }
    }

    @Test
    void testForecastWeatherForLocationAndDayBasedOnHistoryExactly() {

        for (var weather : weatherDtoEntities) {

            var result = weatherService.forecastWeatherForLocationAndDayBasedOnHistoryExactly(weather);
            var resultWeatherEntitiesExpected = weatherEntities.stream()
                    .filter(w -> w.getDay().equals(weather.getDay())
                        && w.getLocation().equals(weather.getLocation()))
                    .map(WeatherForDay::toDto)
                    .toList();

            if (resultWeatherEntitiesExpected.isEmpty()) {
                assertNull(result);
            } else {
                var resultExpected = weatherService.getWeatherForDayDtoAsAverageByTemperature(resultWeatherEntitiesExpected);
                assertEquals(resultExpected, result);
            }
            verify(weatherRepository, times(1)).findAllByLocationAndDay(weather.getLocation(), weather.getDay());
        }
    }

    @Test
    void testForecastWeatherForLocationAndDayBasedOnPreviousDays() {

        for (var weather : weatherDtoEntities) {

            var result = weatherService.forecastWeatherForLocationAndDayBasedOnPreviousDays(weather);

            var dateSince = weather.getDay().minusDays(numberOfDaysOfDeviation);

            var weatherDtoEntities = weatherEntities.stream()
                    .filter(w -> w.getLocation().equals(weather.getLocation()))
                    .filter(w -> w.getDay().isAfter(dateSince)
                            && w.getDay().isBefore(weather.getDay()))
                    .map(WeatherForDay::toDto)
                    .toList();

            var resultExpected = weatherService.getWeatherForDayDtoAsAverageByTemperature(weatherDtoEntities);

            assertEquals(resultExpected, result);
            verify(weatherRepository, atLeastOnce()).findAllByLocation(weather.getLocation());
        }
    }

    @Test
    void testGetWeatherForDayDtoAsAverageByTemperature() {

        var sumTemperature = 0.0;
        for (var weather : weatherEntities) {
            sumTemperature += weather.getTemperatureCelsius();
        }
        var avrTemperature = sumTemperature / weatherEntities.size();
        var resultExpected = new WeatherForDayDto(weatherEntities.get(0), avrTemperature);

        var result = weatherService.getWeatherForDayDtoAsAverageByTemperature(weatherEntities.stream().map(WeatherForDay::toDto).toList());
        assertEquals(resultExpected, result);
    }

    @Test
    void testGetWeatherForDayDtoAsAverageByTemperature_Null() {
        var result = weatherService.getWeatherForDayDtoAsAverageByTemperature(Collections.emptyList());
        assertNull(result);
    }

    @Test
    void testGetAverageTemperature() {

        var sumTemperature = 0.0;
        for (var weather : weatherEntities) {
            sumTemperature += weather.getTemperatureCelsius();
        }
        var avrTemperature = sumTemperature / weatherEntities.size();

        var result = weatherService.getAverageTemperature(weatherEntities.stream().map(WeatherForDay::toDto).toList());
        assertTrue(result.isPresent());
        assertEquals(avrTemperature, result.getAsDouble());
    }

    @Test
    void testGetAverageTemperature_Null() {
        var result = weatherService.getAverageTemperature(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void testCheckWhetherDateIsInRange() {

        for(int numberOfDaysOfDeviation = 1; numberOfDaysOfDeviation <= 30; numberOfDaysOfDeviation++) {

            var dateOfForecast = LocalDate.of(2024, 1, 17);
            var dateBoundSince = dateOfForecast.minusDays(numberOfDaysOfDeviation);
            var dateBoundUntil = dateOfForecast.plusDays(numberOfDaysOfDeviation);

            var weatherEntitiesFirstDate = dateOfForecast.minusDays(1825);

            for (int i = 0; i < 3650; i++) {

                var dateToCheck = weatherEntitiesFirstDate.plusDays(i);

                var result = weatherService.checkWhetherDateIsInRange(dateToCheck, dateOfForecast, dateBoundSince, dateBoundUntil);
                var resultExpected = false;

                var dateBoundSinceForDateToCheck = dateToCheck.minusDays(numberOfDaysOfDeviation);

                for (int j = 0; j <= numberOfDaysOfDeviation * 2; j++) {
                    var date = dateBoundSinceForDateToCheck.plusDays(j);
                    if (date.getMonth() == dateOfForecast.getMonth()
                            && date.getDayOfMonth() == dateOfForecast.getDayOfMonth()) {
                        resultExpected = true;
                        break;
                    }
                }

                assertEquals(resultExpected, result);
            }
        }
    }
}