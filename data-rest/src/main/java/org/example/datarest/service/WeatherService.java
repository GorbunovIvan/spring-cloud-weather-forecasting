package org.example.datarest.service;

import lombok.RequiredArgsConstructor;
import org.example.datarest.model.HasLocationAndDay;
import org.example.datarest.model.WeatherForDay;
import org.example.datarest.model.WeatherForDayDto;
import org.example.datarest.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherRepository weatherRepository;

    @Value("${forecast.rangeOfDaysOfDeviation}")
    private int numberOfDaysOfDeviation;

    public WeatherForDayDto forecastWeatherForLocationAndDay(@NonNull HasLocationAndDay weather) {

        var forecasts = new ArrayList<WeatherForDayDto>();
        forecasts.add(forecastWeatherForLocationAndDayBasedOnHistory(weather));
        forecasts.add(forecastWeatherForLocationAndDayBasedOnPreviousDays(weather));

        var result = getWeatherForDayDtoAsAverageByTemperature(forecasts);
        if (result == null) {
            return null;
        }

        result.setDay(weather.getDay());

        return result;
    }

    protected WeatherForDayDto forecastWeatherForLocationAndDayBasedOnHistory(@NonNull HasLocationAndDay weather) {

        var weatherFound = forecastWeatherForLocationAndDayBasedOnHistoryExactly(weather);
        if (weatherFound != null) {
            return weatherFound;
        }

        var weatherEntities = weatherRepository.findAllByLocation(weather.getLocation());
        if (weatherEntities.isEmpty()) {
            return null;
        }

        var dateOfForecast = weather.getDay().minusDays(numberOfDaysOfDeviation);
        var dateBoundSince = dateOfForecast.minusDays(numberOfDaysOfDeviation);
        var dateBoundUntil = dateOfForecast.plusDays(numberOfDaysOfDeviation);

        var weatherEntitiesInPeriods = new ArrayList<WeatherForDayDto>();

        for (var weatherItem : weatherEntities) {
            var weatherDate = weatherItem.getDay();
            if (checkWhetherDateIsInRange(weatherDate, dateOfForecast, dateBoundSince, dateBoundUntil)) {
                weatherEntitiesInPeriods.add(weatherItem.toDto());
            }
        }

        return getWeatherForDayDtoAsAverageByTemperature(weatherEntitiesInPeriods);
    }

    protected WeatherForDayDto forecastWeatherForLocationAndDayBasedOnHistoryExactly(@NonNull HasLocationAndDay weather) {

        var weatherEntities = weatherRepository.findAllByLocationAndDay(weather.getLocation(), weather.getDay())
                .stream()
                .map(WeatherForDay::toDto)
                .toList();

        return getWeatherForDayDtoAsAverageByTemperature(weatherEntities);
    }

    protected WeatherForDayDto forecastWeatherForLocationAndDayBasedOnPreviousDays(@NonNull HasLocationAndDay weather) {

        var weatherEntities = weatherRepository.findAllByLocation(weather.getLocation());
        if (weatherEntities.isEmpty()) {
            return null;
        }

        var dateSince = weather.getDay().minusDays(numberOfDaysOfDeviation);

        var weatherDtoEntities = weatherEntities.stream()
                .filter(w -> w.getDay().isAfter(dateSince)
                            && w.getDay().isBefore(weather.getDay()))
                .map(WeatherForDay::toDto)
                .toList();

        return getWeatherForDayDtoAsAverageByTemperature(weatherDtoEntities);
    }

    protected WeatherForDayDto getWeatherForDayDtoAsAverageByTemperature(List<WeatherForDayDto> weatherEntities) {
        if (weatherEntities.isEmpty()) {
            return null;
        }
        var temperatureCelsiusOpt = getAverageTemperature(weatherEntities);
        if (temperatureCelsiusOpt.isEmpty()) {
            return null;
        }
        return new WeatherForDayDto(weatherEntities.get(0), temperatureCelsiusOpt.getAsDouble());
    }

    protected OptionalDouble getAverageTemperature(List<WeatherForDayDto> weatherEntities) {
        return weatherEntities.stream()
                .filter(Objects::nonNull)
                .mapToDouble(WeatherForDayDto::getTemperatureCelsius)
                .filter(Objects::nonNull)
                .average();
    }

    protected boolean checkWhetherDateIsInRange(LocalDate dateToCheck, LocalDate dateTargetInRange,
                         LocalDate dateBoundSince, LocalDate dateBoundUntil) {

        if (dateTargetInRange.getMonth() != dateBoundSince.getMonth()
                && dateTargetInRange.getMonth() != dateBoundUntil.getMonth()
                && dateTargetInRange.getMonth() == dateToCheck.getMonth()) {
            return true;
        }
        if (dateToCheck.getMonth() != dateBoundSince.getMonth()
                && dateToCheck.getMonth() != dateBoundUntil.getMonth()) {
            return false;
        }
        if (dateToCheck.getMonth() == dateBoundSince.getMonth()) {
            if (dateToCheck.getDayOfMonth() < dateBoundSince.getDayOfMonth()) {
                return false;
            }
        }
        if (dateToCheck.getMonth() == dateBoundUntil.getMonth()) {
            if (dateToCheck.getDayOfMonth() > dateBoundUntil.getDayOfMonth()) {
                return false;
            }
        }

        return true;
    }
}
