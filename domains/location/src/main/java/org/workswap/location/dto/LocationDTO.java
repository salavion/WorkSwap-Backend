package org.workswap.location.dto;

public record LocationDTO (
    Long id,
    boolean city,
    Long countryId,
    String fullName,
    String name
) {}
