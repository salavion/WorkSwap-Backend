package org.workswap.statistic.ampq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue reviewsQueue() {
        return new Queue("reviewsQueue", true);
    }

    @Bean
    public Queue onlineStatQueue() {
        return new Queue("onlineStatQueue", true);
    }

    @Bean
    public Queue usersStatQueue() {
        return new Queue("usersStatQueue", true);
    }

    @Bean
    public Queue listingViewQueue() {
        return new Queue("listingViewQueue", true);
    }

    @Bean
    public Queue siteViewQueue() {
        return new Queue("siteViewQueue", true);
    }

    @Bean
    public Queue listinsStatQueue() {
        return new Queue("listinsStatQueue", true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplateCustomizer rabbitTemplateCustomizer(@NonNull MessageConverter messageConverter) {
        return template -> template.setMessageConverter(messageConverter);
    }
}
