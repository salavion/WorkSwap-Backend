package org.workswap.statistic.dto;

public record ListingViewDTO(
    Long userId,
    Long listingId,
    boolean temporary
) {}