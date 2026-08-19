package org.workswap.shared.events.review;

import java.time.LocalDateTime;

public record ReviewCreatedEvent(
    Long id,

    String text,
    double rating,

    Long authorId,
    Long profileId,
    Long listingId,

    LocalDateTime createdAt
) {
}
