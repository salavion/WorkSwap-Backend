package org.workswap.subscription.services;

public interface SubscriptionQueryService {
        
    boolean existsEventSubscription(Long subscriberId, Long eventId);
    boolean existsUserSubscription(Long subscriberId, Long userId);
}
