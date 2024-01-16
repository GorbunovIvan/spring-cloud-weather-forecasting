package org.example.datarest.controller;

import lombok.RequiredArgsConstructor;
import org.example.datarest.model.Location;
import org.example.datarest.model.WeatherForDayDto;
import org.example.datarest.service.WeatherService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class ForecastController {

    private final WeatherService weatherService;

    @GetMapping("/forecast")
    public ResponseEntity<WeatherForDayDto> forecastByLocationAndDay(@RequestParam(value = "location", required = false) Location location,
                                                                     @RequestParam(value = "day", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {
        if (day == null) {
            day = LocalDate.now();
        }
        if (location == null) {
            throw new IllegalArgumentException("Location not found");
        }
        var weatherDto = new WeatherForDayDto(day, location);
        weatherDto = weatherService.forecastWeatherForLocationAndDay(weatherDto);
        if (weatherDto == null) {
            throw new IllegalArgumentException("Unable to forecast the weather in '" + location.getName() + "' for the day '" + day + "'");
        }
        return ResponseEntity.ok(weatherDto);
    }
}
