package org.example.weathercollector.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.weathercollector.model.Location;
import org.example.weathercollector.model.WeatherForDay;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class WeatherParser {

    private final WeatherPublisher weatherPublisher;

    @Value("${parser.URL}")
    private String URLToAPI;

    @Value("${parser.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRateString = "${parser.scheduled.fixedRate.milliseconds}")
    public void parseAndPublish() {
        var weathers = parse();
        weatherPublisher.publish(weathers);
    }

    protected List<WeatherForDay> parse() {

        List<Location> locations = getLocations();

        var weatherEntities = new ArrayList<WeatherForDay>();

        for (var location : locations) {
            var weather = parseWeatherForDayByLocation(location);
            if (weather != null) {
                weatherEntities.add(weather);
            }
        }

        return weatherEntities;
    }

    protected WeatherForDay parseWeatherForDayByLocation(Location location) {

        var URL = getURLToParseFrom(location.getName());

        ResponseEntity<WeatherForDay> response = restTemplate.getForEntity(URL, WeatherForDay.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            var weather = response.getBody();
            if (weather != null) {
                weather.setDay(LocalDate.now());
                log.info("Parsed info for '" + location.getName() + "' successfully.");
                return weather;
            } else {
                var message = "The request to '" + URL + "' returned empty body (status code was: " + response.getStatusCode() + ")";
                log.error(message + ".\n" + response.getBody());
            }
        } else {
            var message = "Could not make a request to '" + URL + "', error status is: " + response.getStatusCode();
            log.error(message + ".\n" + response.getBody());
        }

        return null;
    }

    protected String getURLToParseFrom(String location) {
        return String.format("%s?key=%s&q=%s&aqi=no",
                URLToAPI,
                apiKey,
                location);
    }

    protected List<Location> getLocations() {
        List<Location> locations;
        var fileName = "locations.json";
        try {
            var resource = new ClassPathResource(fileName);
            locations = objectMapper.readValue(resource.getInputStream(), new TypeReference<>() {});
        } catch (IOException e) {
            log.error("Could not load locations from file '" + fileName + "'");
            locations = Collections.emptyList();
        }
        return locations;
    }
}
