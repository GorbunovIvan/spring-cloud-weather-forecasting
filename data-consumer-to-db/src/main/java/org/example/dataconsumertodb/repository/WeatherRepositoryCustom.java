package org.example.dataconsumertodb.repository;

import org.example.dataconsumertodb.model.WeatherForDay;

import java.util.List;

public interface WeatherRepositoryCustom {
    List<WeatherForDay> findExisting(List<WeatherForDay> weatherEntities);
}
