package org.example.datarest.model;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "day", "location" })
@ToString
public class WeatherForDayDto implements HasLocationAndDay {

    private LocalDate day;
    private Double temperatureCelsius;
    private Location location;

    public WeatherForDayDto(LocalDate day, Location location) {
        this.day = day;
        this.location = location;
    }

    public WeatherForDayDto(@NonNull WeatherForDay weather) {
        this(weather, weather.getTemperatureCelsius());
    }

    public WeatherForDayDto(@NonNull HasLocationAndDay weather, double temperatureCelsius) {
        this.day = weather.getDay();
        this.location = weather.getLocation();
        this.temperatureCelsius = temperatureCelsius;
    }
}
