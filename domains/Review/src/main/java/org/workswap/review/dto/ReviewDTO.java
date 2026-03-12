package org.workswap.review.dto;

import java.time.LocalDateTime;

public record ReviewDTO(
    Long id,

    String text,
    double rating,

    Long authorId,
    Long profileId,
    Long listingId,

    LocalDateTime createdAt
) {}
