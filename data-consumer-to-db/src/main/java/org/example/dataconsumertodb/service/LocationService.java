package org.example.dataconsumertodb.service;

import lombok.RequiredArgsConstructor;
import org.example.dataconsumertodb.model.Location;
import org.example.dataconsumertodb.repository.LocationRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public Location findOrSave(Location location) {
        var locationFoundInDBOpt = locationRepository.findOne(Example.of(location));
        return locationFoundInDBOpt.orElseGet(() -> create(location));
    }

    public List<Location> findOrSave(List<Location> locations) {
        return locationRepository.findOrSave(locations);
    }

    protected Location create(Location location) {
        return locationRepository.save(location);
    }
}
