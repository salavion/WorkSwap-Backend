package org.workswap.listing.controllers;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.workswap.listing.dto.EventDTO;
import org.workswap.listing.services.event.EventCommandService;
import org.workswap.listing.services.event.EventQueryService;
import org.workswap.user.dto.ShortUserDTO;
import org.salavion.security.dto.UserAuthData;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventController {

    private final EventQueryService eventQueryService;
    private final EventCommandService eventCommandService;
    
    @GetMapping("/{eventId}")
    @PreAuthorize("hasAuthority('GET_LISTING_BY_ID')")
    public EventDTO.Page getEventListing(
        @AuthenticationPrincipal UserAuthData authData,
        @PathVariable Long eventId, 
        @RequestParam(required = false) String token,
        @RequestParam String locale
    ) {
        return eventQueryService.getEventPage(authData, token, eventId, locale);
    }
    
    @GetMapping("/{eventId}/participants")
    @PreAuthorize("hasAuthority('GET_EVENT_PARTICIPANTS')")
    public List<ShortUserDTO> getEventPaticipants(
        @AuthenticationPrincipal UserAuthData authData, 
        @PathVariable Long eventId
    ) {
        return eventQueryService.getEventParticipants(authData, eventId);
    }

    @GetMapping("/{eventId}/participants/check")
    @PreAuthorize("hasAuthority('CHECK_EVENT_PARTICIPANTS')")
    public boolean checkEventPaticipant(
        @AuthenticationPrincipal UserAuthData authData, 
        @PathVariable Long eventId
    ) {
        return eventQueryService.existEventParticipant(authData, eventId);
    }

    @PostMapping("/{eventId}/participants")
    @PreAuthorize("hasAuthority('ADD_EVENT_PARTICIPANT')")
    public void addEventPaticipant(
        @AuthenticationPrincipal UserAuthData authData, 
        @PathVariable Long eventId
    ) {
        eventCommandService.addEventParticipant(authData, eventId);
    }

    @DeleteMapping("/{eventId}/participants")
    @PreAuthorize("hasAuthority('REMOVE_EVENT_PARTICIPANT')")
    public void removeEventPaticipant(
        @AuthenticationPrincipal UserAuthData authData, 
        @PathVariable Long eventId 
    ) {
        eventCommandService.removeEventParticipant(authData, eventId);
    }

    @PatchMapping("/{eventId}/modify")
    @PreAuthorize("hasAuthority('UPDATE_LISTING')")
    public void modifyListing(
        @AuthenticationPrincipal UserAuthData authData,
        @PathVariable Long eventId,
        @RequestBody Map<String, Object> updates
    ) throws AccessDeniedException {
        eventCommandService.modifyEventParam(authData, eventId, updates);
    }

    @GetMapping("/{eventId}/settings")
    @PreAuthorize("hasAuthority('GET_EVENT_SETTINGS')")
    public EventDTO.Settings getEventSettings(
        @AuthenticationPrincipal UserAuthData authData,
        @PathVariable Long eventId
    ) {
        return eventQueryService.getEventSettingsDTO(authData, eventId);
    }
}
