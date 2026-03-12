package org.workswap.core.sheduler;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.workswap.core.services.components.OnlineCounter;
import org.workswap.core.services.components.producers.OnlineStatProducer;
import org.workswap.statistic.dto.OnlineStatSnapshotDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OnlineCounterSheduler {
    
    private final OnlineCounter onlineCounter;
    private final OnlineStatProducer onlineStatProducer;

    @Value("${isTest}")
    private boolean isTest;

    @Scheduled(fixedRate = 15000)
    public void saveOnlineAnalytic() {

        if (isTest == false) {
            OnlineStatSnapshotDTO dto = new OnlineStatSnapshotDTO(
                onlineCounter.getOnlineUsers().size(),
                LocalDateTime.now()
            );

            onlineStatProducer.sendOnlineStat(dto);
        }
    }
}
