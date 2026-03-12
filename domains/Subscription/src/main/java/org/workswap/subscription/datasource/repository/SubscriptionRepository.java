package org.workswap.subscription.datasource.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.workswap.subscription.datasource.model.Subscription;
import org.workswap.subscription.enums.SubscriptionType;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Subscription findBySubscriberIdAndTypeAndTargetId(Long subscriberId, SubscriptionType type, Long targetId);

    List<Subscription> findBySubscriberId(Long subscriberId);
    List<Subscription> findByTypeAndTargetId(SubscriptionType type, Long targetId);

    boolean existsBySubscriberIdAndTypeAndTargetId(Long subscriberId, SubscriptionType type, Long targetId);
    void deleteBySubscriberIdAndTypeAndTargetId(Long subscriberId, SubscriptionType type, Long targetId);
}
