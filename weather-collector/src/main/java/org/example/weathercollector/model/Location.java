package org.example.weathercollector.model;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class Location {

    String name;
    String region;
    String country;

    public Location(String name) {
        this.name = name;
    }
}
