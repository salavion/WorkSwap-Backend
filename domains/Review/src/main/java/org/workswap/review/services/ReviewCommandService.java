package org.workswap.review.services;

import org.salavion.security.dto.UserAuthData;

public interface ReviewCommandService {

    void createReview(UserAuthData authData, Long profileId, Long listingId, Double rating, String text);
}
