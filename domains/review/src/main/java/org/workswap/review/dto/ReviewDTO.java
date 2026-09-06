package org.workswap.review.dto;

import java.time.LocalDateTime;

public record ReviewDTO(
    Long id,

    String text,
    double rating,

    String authorSub,
    String profileSub,
    Long listingId,

    LocalDateTime createdAt
) {}
