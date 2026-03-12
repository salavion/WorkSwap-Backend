package org.workswap.listing.services.impl;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.model.category.ProductCategory;
import org.workswap.listing.datasource.model.category.ServiceCategory;
import org.workswap.listing.datasource.model.types.EventSettings;
import org.workswap.listing.datasource.model.types.ProductSettings;
import org.workswap.listing.datasource.model.types.ServiceSettings;
import org.workswap.listing.dto.EventSettingsDTO;
import org.workswap.listing.dto.ListingDTO;
import org.workswap.listing.dto.ShortListingDTO;
import org.workswap.listing.enums.ListingType;
import org.workswap.listing.services.ListingLocalizationService;
import org.workswap.listing.services.ListingMappingService;
import org.workswap.location.datasource.model.Location;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"production", "statistic"})
public class ListingMappingServiceImpl implements ListingMappingService {

    private final ListingLocalizationService listingLocalizationService;

    @Transactional
    public ListingDTO toDTO(Listing listing, Locale locale) {
        if (listing == null) {
            return null;
        }

        ListingType type = listing.getType();
        Location loc = listing.getLocation();

        listingLocalizationService.localizeListing(listing, locale);

        String categoryName = "";
        Long categoryId = null;

        switch (type) {
            case SERVICE:
                ServiceSettings sSettings = listing.getServiceSettings();
                if (sSettings != null) {
                    ServiceCategory category = sSettings.getCategory();
                    if (category != null) {
                        categoryName = category.getName();
                        categoryId = category.getId();
                    }
                }
                break;

            case PRODUCT:
                ProductSettings pSettings = listing.getProductSettings();
                if (pSettings != null) {
                    ProductCategory category = pSettings.getCategory();
                    if (category != null) {
                        categoryName = category.getName();
                        categoryId = category.getId();
                    }
                }
                break;

            default: 
                break;
        }
        
        ListingDTO dto = new ListingDTO(
            listing.getId(),
            listing.getAuthor().getId(),
            listing.getLocalizedTitle(),
            listing.getLocalizedDescription(),
            listing.getPrice(),
            listing.getPriceType(),
            listing.getType(),
            listing.getPublicType(),
            categoryName,
            categoryId,
            loc != null ? loc.getFullName() : null,
            loc != null ? loc.getId() : null,
            listing.getRating(),
            listing.getViews(),
            listing.getPublishedAt(),
            listing.isActive(),
            listing.getImagePath(),
            listing.isTestMode(),
            listing.isTemporary()
        );

        return dto;
    }

    public ShortListingDTO toShortDTO(Listing listing, Locale locale) {
        
        if (listing == null) {
            return null;
        }

        Location loc = listing.getLocation();

        listingLocalizationService.localizeListing(listing, locale);

        ShortListingDTO dto = new ShortListingDTO(
            listing.getId(),
            listing.getLocalizedTitle(),
            listing.getLocalizedDescription(),
            listing.getPrice(),
            listing.getPriceType(),
            listing.getType(),
            loc != null ? loc.getFullName() : null,
            listing.getRating(),
            listing.getImagePath(),
            listing.getPublishedAt(), 
            0,
            false
        );

        return dto;
    }

    public EventSettingsDTO toEventSettingsDTO(Listing listing) {

        if (listing == null) {
            return null;
        }

        EventSettings settings = listing.getEventSettings();

        return new EventSettingsDTO(
            settings.getEventDate(),
            settings.getRegistrationCloseTime(),
            settings.isRecurring(),
            settings.getRecurrencePattern(),
            settings.getEventStatus(),
            settings.isPublic(),
            settings.getMaxParticipants(),
            settings.getMinParticipants()
        );
    }

    public List<ListingDTO> toDTOList(Collection<Listing> listings, Locale locale) {
        return listings.stream().map(listing -> toDTO(listing, locale)).toList();
    }

    public List<ShortListingDTO> toShortDTOList(Collection<Listing> listings, Locale locale) {
        return listings.stream().map(listing -> toShortDTO(listing, locale)).toList();
    }

    public void setListingCategoryMeta(Listing listing) {
        ListingType type = listing.getType();
        switch (type) {
            case SERVICE:
                ServiceSettings sSettings = listing.getServiceSettings();
                if (sSettings != null) {
                    ServiceCategory category = sSettings.getCategory();
                    if (category != null) {
                        listing.setCategoryName(category.getName());
                        listing.setCategoryId(category.getId());
                    }
                }
                break;

            case PRODUCT:
                ProductSettings pSettings = listing.getProductSettings();
                if (pSettings != null) {
                    ProductCategory category = pSettings.getCategory();
                    if (category != null) {
                        listing.setCategoryName(category.getName());
                        listing.setCategoryId(category.getId());
                    }
                }
                break;

            default: 
                break;
        }
    }
}
