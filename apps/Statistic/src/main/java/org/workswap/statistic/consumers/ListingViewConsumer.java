package org.workswap.statistic.consumers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.workswap.shared.events.listing.ListingViewedEvent;
import org.workswap.statistic.services.StatisticCommandService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListingViewConsumer {
    
    private final StatisticCommandService statisticCommandService;

    @RabbitListener(queues = "listingViewQueue")
    public void listingViewQueue(ListingViewedEvent event) {

        statisticCommandService.saveListingView(event);
    }
}
