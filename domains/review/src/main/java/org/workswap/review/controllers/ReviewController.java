package org.workswap.review.controllers;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.review.dto.ReviewDTO;
import org.workswap.review.services.ReviewCommandService;
import org.workswap.review.services.ReviewQueryService;
import org.salavion.security.dto.UserAuthData;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Profile("production")
@RequestMapping("/review")
public class ReviewController {

    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_REVIEW')")
    public void addReview(
        @RequestParam(required = false) Long listingId,
        @RequestParam(required = false) Long profileId,
        @AuthenticationPrincipal UserAuthData authData,
        @RequestParam String text,
        @RequestParam double rating
    ) {
        reviewCommandService.createReview(authData, profileId, listingId, rating, text);
    }

    @GetMapping("/list")
    @PermitAll
    public List<ReviewDTO> getRewiewsByItem(
        @RequestParam(required = false) Long listingId,
        @RequestParam(required = false) Long profileId
    ) {
        return reviewQueryService.getRewiewsList(listingId, profileId);
    }

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('GET_REVIEWS_PAGE')")
    public Page<ReviewDTO> getRewiewsPage(
        @RequestParam int page,
        @RequestParam int size,
        @RequestParam String sortParam
    ) {
        return reviewQueryService.getRewiewsPage(page, size, sortParam);
    }
}
