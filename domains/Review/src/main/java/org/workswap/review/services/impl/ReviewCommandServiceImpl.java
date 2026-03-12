package org.workswap.review.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.review.datasource.model.Review;
import org.workswap.review.datasource.repository.ReviewRepository;
import org.workswap.review.dto.ReviewDTO;
import org.workswap.review.services.ReviewCommandService;
import org.workswap.review.services.ReviewMappingService;
import org.workswap.user.datasource.model.User;
import org.salavion.security.dto.UserAuthData;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"production"})
public class ReviewCommandServiceImpl implements ReviewCommandService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewCommandService.class);

    /* private final ReviewProducer reviewProducer; */
    private final ReviewMappingService reviewMappingService;
    private final EntityManager entityManager;

    private final ReviewRepository reviewRepository;

    public void createReview(UserAuthData authData, Long profileId, Long listingId, Double rating, String text) {

        boolean alreadyReviewed = true;
        Listing listing = null;

        logger.debug("profileId {}", profileId);
        logger.debug("listingId {}", listingId);

        if (listingId != null) {
            alreadyReviewed = reviewRepository.existsByAuthorIdAndListingId(authData.id(), listingId);
            listing = entityManager.getReference(Listing.class, listingId);
        } else if (profileId != null) {
            alreadyReviewed = reviewRepository.existsByAuthorIdAndProfileId(authData.id(), profileId);
        } else {
            throw new IllegalStateException("Was no listing or profile");
        }

        if (rating == null) throw new IllegalStateException("Рейтинг не может быть нулевой");
        if (authData.id() == profileId) throw new IllegalStateException("Нельзя оставлять отзыв самому себе");
        if (alreadyReviewed) throw new IllegalStateException("Такой отзыв уже остален");

        User author = entityManager.getReference(User.class, authData.id());
        User profile = entityManager.getReference(User.class, profileId);
        Review review = reviewRepository.save(new Review(text, rating, author, listing, profile));

        logger.debug("Отзыв сохранён");

        ReviewDTO dto = reviewMappingService.toDTO(review);
        /* reviewProducer.reviewCreated(dto); */

        //TODO переписать создание отзывов на ивенты
    }
}
