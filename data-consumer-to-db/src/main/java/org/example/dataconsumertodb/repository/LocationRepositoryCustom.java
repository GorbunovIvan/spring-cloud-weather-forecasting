package org.example.dataconsumertodb.repository;

import org.example.dataconsumertodb.model.Location;

import java.util.List;

public interface LocationRepositoryCustom {
    List<Location> findOrSave(List<Location> locations);
    List<Location> findExisting(List<Location> locations);
}
