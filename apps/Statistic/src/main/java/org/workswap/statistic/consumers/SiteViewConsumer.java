package org.workswap.statistic.consumers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.workswap.statistic.dto.SiteViewDTO;
import org.workswap.statistic.services.StatisticCommandService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SiteViewConsumer {
    
    private final StatisticCommandService statisticCommandService;

    @RabbitListener(queues = "siteViewQueue")
    public void siteViewQueue(SiteViewDTO dto) {
        
        statisticCommandService.saveSiteView(dto);
    }
}
