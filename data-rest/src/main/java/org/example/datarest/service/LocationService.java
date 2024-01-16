package org.example.datarest.service;

import lombok.RequiredArgsConstructor;
import org.example.datarest.model.Location;
import org.example.datarest.repository.LocationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public Location getByName(String name) {
        return locationRepository.findByNameIgnoreCase(name)
                .orElse(null);
    }
}
