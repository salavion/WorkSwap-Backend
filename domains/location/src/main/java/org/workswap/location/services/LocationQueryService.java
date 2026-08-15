package org.workswap.location.services;

import java.util.List;

import org.springframework.lang.NonNull;
import org.workswap.location.datasource.model.Location;
import org.workswap.location.dto.LocationDTO;

public interface LocationQueryService {

    List<LocationDTO> getAllLocations();
    List<Location> getAllDescendants(Location location);
    List<LocationDTO> getCities(Long coutryId);
    List<LocationDTO> getCountries();
    LocationDTO getLocation(@NonNull Long locationId);
}
