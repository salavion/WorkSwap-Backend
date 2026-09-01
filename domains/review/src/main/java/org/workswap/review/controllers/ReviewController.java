package org.workswap.review.controllers;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.review.dto.ReviewDTO;
import org.workswap.review.services.ReviewCommandService;
import org.workswap.review.services.ReviewQueryService;
import org.workswap.sso.security.annotations.controllers.PublicEndpoint;
import org.workswap.sso.security.annotations.controllers.RequiredPermission;
import org.workswap.sso.security.annotations.parameters.AuthUser;
import org.workswap.sso.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Profile("server")
@RequestMapping("/review")
public class ReviewController {

    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;

    @PostMapping
    @RequiredPermission("CREATE_REVIEW")
    public void addReview(
        @RequestParam(required = false) Long listingId,
        @RequestParam(required = false) Long profileId,
        @AuthUser UserAuthData authData,
        @RequestParam String text,
        @RequestParam double rating
    ) {
        reviewCommandService.createReview(authData, profileId, listingId, rating, text);
    }

    @GetMapping("/list")
    @PublicEndpoint
    public List<ReviewDTO> getRewiewsByItem(
        @RequestParam(required = false) Long listingId,
        @RequestParam(required = false) Long profileId
    ) {
        return reviewQueryService.getRewiewsList(listingId, profileId);
    }

    @GetMapping("/page")
    @RequiredPermission("GET_REVIEWS_PAGE")
    public Page<ReviewDTO> getRewiewsPage(
        @RequestParam int page,
        @RequestParam int size,
        @RequestParam String sortParam
    ) {
        return reviewQueryService.getRewiewsPage(page, size, sortParam);
    }
}
