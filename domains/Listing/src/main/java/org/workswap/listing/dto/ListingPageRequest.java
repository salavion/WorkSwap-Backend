package org.workswap.listing.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.workswap.listing.enums.ListingPublicType;
import org.workswap.listing.enums.ListingType;
import org.workswap.listing.enums.PriceType;
import org.workswap.user.dto.ShortUserProfileDTO;

public record ListingPageRequest (
    Long id,
    String localizedTitle,
    String localizedDescription,
    double price,
    PriceType priceType,
    Long categoryId,
    String location,
    double rating,
    int views,
    LocalDateTime publishedAt,
    String imagePath,
    ListingType type,
    ListingPublicType publicType,

    ShortUserProfileDTO author,
    List<ImageDTO> images
) {}
