package org.workswap.listing.services.event.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.workswap.listing.datasource.model.Listing;
import org.workswap.listing.datasource.model.ListingTranslation;
import org.workswap.listing.datasource.model.types.EventSettings;
import org.workswap.listing.datasource.repository.ListingRepository;
import org.workswap.listing.datasource.repository.ListingTranslationRepository;
import org.workswap.listing.dto.EventDTO;
import org.workswap.listing.dto.ImageDTO;
import org.workswap.listing.dto.ListingDTO;
import org.workswap.listing.services.ListingMappingService;
import org.workswap.listing.services.ListingQueryService;
import org.workswap.listing.services.SecurityFilterService;
import org.workswap.listing.services.event.EventQueryService;
import org.workswap.location.datasource.model.Location;
import org.workswap.shared.events.listing.ListingViewedEvent;
import org.workswap.user.datasource.model.User;
import org.workswap.user.dto.ShortUserDTO;
import org.workswap.user.dto.ShortUserProfileDTO;
import org.workswap.user.services.UserMappingService;
import org.workswap.security.dto.UserAuthData;
import org.workswap.security.enums.UserStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Profile("server")
public class EventQueryServiceImpl implements EventQueryService {

    private final ListingRepository listingRepository;

    private final ListingTranslationRepository translationRepository;
    private final ListingMappingService listingMappingService;
    private final UserMappingService userMappingService;
    private final SecurityFilterService securityFilterService;
    private final ListingQueryService listingQueryService;
    private final ApplicationEventPublisher eventPublisher;
    
    public boolean existEventParticipant(UserAuthData authData, Long eventId) {
        return listingRepository.existsParticipant(eventId, authData.id());
    }

    public EventDTO.Settings getEventSettingsDTO(UserAuthData authData, Long eventId) {

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

    public EventDTO.Page getEventPage(Optional<UserAuthData> optAuthData, String token, Long eventId, String locale) {
        if (eventId == null) {
            throw new IllegalStateException("ID событмя отсутствует");
        }
        Listing listing = listingRepository.findById(eventId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NO_CONTENT, "Событие не найдено отсутствует"));

        boolean isAuthor = false;

        Location loc = listing.getLocation();
        ListingTranslation translation = translationRepository.findBestTranslation(eventId, locale);

        EventSettings event = listing.getEventSettings();

        ShortUserProfileDTO author = userMappingService.toShortProfileDTO(listing.getAuthor());
        List<ImageDTO> images = listing.getImages().stream()
            .map(image -> new ImageDTO(image.getId(), eventId, listingMappingService.getImageLink(image))).toList();

        List<ShortUserDTO> participants = userMappingService.toShortDTOList(event.getParticipants());

        if (optAuthData.isPresent()) {
            UserAuthData authData = optAuthData.get();

            isAuthor = securityFilterService.listingAuthorFilter(authData, eventId);

            eventPublisher.publishEvent(
                new ListingViewedEvent(
                    authData.id(), 
                    eventId, 
                    authData.status().equals(UserStatus.TEMP), 
                    LocalDateTime.now()
                ));
        }

        ListingDTO.Full listingDto = new ListingDTO.Full(
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
        );

        EventDTO.Settings settings = new EventDTO.Settings(
            event.getEventDate(),
            event.getRegistrationCloseTime(),
            event.isRecurring(),
            event.getRecurrencePattern(),
            event.getEventStatus(),
            event.isPublic(),
            event.getMaxParticipants(),
            event.getMinParticipants()
        );

        EventDTO.Page dto = new EventDTO.Page(
            listingDto,
            author,
            images,

            settings,
            isAuthor ? participants : null,
            participants.size()
        );
        return dto;
    }
}
