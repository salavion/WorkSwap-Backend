package org.workswap.subscription.services.impl;

import org.workswap.subscription.datasource.model.Subscription;
import org.workswap.subscription.dto.SubscriptionDTO;
import org.workswap.subscription.services.SubscriptionMappingService;

public class SubscriptionMappingServiceImpl implements SubscriptionMappingService {
    
    public SubscriptionDTO toDTO(Subscription subscription) {
        return new SubscriptionDTO(
            subscription.getId(),
            subscription.getSubscriber().getId(),
            subscription.getType().toString(),
            subscription.getTargetId(),
            subscription.getCreatedAt()
        );
    }
}
