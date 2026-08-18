package org.workswap.review.datasource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.workswap.review.datasource.model.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // Пример кастомного метода:
    List<Review> findByListingIdOrderByCreatedAtDesc(Long listingId);
    List<Review> findByProfileIdOrderByCreatedAtDesc(Long profileId);

    boolean existsByAuthorIdAndListingId(Long authorId, Long listingId);
    boolean existsByAuthorIdAndProfileId(Long authorId, Long profileId);

    @Query("SELECT r FROM Review r JOIN FETCH r.author WHERE r.listing.id = :listingId")
    List<Review> findByListingIdWithAuthors(@Param("listingId") Long listingId);

    @Query("SELECT r FROM Review r JOIN FETCH r.author WHERE r.profile.id = :profileId")
    List<Review> findByProfileIdWithAuthors(@Param("profileId") Long profileId);

    void deleteAllByListingId(Long listingId);
    void deleteAllByAuthorId(Long userId);
}