package org.workswap.listing.services.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.workswap.listing.datasource.model.Image;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.model.ListingTranslation;
import org.workswap.listing.datasource.model.category.ProductCategory;
import org.workswap.listing.datasource.model.category.ServiceCategory;
import org.workswap.listing.datasource.model.types.EventSettings;
import org.workswap.listing.datasource.model.types.ProductSettings;
import org.workswap.listing.datasource.model.types.ServiceSettings;
import org.workswap.listing.datasource.repository.ListingTranslationRepository;
import org.workswap.listing.dto.EventDTO;
import org.workswap.listing.dto.ListingDTO;
import org.workswap.listing.dto.ShortListingDTO;
import org.workswap.listing.enums.ListingType;
import org.workswap.listing.services.ListingMappingService;
import org.workswap.location.datasource.model.Location;
import org.workswap.storage.util.ImageFormatRegistry;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"server", "statistic"})
public class ListingMappingServiceImpl implements ListingMappingService {

    private final ListingTranslationRepository translationRepository;

    @Transactional
    public ListingDTO.Full toDTO(Listing listing, ListingTranslation translation) {
        if (listing == null) {
            return null;
        }

        ListingType type = listing.getType();
        Location loc = listing.getLocation();

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

        Image mainImage = listing.getImages()
            .stream()
            .filter(i -> 
                listing.getImagePath().equals(getImageLink(i))
            )
            .findFirst()
            .orElse(null);
        
        ListingDTO.Full dto = new ListingDTO.Full(
            listing.getId(),
            translation != null ? translation.getTitle() : null,
            translation != null ? translation.getDescription() : null,
            listing.getPrice(),
            listing.getPriceType(),
            listing.getType(),
            loc != null ? loc.getFullName() : null,
            listing.getRating(),
            listing.getImagePath(),
            listing.getPublishedAt(),
            0,
            false,

            listing.getAuthor().getId(),
            listing.getPublicType(),
            categoryName,
            categoryId,
            loc != null ? loc.getId() : null,
            mainImage != null ? mainImage.getId() : null,
            listing.getAccessToken(), 
            listing.getViews(),
            listing.isActive(),
            listing.isTestMode(),
            listing.isTemporary()
        );

        return dto;
    }

    @Transactional
    public ListingDTO.Full toDTO(Listing listing, String locale) {

        ListingTranslation translation = translationRepository.findBestTranslation(
            listing.getId(), locale);

        return toDTO(listing, translation);
    }

    @Transactional
    public ShortListingDTO toShortDTO(Listing listing, String locale) {

        ListingTranslation translation = translationRepository.findBestTranslation(
            listing.getId(), locale);

        return toShortDTO(listing, translation);
    }

    public ShortListingDTO toShortDTO(Listing listing, ListingTranslation translation) {
        
        if (listing == null) {
            return null;
        }

        Location loc = listing.getLocation();

        ShortListingDTO dto = new ShortListingDTO(
            listing.getId(),
            translation != null ? translation.getTitle() : null,
            translation != null ? translation.getDescription() : null,
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

    public EventDTO.Settings toEventSettingsDTO(Listing listing) {

        if (listing == null) {
            return null;
        }

        EventSettings settings = listing.getEventSettings();

        return new EventDTO.Settings(
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

    public Map<Long, ListingTranslation> getBestListingsTranslations(Collection<Listing> listings, String locale) {
        List<Long> ids = listings.stream()
            .map(Listing::getId)
            .toList();

        return translationRepository
            .findBestTranslations(ids, locale)
            .stream()
            .collect(Collectors.toMap(
                ListingTranslation::getListingId,
                Function.identity()
            ));
    }

    public List<ListingDTO.Full> toDTOList(Collection<Listing> listings, String locale) {

        Map<Long, ListingTranslation> translations = getBestListingsTranslations(listings, locale);

        return listings.stream()
            .map(listing -> toDTO(
                listing, 
                translations.get(listing.getId())
            ))
            .toList();
    }

    public List<ShortListingDTO> toShortDTOList(Collection<Listing> listings, String locale) {
        Map<Long, ListingTranslation> translations = getBestListingsTranslations(listings, locale);

        return listings.stream()
            .map(listing -> toShortDTO(
                listing, 
                translations.get(listing.getId())
            ))
            .toList();
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

    public String getImageLink(Image image) {
        return "https://cloud.workswap.org/listing-images/%s.%s".formatted(
            image.getObjectKey(), 
            ImageFormatRegistry.extensionFromMime(image.getContentType()));
    }
}
