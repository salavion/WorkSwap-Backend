package org.workswap.listing.services.event.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.model.types.EventSettings;
import org.workswap.listing.datasource.repository.ListingRepository;
import org.workswap.listing.dto.EventPageRequest;
import org.workswap.listing.dto.EventSettingsDTO;
import org.workswap.listing.dto.ImageDTO;
import org.workswap.listing.enums.ListingPublicType;
import org.workswap.listing.enums.ListingType;
import org.workswap.listing.services.ListingLocalizationService;
import org.workswap.listing.services.ListingMappingService;
import org.workswap.listing.services.ListingQueryService;
import org.workswap.listing.services.SecurityFilterService;
import org.workswap.listing.services.event.EventQueryService;
import org.workswap.location.datasource.model.Location;
import org.workswap.user.datasource.model.User;
import org.workswap.user.dto.ShortUserDTO;
import org.workswap.user.dto.ShortUserProfileDTO;
import org.workswap.user.services.UserMappingService;
import org.salavion.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile("production")
public class EventQueryServiceImpl implements EventQueryService {

    private static final Logger logger = LoggerFactory.getLogger(EventQueryService.class);

    private final ListingRepository listingRepository;

    private final ListingLocalizationService listingLocalizationService;
    private final ListingMappingService listingMappingService;
    private final UserMappingService userMappingService;
    private final SecurityFilterService securityFilterService;
    private final ListingQueryService listingQueryService;

    /* private final ListingViewProducer listingViewProducer; */

    public boolean existEventParticipant(UserAuthData authData, Long eventId) {
        return listingRepository.existsParticipant(eventId, authData.id());
    }

    public EventSettingsDTO getEventSettingsDTO(UserAuthData authData, Long eventId) {

        if (eventId == null) {
            throw new IllegalStateException("ID события отсутствует");
        }

        Listing event = listingQueryService.getListingById(eventId);

        securityFilterService.listingUpdateFilter(authData, eventId);

        return listingMappingService.toEventSettingsDTO(event);
    }

    public List<ShortUserDTO> getEventParticipants(UserAuthData authData, Long eventId) {
        if (eventId == null) {
            throw new IllegalStateException("ID события отсутствует");
        }
        Listing event = listingQueryService.getListingById(eventId);
        try {
            securityFilterService.listingUpdateFilter(authData, eventId);

            List<ShortUserDTO> list = new ArrayList<>();
            for (User participant : event.getEventSettings().getParticipants()) {
                ShortUserDTO dto = userMappingService.toShortDTO(participant);
                list.add(dto);
            }

            return list;
        } catch (AccessDeniedException e) {
            return null;
        }
    }

    public EventPageRequest getEventPage(UserAuthData authData, String token, Long eventId, String locale) {
        if (eventId == null) {
            throw new IllegalStateException("ID событмя отсутствует");
        }
        Listing event = listingRepository.findById(eventId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NO_CONTENT, "Событие не найдено отсутствует"));

        boolean isAuthor = securityFilterService.listingAuthorFilter(authData, eventId);
        logger.debug("Пользователь является автором события? {}", isAuthor);

        Location loc = event.getLocation();

        listingLocalizationService.localizeListing(event, Locale.of(locale));

        EventSettings settings = event.getEventSettings();

        ShortUserProfileDTO author = userMappingService.toShortProfileDTO(event.getAuthor());
        List<ImageDTO> images = event.getImages().stream()
            .map(image -> new ImageDTO(image.getId(), eventId, image.getPath())).toList();

        List<ShortUserDTO> participants = userMappingService.toShortDTOList(settings.getParticipants());

        /* listingViewProducer.listingViewed(new ListingViewDTO(authData.id(), eventId, authData.status().equals(UserStatus.TEMP))); */

        EventPageRequest dto = new EventPageRequest(
            event.getId(),
            event.getLocalizedTitle(),
            event.getLocalizedDescription(),
            event.getPrice(),
            event.getPriceType(),
            null,
            loc != null ? loc.getFullName() : null,
            event.getRating(),
            event.getViews(),
            event.getPublishedAt(),
            event.getImagePath(),
            ListingType.EVENT,
            ListingPublicType.EVENT,
            settings.getEventDate(),
            settings.getRegistrationCloseTime(),
            settings.isRecurring(),
            settings.getRecurrencePattern(),
            settings.getEventStatus(),
            settings.isPublic(),
            settings.getMaxParticipants(),
            settings.getMinParticipants(),
            author,
            images,
            isAuthor ? participants : null,
            participants.size()
        );
        return dto;
    }
}
