package org.workswap.listing.dto;

import java.util.List;

public record CatalogRequest(
    int totalPages,
    List<ShortListingDTO> listings
) {}