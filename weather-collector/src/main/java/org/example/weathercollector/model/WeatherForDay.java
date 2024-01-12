package org.example.weathercollector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "day", "location" })
@ToString
public class WeatherForDay {

    private LocalDate day;
    private Location location;
    private Double temperatureCelsius;

    @JsonProperty("current")
    public void processNodeCurrent(JsonNode nodeCurrent) {
        if (nodeCurrent == null || !nodeCurrent.has("temp_c")) {
            return;
        }
        setTemperatureCelsius(nodeCurrent.get("temp_c").asDouble());
    }
}
