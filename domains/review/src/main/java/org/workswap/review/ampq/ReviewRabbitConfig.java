package org.workswap.review.ampq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReviewRabbitConfig {

    @Bean
    public Queue reviewsQueue() {
        return new Queue("reviewsQueue", true);
    }
}
