package org.workswap.core.services.components.producers;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.statistic.dto.AllListingsStatSnapshotDTO;

import lombok.RequiredArgsConstructor;

@Service
@Profile("production")
@RequiredArgsConstructor
public class ListingsStatProducer {

    private final AmqpTemplate amqpTemplate;

    public void sendListingsStat(AllListingsStatSnapshotDTO dto) {

        amqpTemplate.convertAndSend("listinsStatQueue", dto);
    }
}

