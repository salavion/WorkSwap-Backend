package org.workswap.listing.dto;

import java.time.LocalDateTime;

import org.workswap.listing.enums.EventStatus;
import org.workswap.listing.enums.RecurrencePattern;

public record EventSettingsDTO (
    LocalDateTime eventDate,
    LocalDateTime registrationCloseTime,
    boolean recurring,
    RecurrencePattern recurrencePattern,
    EventStatus eventStatus,
    boolean isPublic,
    Integer maxParticipants,
    Integer minParticipants
) {}
