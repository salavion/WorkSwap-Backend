package org.workswap.core.services.components.producers;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.statistic.dto.SiteViewDTO;

import lombok.RequiredArgsConstructor;

@Service
@Profile("production")
@RequiredArgsConstructor
public class SiteViewProducer {

    private final AmqpTemplate amqpTemplate;

    public void sendSiteView(SiteViewDTO dto) {

        amqpTemplate.convertAndSend("siteViewQueue", dto);
    }
}

