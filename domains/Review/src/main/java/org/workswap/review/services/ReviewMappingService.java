package org.workswap.review.services;

import org.workswap.review.datasource.model.Review;
import org.workswap.review.dto.ReviewDTO;

public interface ReviewMappingService {
    
    ReviewDTO toDTO(Review review);
}
