package org.workswap.statistic.eventhandlers;

import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.workswap.shared.events.listing.ListingViewedEvent;
import org.workswap.statistic.ampq.producers.ListingViewProducer;

import lombok.RequiredArgsConstructor;

@Component
@Profile("production")
@RequiredArgsConstructor
public class ListingViewedEventHandler {

    private final ListingViewProducer listingViewProducer;
    
    @EventListener
    public void handle(ListingViewedEvent event) {
        listingViewProducer.listingViewed(event);
    }
}
