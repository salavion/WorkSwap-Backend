package org.workswap.listing.datasource.repository;

import java.util.List;

import org.salavion.security.dto.UserAuthData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.workswap.category.datasource.Category;
import org.workswap.listing.dto.ShortListingDTO;
import org.workswap.listing.enums.ListingType;
import org.workswap.listing.enums.ProductType;
import org.workswap.listing.enums.ServiceType;

public interface ListingRepositoryCustom {
    Page<ShortListingDTO> findListings(
        List<? extends Category> categories,
        String locationName,
        String search,
        Boolean requireReviews,
        Boolean translationsFilter,
        List<String> languages,
        ListingType type,
        ServiceType serviceType,
        ProductType productType,
        String sortBy,
        Pageable pageable,
        UserAuthData authData
    );
}
