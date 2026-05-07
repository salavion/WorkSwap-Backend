package org.workswap.listing.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.workswap.listing.enums.EventStatus;
import org.workswap.listing.enums.RecurrencePattern;
import org.workswap.user.dto.ShortUserDTO;
import org.workswap.user.dto.ShortUserProfileDTO;

public class EventDTO {

    public record Settings(
        LocalDateTime eventDate,
        LocalDateTime registrationCloseTime,
        boolean recurring,
        RecurrencePattern recurrencePattern,
        EventStatus eventStatus,
        boolean isPublic,
        Integer maxParticipants,
        Integer minParticipants
    ) {
    }

    public record Page (
        ListingDTO.Full listing,
        ShortUserProfileDTO author,
        List<ImageDTO> images,

        Settings event,
        List<ShortUserDTO> participants,
        int participantsCount
    ) {}

    
}