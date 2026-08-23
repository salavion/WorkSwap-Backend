package org.workswap.location.controllers;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.location.datasource.repository.LocationRepository;
import org.workswap.location.dto.LocationDTO;
import org.workswap.location.services.LocationCommandService;
import org.workswap.location.services.LocationQueryService;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Profile("server")
@RequestMapping("/location")
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
    public LocationDTO getLocation(@PathVariable Long locationId) {
        return locationQueryService.getLocation(locationId);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_LOCATION')")
    public Long createLocation(
        @RequestBody LocationDTO location
    ) {
        return locationCommandService.createLocation(location).getId();
    }

    @DeleteMapping("/{locationId}")
    @PreAuthorize("hasAuthority('DELETE_LOCATION')")
    public void deleteLocation(@PathVariable Long locationId) {
        locationRepository.deleteById(locationId);
    }
}