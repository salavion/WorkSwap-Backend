package org.workswap.review.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.workswap.review.datasource.model.Review;
import org.workswap.review.datasource.repository.ReviewRepository;
import org.workswap.review.dto.MyReviews;
import org.workswap.review.dto.ReviewDTO;
import org.workswap.sso.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"server", "statistic"})
public class ReviewQueryService {

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

    public Page<ReviewDTO> getRewiewsPage(int page, int size, String sortParam) {

        if (sortParam == null || sortParam.length() == 0) sortParam = "createdAt";

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortParam).descending());
        Page<Review> reviews = reviewRepository.findAll(pageable);

        List<ReviewDTO> dtos = reviews.stream().map(r -> reviewMappingService.toDTO(r)).toList();

        return new PageImpl<>(
            dtos != null ? dtos : new ArrayList<>(), 
            pageable, 
            reviews.getTotalElements());
    }

    public MyReviews getMyReviews(UserAuthData authData) {
        List<Review> given = reviewRepository.findByAuthorId(authData.sub());
        List<Review> recived = reviewRepository.findByProfileIdOrderByCreatedAtDesc(authData.sub());

        return new MyReviews(
            given.stream()
                .map(r -> reviewMappingService.toDTO(r))
                .toList(), 
            recived.stream()
                .map(r -> reviewMappingService.toDTO(r))
                .toList()
            );
    }
}
