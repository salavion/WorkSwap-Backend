package org.workswap.review.ampq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.workswap.shared.events.review.ReviewCreatedEvent;

import lombok.RequiredArgsConstructor;

@Service
@Profile("server")
@RequiredArgsConstructor
public class ReviewProducer {

    private final AmqpTemplate amqpTemplate;

    public void reviewCreated(ReviewCreatedEvent event) {

        amqpTemplate.convertAndSend("reviewsQueue", event);
    }
}
