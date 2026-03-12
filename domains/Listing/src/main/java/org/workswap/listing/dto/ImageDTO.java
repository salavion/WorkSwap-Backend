package org.workswap.listing.dto;

public record ImageDTO(
    Long id,
    Long listingId,
    String path
) {}
