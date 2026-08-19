package org.workswap.location.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.location.datasource.model.Location;
import org.workswap.location.datasource.repository.LocationRepository;
import org.workswap.location.dto.LocationDTO;
import org.workswap.location.services.LocationMappingService;
import org.workswap.location.services.LocationQueryService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"production", "statistic"})
public class LocationQueryServiceImpl implements LocationQueryService {
    
    private final LocationRepository locationRepository;
    private final LocationMappingService locationMappingService;

    public List<LocationDTO> getAllLocations() {
        return locationRepository.findAll()
                .stream()
                .map(loc -> locationMappingService.toDTO(loc))
                .collect(Collectors.toList());
    }

    public List<Location> getAllDescendants(Location location) {
        List<Location> descendants = new ArrayList<>();
        descendants.add(location);
        if (!location.isCity()) {
            List<Location> cities = locationRepository.findByCountry(location);
            for (Location child : cities) {
                descendants.add(child);
            }
        }
        return descendants;
    }

    public List<LocationDTO> getCountries() {
        return locationRepository.findByCity(false)
                                .stream()
                                .map(loc -> locationMappingService.toDTO(loc))
                                .collect(Collectors.toList());
    }

    public List<LocationDTO> getCities(Long coutryId) {
        return locationRepository.findByCountryId(coutryId)
                                .stream()
                                .map(loc -> locationMappingService.toDTO(loc))
                                .collect(Collectors.toList());
    }

    public LocationDTO getLocation(Long locationId) {
        return locationMappingService.toDTO(
            locationRepository.findById(locationId).orElseThrow(
                () -> new EntityNotFoundException("Локация не найдена")
            ));
    }
}
