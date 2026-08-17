package org.workswap.review.eventhandlers;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.workswap.review.ampq.ReviewProducer;
import org.workswap.shared.events.review.ReviewCreatedEvent;

import lombok.RequiredArgsConstructor;

@Component
@Profile("production")
@RequiredArgsConstructor
public class ReviewCreatedEventHandler {

    private final ReviewProducer reviewProducer;
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ReviewCreatedEvent event) {
        reviewProducer.reviewCreated(event);
    }
}
