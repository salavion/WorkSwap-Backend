package org.workswap.listing.services;

import java.util.List;
import java.util.Map;

import org.salavion.security.dto.UserAuthData;
import org.springframework.lang.NonNull;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.dto.CatalogFilterDTO;
import org.workswap.listing.dto.CatalogRequest;
import org.workswap.listing.dto.ImageDTO;
import org.workswap.listing.dto.ListingDTO;
import org.workswap.listing.dto.ListingPageRequest;
import org.workswap.listing.dto.ListingTranslationDTO;
import org.workswap.listing.dto.ShortListingDTO;

public interface ListingQueryService {

    @NonNull
    Listing getListingById(Long listingId);

    boolean isFavorite(UserAuthData authData, Long listingId);

    CatalogRequest getSortedCatalog(
        UserAuthData authData, 
        CatalogFilterDTO filters, 
        String locale
    );
    
    List<ListingDTO> getListingDtosByUser(Long userId, String locale);
    List<ListingDTO> getOwnListingsByUser(UserAuthData authData, String locale);
    List<ListingDTO> getRecentListings(int amount, String locale);
    List<ShortListingDTO> getFavorites(UserAuthData authData, String locale);
    List<ListingDTO> getDrafts(UserAuthData authData, String locale);
    Map<String, ListingTranslationDTO> getTranslations(Long id);
    List<ImageDTO> getImages(Long id);

    ListingPageRequest getListingPage(UserAuthData authData, String token, Long id, String locale);
    ShortListingDTO getCatalogListing(Long listingId, UserAuthData authData, String locale);
    ListingDTO getListingDTO(Long listingId, UserAuthData authData, String locale);

    String getListingToken(UserAuthData authData, Long listingId);
}