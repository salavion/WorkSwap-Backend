package org.workswap.listing.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.workswap.category.datasource.Category;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.model.ListingTranslation;
import org.workswap.listing.datasource.repository.ListingRepository;
import org.workswap.listing.datasource.repository.ListingTranslationRepository;
import org.workswap.listing.dto.CatalogFilterDTO;
import org.workswap.listing.dto.CatalogRequest;
import org.workswap.listing.dto.ImageDTO;
import org.workswap.listing.dto.ListingDTO;
import org.workswap.listing.dto.ListingTranslationDTO;
import org.workswap.listing.dto.ShortListingDTO;
import org.workswap.listing.enums.ListingPublicType;
import org.workswap.listing.enums.ListingType;
import org.workswap.listing.enums.ProductType;
import org.workswap.listing.enums.ServiceType;
import org.workswap.listing.services.ListingMappingService;
import org.workswap.listing.services.ListingQueryService;
import org.workswap.listing.services.SecurityFilterService;
import org.workswap.listing.services.category.query.ProductCategoryQueryService;
import org.workswap.listing.services.category.query.ServiceCategoryQueryService;
import org.workswap.location.datasource.model.Location;
import org.workswap.shared.locale.LocalisationConfig.LanguageUtils;
import org.workswap.user.datasource.repository.UserRepository;
import org.workswap.user.dto.ShortUserProfileDTO;
import org.workswap.user.services.UserMappingService;
import org.salavion.security.dto.UserAuthData;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile("production")
public class ListingQueryServiceImpl implements ListingQueryService {

    private static final Logger logger = LoggerFactory.getLogger(ListingQueryService.class);

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    private final ServiceCategoryQueryService serviceCategoryQueryService;
    private final ProductCategoryQueryService productCategoryQueryService;
    private final ListingMappingService mappingService;
    private final ListingTranslationRepository translationRepository;
    private final SecurityFilterService securityFilterService;
    private final UserMappingService userMappingService;

    /* private final ListingViewProducer listingViewProducer; */

    public boolean isFavorite(UserAuthData authData, Long listingId) {
        return listingRepository.existsFavoriteListing(authData.id(), listingId);
    }

    public List<ListingDTO.Full> getRecentListings(int amount, String locale) {
        Pageable pageable = PageRequest.of(0, amount);
        List<Listing> listings = listingRepository.findAllByTemporaryFalseOrderByCreatedAtDesc(pageable).getContent();
        return mappingService.toDTOList(listings, locale);
    }

    @NonNull
    public Listing getListingById(Long listingId) {

        Objects.requireNonNull(listingId, "listingId must not be null");

        return Objects.requireNonNull(
            listingRepository.findById(listingId)
                .orElseThrow(() ->
                    new EntityNotFoundException("Объявление не найдено с ID = " + listingId)
                )
        );
    }

    public CatalogRequest getSortedCatalog(
        UserAuthData authData, 
        CatalogFilterDTO filters, 
        String locale
    ) {

        List<String> languages = new ArrayList<>();

        logger.debug("Язык: {}", locale);

        if (filters.translationsFilter()) {
            userRepository.findLanguagesByUserId(authData.id());

            if (!languages.contains(locale)) {
                languages.add(locale);
            }
        } else {
            languages.addAll(LanguageUtils.SUPPORTED_LANGUAGES);
        }

        long t0 = System.currentTimeMillis();

        List<? extends Category> categories = Collections.emptyList();

        ListingPublicType publicType = null;

        ListingType listingType = null;
        ServiceType serviceType = null;
        ProductType productType = null;

        if (filters.type() != null) {
            publicType = ListingPublicType.valueOf(filters.type());
            listingType = publicType.getListingType();

            logger.debug("Тип объявления: {} {}", publicType, listingType);

            switch (listingType) {
                case SERVICE:
                    categories = serviceCategoryQueryService.getAllDescendantsById(filters.categoryId());
                    serviceType = publicType.getServiceType();
                    break;
                case PRODUCT:
                    categories = productCategoryQueryService.getAllDescendantsById(filters.categoryId());
                    productType = publicType.getProductType();
                    break;
                case EVENT:
                    // ничего
                    break;
            }
        }

        logger.debug("⏱️ categories: {} ms", System.currentTimeMillis() - t0);

        logger.debug("Все параметры переформатированы, делаем запрос в бд");

        logger.debug("Языки: {}", languages);

        t0 = System.currentTimeMillis();

        PageRequest pageable = PageRequest.of(filters.page(), 39);
        Page<ShortListingDTO> listings = listingRepository.findListings(
            categories,
            filters.location(),
            filters.searchQuery(),
            filters.hasReviews(),
            filters.translationsFilter(),
            languages,
            listingType,
            serviceType,
            productType,
            filters.sortBy(),
            pageable,
            authData
        );

        List<Long> ids = listings.stream().map(l -> l.id()).toList();
        List<ListingTranslation> translations = translationRepository.findByListingIdsAndLanguages(ids, languages);

        Map<Long, ListingTranslation> translationByListing =
            translations.stream()
                .collect(Collectors.toMap(
                    t -> t.getListing().getId(),
                    t -> t
                ));

        List<ShortListingDTO> enriched = listings.getContent().stream()
            .map(l -> {
                ListingTranslation t = translationByListing.get(l.id());

                return new ShortListingDTO(
                    l.id(),
                    t != null ? t.getTitle() : l.localizedTitle(),
                    t != null ? t.getDescription() : l.localizedDescription(),
                    l.price(),
                    l.priceType(),
                    l.type(),
                    l.location(),
                    l.rating(),
                    l.imagePath(),
                    l.publishedAt(),
                    l.likes(),
                    l.liked()
                );
            })
            .toList();


        logger.debug("⏱️ DB query: {} ms", System.currentTimeMillis() - t0);
        logger.debug("Пришёл запрос из бд");

        return new CatalogRequest(listings.getTotalPages(), enriched);
    }

    public List<ListingDTO.Full> getListingDtosByUser(Long userId, String locale) {

        List<Listing> listings = listingRepository.findByAuthorIdAndActiveTrue(userId);
        return mappingService.toDTOList(listings, locale);
    }

    public List<ListingDTO.Full> getOwnListingsByUser(UserAuthData authData, String locale) {
        List<Listing> listings = listingRepository.findByAuthorIdWithAllDetails(authData.id());
        return mappingService.toDTOList(listings, locale);
    }

    public List<ShortListingDTO> getFavorites(UserAuthData authData, String locale) {
        return listingRepository.findLikedListings(authData.id(), locale);
    }

    public Map<String, ListingTranslationDTO> getTranslations(Long listingId) {
        if (listingId == null) {
            throw new IllegalStateException("ID объявления отсутствует");
        }

        Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new EntityNotFoundException("Listing not found"));
        
        return listing.getTranslations().entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> new ListingTranslationDTO(
                    entry.getValue().getTitle(), 
                    entry.getValue().getDescription()
                )
            ));
    }

    public List<ImageDTO> getImages(Long listingId) {

        if (listingId == null) {
            throw new IllegalStateException("ID объявления отсутствует");
        }

        return listingRepository.findById(listingId).orElse(null)
            .getImages()
            .stream()
            .map(image -> new ImageDTO(image.getId(), listingId, mappingService.getImageLink(image)))
            .toList();
    }

    public String getListingToken(UserAuthData authData, Long listingId) {
        securityFilterService.listingUpdateFilter(authData, listingId);

        Listing listing = getListingById(listingId);
        return listing.getAccessToken();
    }

    public List<ListingDTO.Full> getDrafts(UserAuthData authData, String locale) {

        List<Listing> listings = listingRepository.findByAuthorIdAndTemporary(authData.id(), true);

        return mappingService.toDTOList(listings, locale);
    }

    public ListingDTO.Page getListingPage(UserAuthData authData, String token, Long listingId, String locale) {

        if (listingId == null) {
            throw new IllegalStateException("ID объявления отсутствует");
        }

        Listing listing = getListingById(listingId);
        ListingTranslation translation = translationRepository.findBestTranslation(listingId, locale);

        securityFilterService.listingGetFilter(authData, listing, token);
        mappingService.setListingCategoryMeta(listing);

        Location loc = listing.getLocation();

        ShortUserProfileDTO author = userMappingService.toShortProfileDTO(listing.getAuthor());
        List<ImageDTO> images = listing.getImages().stream()
            .map(image -> new ImageDTO(image.getId(), listingId, mappingService.getImageLink(image))).toList();

        /* listingViewProducer.listingViewed(new ListingViewDTO(authData.id(), listingId, authData.status().equals(UserStatus.TEMP))); */ 

        // TODO переписать на отправку события которое словит другой модуль, модуль rabbit mq 

        return new ListingDTO.Page(
            new ListingDTO.Full(
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
                null,
                null,
                loc != null ? loc.getId() : null,
                listing.getViews(),
                listing.isActive(),
                listing.isTestMode(),
                listing.isTemporary()
            ),
            author,
            images
        );
    }

    public ShortListingDTO getCatalogListing(Long listingId, UserAuthData authData, String locale) {

        Listing listing = getListingById(listingId);
        securityFilterService.listingGetFilter(authData, listing, null);

        return mappingService.toShortDTO(listing, locale);
    }

    public ListingDTO.Full getListingDTO(Long listingId, UserAuthData authData, String locale) {
        securityFilterService.listingUpdateFilter(authData, listingId);

        Listing listing = getListingById(listingId);
        return mappingService.toDTO(listing, locale);
    }
}