package org.workswap.listing.services;

import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Profile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.dto.ListingTranslationDTO;
import org.salavion.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile("production")
public class ListingFacage {

    private final ListingCommandService listingCommandService;
    
    public Listing create(UserAuthData authData, String type) {
        return listingCommandService.create(authData, type);
    }

    public void delete(UserAuthData authData, Long listingId) {
        listingCommandService.delete(authData, listingId);
    }

    public void publish(UserAuthData authData, Long listingId) {
        listingCommandService.publish(authData, listingId);
    }

    public void addListingToFavorite(UserAuthData authData, Long listingId) {
        listingCommandService.addListingToFavorite(authData, listingId);
    }

    public void removeListingFromFavorite(UserAuthData authData, Long listingId) {
        listingCommandService.removeListingFromFavorite(authData, listingId);
    }

    public void modifyListingParam(UserAuthData authData, Long id, Map<String, Object> updates) throws AccessDeniedException {
        listingCommandService.modifyListingParam(authData, id, updates);
    }

    public Set<String> updateListingTranslations(
            UserAuthData authData,
            Long listingId,
            Map<String, ListingTranslationDTO> translationsMap
    ) {
        return listingCommandService.updateListingTranslations(authData, listingId, translationsMap);
    }
}
