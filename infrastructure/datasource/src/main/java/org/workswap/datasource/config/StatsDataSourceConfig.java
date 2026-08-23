package org.workswap.datasource.config;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "org.workswap.statistic",
    entityManagerFactoryRef = "statsEntityManagerFactory",
    transactionManagerRef = "statsTransactionManager"
)
@Profile({"server", "statistic"})
public class StatsDataSourceConfig {

    @PostConstruct
    public void init() {
        System.out.println(">>> StatsDataSourceConfig LOADED");
    }

    @Value("${spring.jpa.properties.hibernate.dialect}")
    private String hibernateDialect;

    @Bean
    @ConfigurationProperties("spring.statistics-datasource")
    public DataSource statsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean statsEntityManagerFactory(
            EntityManagerFactoryBuilder builder
    ) {
        return builder
                .dataSource(statsDataSource())
                .packages("org.workswap.statistic") // Пакет с @Entity статистики
                .persistenceUnit("stats")
                .properties(Map.of(
                    "hibernate.dialect", hibernateDialect
                ))
                .build();
    }

    @Bean
    public PlatformTransactionManager statsTransactionManager(
            @Qualifier("statsEntityManagerFactory") EntityManagerFactory emf
    ) {

        if (emf == null) {
            throw new IllegalArgumentException("EntityManagerFactory не найден (null)");
        }
        
        return new JpaTransactionManager(emf);
    }
}
