package org.workswap.core.config.config;

import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.workswap.security.util.QueryCounter;

@Configuration
public class HibernateConfig {

    @Bean
    public HibernatePropertiesCustomizer queryCounterCustomizer(
        QueryCounter queryCounter
    ) {
        return properties -> properties.put(
            "hibernate.session_factory.statement_inspector",
            queryCounter
        );
    }
}