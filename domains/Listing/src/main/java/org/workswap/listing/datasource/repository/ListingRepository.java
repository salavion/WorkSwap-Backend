package org.workswap.listing.datasource.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.dto.ShortListingDTO;

public interface ListingRepository extends JpaRepository<Listing, Long>, ListingRepositoryCustom {
    @Query("""
        SELECT DISTINCT l FROM Listing l
        LEFT JOIN FETCH l.translations t
        LEFT JOIN FETCH l.location loc
        LEFT JOIN FETCH l.serviceSettings ss
        LEFT JOIN FETCH ss.category ssc
        LEFT JOIN FETCH l.productSettings ps
        LEFT JOIN FETCH ps.category psc
        LEFT JOIN FETCH l.eventSettings es
        WHERE l.author.id = :authorId
    """)
    List<Listing> findByAuthorIdWithAllDetails(@Param("authorId") Long authorId);

    List<Listing> findByAuthorId(@Param("authorId") Long authorId);

    Listing findByAccessToken(String accessToken);

    List<Listing> findByAuthorIdAndActiveTrue(Long authorId);

    List<Listing> findByAuthorIdAndTemporary(Long authorId, boolean temporary);

    List<Listing> findByAuthorIdAndTemporaryAndActive(Long authorId, boolean temporary, boolean active);

    Page<Listing> findAllByTemporaryFalseOrderByCreatedAtDesc(Pageable pageable);

    @Query(value = "select count(*) from favorite_listing where listing_id = :listingId", nativeQuery = true)
    int countFavoritesByListingId(@Param("listingId") Long listingId);
    int countByTemporary(boolean temporary);

    List<Listing> findByCreatedAtBefore(LocalDateTime date);

    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM Listing l
        JOIN l.eventSettings es
        JOIN es.participants p
        WHERE l.id = :listingId AND p.id = :authorId
    """)
    boolean existsParticipant(@Param("listingId") Long listingId, @Param("authorId") Long authorId);
    boolean existsByIdAndAuthorId(Long listingId, Long userId);

    @Query("SELECT l.author.id FROM Listing l WHERE l.id = :listingId")
    Long findAuthorIdByListingId(@Param("listingId") Long listingId);   

    @Query("""
        SELECT new org.workswap.listing.dto.ShortListingDTO(
            l.id,

            COALESCE(tLang.title, MIN(tAny.title)),
            COALESCE(tLang.description, MIN(tAny.description)),

            l.price,
            l.priceType,
            l.type,

            CASE
                WHEN loc.city = false THEN loc.name
                ELSE CONCAT(country.name, ', ', loc.name)
            END,

            l.rating,
            l.imagePath,
            l.publishedAt,

            COUNT(DISTINCT u.id),

            CASE WHEN SUM(CASE WHEN u2.id = :userId THEN 1 ELSE 0 END) > 0
                THEN true ELSE false END
        )
        FROM Listing l

        LEFT JOIN l.favoredByUsers u
        LEFT JOIN l.favoredByUsers u2

        LEFT JOIN l.translations tLang
            ON tLang.language = :lang
        LEFT JOIN l.translations tAny

        LEFT JOIN l.location loc
        LEFT JOIN loc.country country

        WHERE l.id IN :listingIds

        GROUP BY
            l.id,
            tLang.title,
            tLang.description,
            l.price,
            l.priceType,
            l.type,
            loc.city,
            loc.name,
            country.name,
            l.rating,
            l.imagePath,
            l.publishedAt
    """)
    List<ShortListingDTO> findShortListingsByIds(
        @Param("listingIds") List<Long> listingIds,
        @Param("userId") Long userId,
        @Param("lang") String lang
    );

    @Query("""
        SELECT new org.workswap.listing.dto.ShortListingDTO(
            l.id,

            COALESCE(tLang.title, MIN(tAny.title)),
            COALESCE(tLang.description, MIN(tAny.description)),

            l.price,
            l.priceType,
            l.type,

            CASE
                WHEN loc.city = false THEN loc.name
                ELSE CONCAT(country.name, ', ', loc.name)
            END,

            l.rating,
            l.imagePath,
            l.publishedAt,

            COUNT(DISTINCT uAll.id),

            true
        )
        FROM Listing l

        JOIN l.favoredByUsers uMe

        LEFT JOIN l.favoredByUsers uAll

        LEFT JOIN l.translations tLang
            ON tLang.language = :lang
        LEFT JOIN l.translations tAny

        LEFT JOIN l.location loc
        LEFT JOIN loc.country country

        WHERE uMe.id = :userId

        GROUP BY
            l.id,
            tLang.title,
            tLang.description,
            l.price,
            l.priceType,
            l.type,
            loc.city,
            loc.name,
            country.name,
            l.rating,
            l.imagePath,
            l.publishedAt
    """)
    List<ShortListingDTO> findLikedListings(
        @Param("userId") Long userId,
        @Param("lang") String lang
    );

    @Query("""
        SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END
        FROM Listing l JOIN l.favoredByUsers u
        WHERE l.id = :listingId AND u.id = :userId
        """)
    boolean existsFavoriteListing(
            @Param("userId") Long userId,
            @Param("listingId") Long listingId
    );

    @Modifying
    @Transactional
    @Query(value = "INSERT IGNORE INTO favorite_listing(user_id, listing_id) VALUES (:userId, :listingId)", nativeQuery = true)
    void addFavoriteListing(@Param("userId") Long userId, @Param("listingId") Long listingId);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM favorite_listing WHERE user_id = :userId AND listing_id = :listingId", nativeQuery = true)
    void removeFavoriteListing(@Param("userId") Long userId, @Param("listingId") Long listingId);

    @Modifying
    @Transactional
    @Query("""
        update Listing l
        set l.imagePath = :imagePath
        where l.id = :id
    """)
    void updateImagePath(@Param("id") Long id, @Param("imagePath") String imagePath);

    @Modifying
    @Transactional
    @Query("""
        update Listing l
        set l.imagePath = :imagePath
        where l.id = :id
        and (l.imagePath is null or l.imagePath = '')
    """)
    void setImagePathIfEmpty(@Param("id") Long id,
                            @Param("imagePath") String imagePath);
}
