package org.workswap.listing.services.event;

import java.util.List;

import org.salavion.security.dto.UserAuthData;
import org.workswap.listing.dto.EventPageRequest;
import org.workswap.listing.dto.EventSettingsDTO;
import org.workswap.user.dto.ShortUserDTO;

public interface EventQueryService {
    boolean existEventParticipant(UserAuthData authData, Long eventId);

    List<ShortUserDTO> getEventParticipants(UserAuthData authData, Long eventId);
    EventSettingsDTO getEventSettingsDTO(UserAuthData authData, Long eventId);
    EventPageRequest getEventPage(UserAuthData authData, String token, Long eventId, String locale);
}
