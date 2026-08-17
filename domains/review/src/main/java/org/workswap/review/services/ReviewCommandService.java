package org.workswap.review.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.review.datasource.model.Review;
import org.workswap.review.datasource.repository.ReviewRepository;
import org.workswap.shared.events.review.ReviewCreatedEvent;
import org.workswap.user.datasource.model.User;
import org.salavion.security.dto.UserAuthData;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"production"})
public class ReviewCommandService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewCommandService.class);

    private final EntityManager entityManager;

    private final ReviewRepository reviewRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
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

        eventPublisher.publishEvent(new ReviewCreatedEvent(
            review.getId(), 
            review.getText(), 
            review.getRating(), 
            review.getAuthor().getId(),
            review.getListing() != null ? review.getListing().getId() : null, 
            review.getProfile() != null ? review.getProfile().getId() : null,
            review.getCreatedAt()
        ));
    }
}
