package org.workswap.sso.amqp;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserCreateRabbitConfig {

    @Bean
    public Queue reviewsQueue() {
        return new Queue("reviewsQueue", true);
    }
}
