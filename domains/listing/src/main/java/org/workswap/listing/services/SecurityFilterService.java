package org.workswap.listing.services;

import org.salavion.security.dto.UserAuthData;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.repository.ListingRepository;
import org.workswap.listing.exceptions.ListingAccessDeniedException;

import lombok.RequiredArgsConstructor;

@Service
@Profile("production")
@RequiredArgsConstructor
public class SecurityFilterService {

    private final ListingRepository listingRepository;
    
    // Фильтр который проверяет, есть ли доступ у человека к этому объявлению (на обновление, более жёсткий)
    public void listingUpdateFilter(UserAuthData authData, Long listingId) {
        // Вся логика проверок в одном месте

        if (!listingAuthorFilter(authData, listingId)) {
            throw new ListingAccessDeniedException();
        }
    }

    // Фильтр который проверяет, есть ли доступ у человека доступ к полной информации объявления
    public boolean listingGetFilter(UserAuthData authData, Listing listing, String token) {

        boolean isAuthor = listingAuthorFilter(authData, listing.getId());

        boolean isOpen = listing.isActive() && !listing.isTemporary() && !listing.isTestMode();

        boolean hasAccessToken = false;
        String accessToken = listing.getAccessToken();

        if (accessToken != null && token != null) {
            hasAccessToken = accessToken.equals(token);
        }

        if (isOpen || isAuthor || hasAccessToken) {
            return true;
        }

        return false;
    }

    public boolean listingAuthorFilter(UserAuthData authData, Long listingId) {
        return listingRepository.existsByIdAndAuthorId(listingId, authData.id());
    }
}
