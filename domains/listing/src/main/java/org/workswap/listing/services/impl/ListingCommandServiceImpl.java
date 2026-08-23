package org.workswap.listing.services.impl;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.model.ListingTranslation;
import org.workswap.listing.datasource.model.category.ProductCategory;
import org.workswap.listing.datasource.model.category.ServiceCategory;
import org.workswap.listing.datasource.repository.ListingRepository;
import org.workswap.listing.datasource.repository.category.ProductCategoryRepository;
import org.workswap.listing.datasource.repository.category.ServiceCategoryRepository;
import org.workswap.listing.dto.ListingTranslationDTO;
import org.workswap.listing.enums.PriceType;
import org.workswap.listing.services.ListingCommandService;
import org.workswap.listing.services.ListingQueryService;
import org.workswap.listing.services.SecurityFilterService;
import org.workswap.location.datasource.model.Location;
import org.workswap.location.datasource.repository.LocationRepository;
import org.workswap.shared.events.listing.ListingDeletedEvent;
import org.workswap.shared.locale.LanguageMapper;
import org.workswap.shared.locale.LocalisationConfig.LanguageUtils;
import org.workswap.user.datasource.model.User;

import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;

import jakarta.persistence.EntityManager;

import org.salavion.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile({"server"})
public class ListingCommandServiceImpl implements ListingCommandService {

    private static final Logger logger = LoggerFactory.getLogger(ListingCommandService.class);

    private final ListingRepository listingRepository;
    private final LocationRepository locationRepository;

    private final EntityManager entityManager;
    private final SecurityFilterService securityFilterService;
    private final ListingQueryService listingQueryService;
    private final ProductCategoryRepository productCategoryRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    private final ApplicationEventPublisher eventPublisher;

    public Listing create(UserAuthData authData, String type) {
        User authorProxy = entityManager.getReference(User.class, authData.id());
        Listing listing = new Listing(authorProxy, type);
        return listingRepository.save(listing);
    }

    @Transactional
    public void delete(UserAuthData authData, Long listingId) {

        securityFilterService.listingUpdateFilter(authData, listingId);

        Listing listing = listingQueryService.getListingById(listingId);

        logger.debug("Удаляем объявление");
        listingRepository.delete(listing);

        eventPublisher.publishEvent(new ListingDeletedEvent(listingId));
    }

    public void addListingToFavorite(UserAuthData authData, Long listingId) {
        listingRepository.addFavoriteListing(authData.id(), listingId);
    }

    public void removeListingFromFavorite(UserAuthData authData, Long listingId) {
        listingRepository.removeFavoriteListing(authData.id(), listingId);
    }

    public void publish(UserAuthData authData, Long listingId) {
        securityFilterService.listingUpdateFilter(authData, listingId);

        Listing listing = listingQueryService.getListingById(listingId);

        if (!listing.isTemporary()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Объявление уже опубликовано");
        }

        listing.setTemporary(false);
        listing.setPublishedAt(LocalDateTime.now());
        listingRepository.save(listing);
    }

    public void modifyListingParam(UserAuthData authData, Long listingId, Map<String, Object> updates) throws AccessDeniedException {
        securityFilterService.listingUpdateFilter(authData, listingId);

        Listing listing = listingQueryService.getListingById(listingId);

        updates.forEach((key, value) -> {
            logger.debug("Обновляем часть объявления: {}", key );

            if (value == null) {
                throw new IllegalStateException("Вы передали пустой параметр");
            }

            switch (key) {
                case "price":
                    Double price;
                    if (value instanceof Number) {
                        price = ((Number) value).doubleValue();
                    } else {
                        price = Double.parseDouble(value.toString());
                    }
                    listing.setPrice(price);
                    break;
                case "mainImage":
                    listing.setImagePath((String) value);
                    break;
                case "priceType":
                    switch (listing.getPublicType()) {
                        case PRODUCT_GIVEAWAY, PRODUCT_SWAP, PRODUCT_WANTED_FREE:
                            
                            break;
                    
                        default:
                            listing.setPriceType(PriceType.valueOf((String) value));
                            break;
                    }
                    break;
                case "active":
                    listing.setActive((Boolean) value);
                    break;
                case "testMode":
                    listing.setTestMode((Boolean) value);
                    break;
                case "location":
                    Long locId = ((Number) value).longValue(); // безопасно для Integer и Long
                    Location loc = locationRepository.findById(locId).orElse(null);
                    listing.setLocation(loc);
                    break;
                case "accessToken":
                    listing.setAccessToken((String) value);
                    break;
                case "category":
                    Long catId = ((Number) value).longValue(); // безопасно для Integer и Long
                    switch (listing.getType()) {
                        case PRODUCT:
                            ProductCategory pCat = productCategoryRepository.findById(catId).orElse(null);
                            listing.getProductSettings().setCategory(pCat);
                            break;
                        case SERVICE:
                            ServiceCategory sCat = serviceCategoryRepository.findById(catId).orElse(null);
                            listing.getServiceSettings().setCategory(sCat);
                            break;
                        default:
                            break;
                    }
                    break;
            }
        });

        listingRepository.save(listing);
    }

    @Transactional
    public Set<String> updateListingTranslations(
        UserAuthData authData,
        Long listingId, 
        Map<String, ListingTranslationDTO> translationsMap
    ) {

        securityFilterService.listingUpdateFilter(authData, listingId);

        Listing listing = listingQueryService.getListingById(listingId);

        Map<String, ListingTranslation> currentTranslations = listing.getTranslations();
        Set<String> newLanguages = new HashSet<>();

        LanguageDetector detector = LanguageDetectorBuilder.fromLanguages(LanguageUtils.SUPPORTED_LANGUAGES_LINGUA).build();

        for (Map.Entry<String, ListingTranslationDTO> entry : translationsMap.entrySet()) {

            String lang = entry.getKey();
            ListingTranslationDTO dto = entry.getValue();

            String title = dto.title();
            String description = dto.description();

            logger.debug("incoming {} -> title: {}", lang, title);

            // Определяем язык, если пришёл undetected
            if (lang != null && "undetected".equalsIgnoreCase(lang.trim())) {
                if (title != null && !title.isBlank()) {
                    lang = LanguageMapper.toShortCode(detector.detectLanguageOf(title));
                } else if (description != null && !description.isBlank()) {
                    lang = LanguageMapper.toShortCode(detector.detectLanguageOf(description));
                }
            }

            // Добавляем язык, если есть данные
            if ((title != null && !title.isBlank()) ||
                (description != null && !description.isBlank())) {

                logger.debug("using lang {} -> {}", lang, title);
                newLanguages.add(lang);
            }

            // update or create translation
            if (currentTranslations.containsKey(lang)) {
                ListingTranslation tr = currentTranslations.get(lang);
                tr.setTitle(title);
                tr.setDescription(description);
            } else {
                ListingTranslation tr = new ListingTranslation(lang, title, description, listing);
                currentTranslations.put(lang, tr);
            }
        }

        // удаляем переводы, которых больше нет
        currentTranslations.keySet().removeIf(lang -> !newLanguages.contains(lang));

        return newLanguages;
    }
}