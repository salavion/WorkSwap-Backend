package org.workswap.subscription.services;

import org.workswap.subscription.datasource.model.Subscription;
import org.workswap.subscription.dto.SubscriptionDTO;

public interface SubscriptionMappingService {
    
    SubscriptionDTO toDTO(Subscription subscription);
}
