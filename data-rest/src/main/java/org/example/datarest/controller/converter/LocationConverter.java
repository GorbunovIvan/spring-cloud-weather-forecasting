package org.example.datarest.controller.converter;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.datarest.model.Location;
import org.example.datarest.service.LocationService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class LocationConverter implements Converter<String, Location> {

    private final LocationService locationService;

    @Override
    public Location convert(@NonNull String source) {
        var location = locationService.getByName(source);
        if (location == null) {
            log.warn("Location '" + source + "' was not found");
        }
        return location;
    }
}
