package org.workswap.listing.dto;

import java.time.LocalDateTime;

import org.workswap.listing.enums.ListingPublicType;
import org.workswap.listing.enums.ListingType;
import org.workswap.listing.enums.PriceType;

public record ListingDTO(
    Long id,
    Long authorId,
    String localizedTitle,
    String localizedDescription,
    double price,
    PriceType priceType,
    ListingType type,
    ListingPublicType publicType,
    String category,
    Long categoryId,
    String location,
    Long locationId,
    double rating,
    int views,
    LocalDateTime publishedAt,
    boolean active,
    String imagePath,
    boolean testmode,
    boolean temporary
) {}