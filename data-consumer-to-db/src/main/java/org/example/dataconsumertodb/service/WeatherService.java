package org.example.dataconsumertodb.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.dataconsumertodb.model.WeatherForDay;
import org.example.dataconsumertodb.repository.WeatherRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherRepository weatherRepository;
    private final LocationService locationService;

    public WeatherForDay createIfNotExisting(WeatherForDay weatherForDay) {

        var location = locationService.findOrSave(weatherForDay.getLocation());
        weatherForDay.setLocation(location);

        var weatherToFindInDB = new WeatherForDay(null, weatherForDay.getDay(), null, weatherForDay.getLocation());
        var weatherFoundOpt = weatherRepository.findOne(Example.of(weatherToFindInDB));
        if (weatherFoundOpt.isPresent()) {
            return update(weatherFoundOpt.get().getId(), weatherForDay);
        }

        return create(weatherForDay);
    }

    public List<WeatherForDay> createIfNotExisting(List<WeatherForDay> weatherEntities) {

        weatherEntities = weatherEntities.stream()
                .distinct()
                .toList();

        persistLocations(weatherEntities);

        var weatherEntitiesResult = new ArrayList<WeatherForDay>();

        // Updating existing entities
        var weatherEntitiesFound = weatherRepository.findExisting(weatherEntities);

        if (!weatherEntitiesFound.isEmpty()) {

            var weatherEntitiesToUpdate = new ArrayList<WeatherForDay>();

            for (var weatherExisting : weatherEntitiesFound) {
                for (var weather : weatherEntities) {
                    if (weather.equals(weatherExisting)) {
                        weather.setId(weatherExisting.getId());
                        weatherEntitiesToUpdate.add(weather);
                        break;
                    }
                }
            }

            if (weatherEntitiesFound.size() != weatherEntitiesToUpdate.size()) {
                throw new IllegalStateException("Something went wrong with updating existing weather entities");
            }

            var weatherEntitiesUpdated = weatherRepository.saveAll(weatherEntitiesToUpdate);
            weatherEntitiesResult.addAll(weatherEntitiesUpdated);
        }

        // Creating new entities
        var weatherEntitiesNotFound = new ArrayList<>(weatherEntities);
        weatherEntitiesNotFound.removeAll(weatherEntitiesFound);

        if (!weatherEntitiesNotFound.isEmpty()) {
            var weatherEntitiesPersisted = createAll(weatherEntitiesNotFound);
            weatherEntitiesResult.addAll(weatherEntitiesPersisted);
        }

        return weatherEntitiesResult;
    }
    protected WeatherForDay create(WeatherForDay weatherForDay) {
        return weatherRepository.save(weatherForDay);
    }

    protected List<WeatherForDay> createAll(List<WeatherForDay> weathersEntities) {
        return weatherRepository.saveAll(weathersEntities);
    }

    protected WeatherForDay update(Long id, WeatherForDay weatherForDay) {
        if (!weatherRepository.existsById(id)) {
            throw new EntityNotFoundException("Weather with id '" + id + "' was not found");
        }
        weatherForDay.setId(id);
        return weatherRepository.save(weatherForDay);
    }

    private void persistLocations(List<WeatherForDay> weatherEntities) {

        var locations = weatherEntities.stream()
                .map(WeatherForDay::getLocation)
                .distinct()
                .toList();

        locations = locationService.findOrSave(locations);

        for (var location : locations) {
            weatherEntities.stream()
                    .filter(w -> w.getLocation().equals(location))
                    .forEach(w -> w.setLocation(location));
        }
    }
}
