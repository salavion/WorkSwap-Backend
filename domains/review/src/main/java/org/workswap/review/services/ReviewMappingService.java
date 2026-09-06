package org.workswap.review.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.review.datasource.model.Review;
import org.workswap.review.dto.ReviewDTO;

import lombok.RequiredArgsConstructor;

@Service
@Profile({"server", "statistic"})
@RequiredArgsConstructor
public class ReviewMappingService{
    
    public ReviewDTO toDTO(Review review) {
        return new ReviewDTO(
            review.getId(),
            review.getText(),
            review.getRating(),
            review.getAuthor() != null ? review.getAuthor().getSub() : null,
            review.getProfile() != null ? review.getProfile().getSub() : null,
            review.getListing() != null ? review.getListing().getId() : null,
            review.getCreatedAt()
        );
    }
}
