package org.workswap.location.controllers;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.location.datasource.repository.LocationRepository;
import org.workswap.location.dto.LocationDTO;
import org.workswap.location.services.LocationCommandService;
import org.workswap.location.services.LocationQueryService;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/locations")
public class LocationController {
    
    private final LocationQueryService locationQueryService;
    private final LocationCommandService locationCommandService;
    private final LocationRepository locationRepository;

    @GetMapping
    @PermitAll
    public List<LocationDTO> getAllLocations() {
        return locationQueryService.getAllLocations();
    }

    @GetMapping("/countries")
    @PermitAll
    public List<LocationDTO> getCountires() {
        return locationQueryService.getCountries();
    }

    @GetMapping("/cities/{coutryId}")
    @PermitAll
    public List<LocationDTO> getCities(@PathVariable Long coutryId) {
        return locationQueryService.getCities(coutryId);
    }

    @GetMapping("/{locationId}/get")
    @PermitAll
    public LocationDTO getLocation(@NonNull @PathVariable Long locationId) {
        return locationQueryService.getLocation(locationId);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('CREATE_LOCATION')")
    public void addLocation(
        @RequestParam(required = false) Long countryId,
        @RequestParam String name
    ) {
        locationCommandService.createLocation(countryId, name);
    }

    @GetMapping("/{locationId}/delete")
    @PreAuthorize("hasAuthority('DELETE_LOCATION')")
    public void deleteLocation(@NonNull @PathVariable Long locationId) {
        locationRepository.deleteById(locationId);
    }
}