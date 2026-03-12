package org.workswap.core.services.components.producers;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.statistic.dto.ListingViewDTO;

import lombok.RequiredArgsConstructor;

@Service
@Profile("production")
@RequiredArgsConstructor
public class ListingViewProducer {

    private final AmqpTemplate amqpTemplate;

    public void listingViewed(ListingViewDTO dto) {

        amqpTemplate.convertAndSend("listingViewQueue", dto);
    }
}
