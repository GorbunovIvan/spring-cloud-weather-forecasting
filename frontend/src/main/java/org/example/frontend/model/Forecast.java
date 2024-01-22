package org.example.frontend.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "day", "location" })
@ToString
public class Forecast {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate day;

    private Location location;
    private Double temperatureCelsius;

    public Forecast(LocalDate day, String locationName) {
        this.day = day;
        this.location = new Location(locationName);
    }
}
