package org.example.datarest.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "locations",
        uniqueConstraints = @UniqueConstraint(columnNames = { "name", "region", "country" }))
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "region")
    private String region;

    @Column(name = "country")
    private String country;

    public Location(String name) {
        this.name = name;
    }
}
