package org.workswap.review.dto;

import java.time.LocalDateTime;

import org.workswap.review.datasource.model.Review;
import org.workswap.user.dto.ShortUserDTO;

public record ReviewDTO(
    Long id,

    String text,
    double rating,

    ShortUserDTO userDTO,
    String profileSub,
    Long listingId,

    LocalDateTime createdAt
) {
    public static ReviewDTO ofReview(Review review) {
        return new ReviewDTO(
            review.getId(),
            review.getText(),
            review.getRating(),
            ShortUserDTO.ofUser(review.getAuthor()),
            review.getProfile() != null ? review.getProfile().getSub() : null,
            review.getListing() != null ? review.getListing().getId() : null,
            review.getCreatedAt()
        );
    }
}
