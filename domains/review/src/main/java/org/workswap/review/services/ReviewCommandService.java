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
import org.workswap.review.dto.ReviewCreateDTO;
import org.workswap.shared.events.review.ReviewCreatedEvent;
import org.workswap.sso.security.dto.UserAuthData;
import org.workswap.user.datasource.model.User;
import org.workswap.user.datasource.repository.UserRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"server"})
public class ReviewCommandService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewCommandService.class);

    private final EntityManager entityManager;

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void createReview(ReviewCreateDTO reviewDto, UserAuthData authData) {

        boolean alreadyReviewed = true;
        Listing listing = null;

        logger.debug("profileSub {}", reviewDto.profileSub());
        logger.debug("listingId {}", reviewDto.listingId());

        if (reviewDto.listingId() != null) {
            alreadyReviewed = reviewRepository.existsByAuthorSubAndListingId(authData.sub(), reviewDto.listingId());
            listing = entityManager.getReference(Listing.class, reviewDto.listingId());
        } else if (reviewDto.profileSub() != null) {
            alreadyReviewed = reviewRepository.existsByAuthorSubAndProfileSub(authData.sub(), reviewDto.profileSub());
        } else {
            throw new IllegalStateException("Was no listing or profile");
        }

        if (reviewDto.rating() == null) throw new IllegalStateException("Рейтинг не может быть нулевой");
        if (authData.sub() == reviewDto.profileSub()) throw new IllegalStateException("Нельзя оставлять отзыв самому себе");
        if (alreadyReviewed) throw new IllegalStateException("Такой отзыв уже остален");

        User author = userRepository.findBySub(authData.sub()).orElseThrow();
        User profile = userRepository.findBySub(reviewDto.profileSub()).orElseThrow();

        Review review = reviewRepository.save(
            new Review(
                reviewDto.text(), 
                reviewDto.rating(), 
                author, 
                listing, 
                profile
            ));

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

    public void deleteReviewsByListingId(Long listingId) {
        reviewRepository.deleteAllByListingId(listingId);
    }

    public void deleteReviewsByAuthorId(Long userId) {
        reviewRepository.deleteAllByAuthorId(userId);
    }
}
