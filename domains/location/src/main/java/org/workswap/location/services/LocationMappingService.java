package org.workswap.location.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.location.dto.LocationDTO;
import org.workswap.location.datasource.model.Location;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"production", "statistic"})
public class LocationMappingService{
    
    public LocationDTO toDTO(Location location) {
        return new LocationDTO(
            location.getId(),
            location.isCity(),
            location.isCity() == true ? location.getCountry().getId() : null,
            location.getFullName(),
            location.getName()
        );
    }
}
