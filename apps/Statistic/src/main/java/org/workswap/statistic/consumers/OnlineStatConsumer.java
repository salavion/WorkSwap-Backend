package org.workswap.statistic.consumers;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.workswap.statistic.dto.OnlineStatSnapshotDTO;
import org.workswap.statistic.services.StatisticCommandService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OnlineStatConsumer {
    
    private final StatisticCommandService statisticCommandService;

    @RabbitListener(queues = "onlineStatQueue")
    public void onlineStatQueue(OnlineStatSnapshotDTO dto) {
        
        statisticCommandService.saveOnlineStatSnapshot(dto);
    }
}
