package org.workswap.statistic.ampq.producers;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.shared.events.listing.ListingViewedEvent;

import lombok.RequiredArgsConstructor;

@Service
@Profile("production")
@RequiredArgsConstructor
public class ListingViewProducer {

    private final AmqpTemplate amqpTemplate;

    public void listingViewed(ListingViewedEvent event) {

        amqpTemplate.convertAndSend("listingViewQueue", event);
    }
}
