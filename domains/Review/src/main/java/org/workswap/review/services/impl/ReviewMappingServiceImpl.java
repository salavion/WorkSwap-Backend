package org.workswap.review.services.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.review.datasource.model.Review;
import org.workswap.review.dto.ReviewDTO;
import org.workswap.review.services.ReviewMappingService;

import lombok.RequiredArgsConstructor;

@Service
@Profile({"production", "statistic"})
@RequiredArgsConstructor
public class ReviewMappingServiceImpl implements ReviewMappingService{
    
    public ReviewDTO toDTO(Review review) {
        return new ReviewDTO(
            review.getId(),
            review.getText(),
            review.getRating(),
            review.getAuthor() != null ? review.getAuthor().getId() : null,
            review.getProfile() != null ? review.getProfile().getId() : null,
            review.getListing() != null ? review.getListing().getId() : null,
            review.getCreatedAt()
        );
    }
}
