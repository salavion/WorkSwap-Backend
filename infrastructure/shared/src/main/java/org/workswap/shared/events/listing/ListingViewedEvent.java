package org.workswap.shared.events.listing;

import java.time.LocalDateTime;

public record ListingViewedEvent(
    Long userId,
    Long listingId,
    boolean temporary,
    LocalDateTime timestamp
) {
}
