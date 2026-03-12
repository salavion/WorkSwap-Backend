package org.workswap.location.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.location.datasource.model.Location;
import org.workswap.location.datasource.repository.LocationRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile("production")
public class LocationCommandService {

    private final LocationRepository locationRepository;
    
    public Location createLocation(Long countryId, String name) {

        Location country;
        Location newLocation;

        if (countryId != null) {
            country = locationRepository.findById(countryId).orElseThrow(
                () -> new EntityNotFoundException("Локация не найдена"));

            newLocation = new Location(name, true, country);
        } else {
            newLocation = new Location(name, false, null);
        }

        return locationRepository.save(newLocation);
    }
}
