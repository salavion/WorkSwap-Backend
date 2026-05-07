package org.workswap.listing.dto;

import java.time.LocalDateTime;

import org.workswap.listing.enums.ListingType;
import org.workswap.listing.enums.PriceType;

public record ShortListingDTO(
    Long id,
    String localizedTitle,
    String localizedDescription,
    double price,
    PriceType priceType,
    ListingType type,
    String location,
    double rating,
    String imagePath,
    LocalDateTime publishedAt,
    long likes,
    boolean liked
) {}
