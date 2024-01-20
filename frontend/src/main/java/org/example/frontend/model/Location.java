package org.example.frontend.model;

import lombok.*;

import java.util.Objects;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
public class Location {

    private String name;
    private String region;
    private String country;

    public Location(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return Objects.requireNonNullElse(getName(), "");
    }

    public String toStringFull() {
        return "Location{" +
                "name='" + name + '\'' +
                ", region='" + region + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
