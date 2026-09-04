package org.workswap.listing.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.workswap.listing.enums.ListingPublicType;
import org.workswap.listing.enums.ListingType;
import org.workswap.listing.enums.PriceType;
import org.workswap.user.dto.ShortUserProfileDTO;

public class ListingDTO {

    public record Full (
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
        boolean liked,

        Long authorId,
        ListingPublicType publicType,
        String category,
        Long categoryId,
        Long locationId,
        Long mainImageId,
        String accessToken,
        int views,
        boolean active,
        boolean testmode,
        boolean temporary
    ) {}

    public record Update(
        double price,
        PriceType priceType,
        Long locationId,
        Long categoryId,
        Long mainImageId,
        String accessToken,
        boolean active,
        boolean testMode
    ) {
    }

    public record Page(
        ListingDTO.Full listing,
        ShortUserProfileDTO author,
        List<ImageDTO> images
    ) {}
}