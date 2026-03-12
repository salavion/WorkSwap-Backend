package org.workswap.review.services;

import java.util.List;

import org.workswap.review.datasource.model.Review;
import org.workswap.review.dto.ReviewDTO;

public interface ReviewQueryService {
    
    List<Review> getReviewsByListingId(Long listingId);
    List<Review> getReviewsByProfileId(Long profileId);
    List<ReviewDTO> getRewiewsList(Long listingId, Long profileId);
}
