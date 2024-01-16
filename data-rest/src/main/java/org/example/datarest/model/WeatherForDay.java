package org.example.datarest.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDate;

@Entity
@Table(name = "weather_for_day",
        uniqueConstraints = @UniqueConstraint(columnNames = { "date_recorded", "location_id" }))
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "day", "location" })
@ToString
public class WeatherForDay implements HasLocationAndDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_recorded")
    private LocalDate day;

    @Column(name = "temp_c")
    private Double temperatureCelsius;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "location_id")
    @Fetch(FetchMode.JOIN)
    private Location location;

    public WeatherForDayDto toDto() {
        return new WeatherForDayDto(this);
    }
}
