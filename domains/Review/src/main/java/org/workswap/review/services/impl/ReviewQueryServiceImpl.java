package org.workswap.review.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.review.datasource.model.Review;
import org.workswap.review.datasource.repository.ReviewRepository;
import org.workswap.review.dto.ReviewDTO;
import org.workswap.review.services.ReviewMappingService;
import org.workswap.review.services.ReviewQueryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"production", "statistic"})
public class ReviewQueryServiceImpl implements ReviewQueryService {

    private final ReviewRepository reviewRepository;
    private final ReviewMappingService reviewMappingService;

    public List<Review> getReviewsByListingId(Long listingId) {
        return reviewRepository.findByListingIdOrderByCreatedAtDesc(listingId); // Получаем отзывы для объявления
    }

    public List<Review> getReviewsByProfileId(Long profileId) {
        return reviewRepository.findByProfileIdOrderByCreatedAtDesc(profileId); // Получаем отзывы для объявления
    }

    public List<ReviewDTO> getRewiewsList(Long listingId, Long profileId) {
        List<Review> reviews = new ArrayList<>();
        if (listingId != null) {
            reviews = getReviewsByListingId(listingId);
        } else if (profileId != null) {
            reviews = getReviewsByProfileId(profileId);
        }

        return reviews.stream()
            .map(r -> reviewMappingService.toDTO(r))
            .toList();
    }
}
