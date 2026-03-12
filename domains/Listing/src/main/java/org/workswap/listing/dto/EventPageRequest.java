package org.workswap.listing.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.workswap.listing.enums.EventStatus;
import org.workswap.listing.enums.ListingPublicType;
import org.workswap.listing.enums.ListingType;
import org.workswap.listing.enums.PriceType;
import org.workswap.listing.enums.RecurrencePattern;
import org.workswap.user.dto.ShortUserDTO;
import org.workswap.user.dto.ShortUserProfileDTO;

public record EventPageRequest (
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

    LocalDateTime eventDate,
    LocalDateTime registrationCloseTime,
    boolean recurring,
    RecurrencePattern recurrencePattern,
    EventStatus eventStatus,
    boolean isPublic,
    Integer maxParticipants,
    Integer minParticipants,

    ShortUserProfileDTO author,
    List<ImageDTO> images,
    List<ShortUserDTO> participants,
    int participantsCount
) {}
