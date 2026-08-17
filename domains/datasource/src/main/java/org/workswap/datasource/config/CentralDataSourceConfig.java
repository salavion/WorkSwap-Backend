package org.workswap.datasource.config;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = {
        "org.workswap.chat",
        "org.workswap.forum",
        "org.workswap.listing",
        "org.workswap.location",
        "org.workswap.notification",
        "org.workswap.order",
        "org.workswap.review",
        "org.workswap.subscription",
        "org.workswap.task",
        "org.workswap.user"
    }, // Пакет основной БД
    entityManagerFactoryRef = "centralEntityManagerFactory",
    transactionManagerRef = "centralTransactionManager"
)
@Profile({"production", "statistic"})
public class CentralDataSourceConfig {

    @PostConstruct
    public void init() {
        System.out.println(">>> CentralDataSourceConfig LOADED");
    }

    @Value("${spring.jpa.properties.hibernate.dialect}")
    private String hibernateDialect;

    @Primary
    @Bean
    @ConfigurationProperties("spring.central-datasource")
    public DataSource centralDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean centralEntityManagerFactory(
            EntityManagerFactoryBuilder builder
    ) {
        return builder
                .dataSource(centralDataSource())
                .packages(
                    "org.workswap.chat",
                    "org.workswap.forum",
                    "org.workswap.listing",
                    "org.workswap.location",
                    "org.workswap.notification",
                    "org.workswap.order",
                    "org.workswap.review",
                    "org.workswap.subscription",
                    "org.workswap.task",
                    "org.workswap.user"
                )
                .persistenceUnit("central")
                .properties(Map.of(
                    "hibernate.dialect", hibernateDialect
                ))
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager centralTransactionManager(
            @Qualifier("centralEntityManagerFactory") EntityManagerFactory emf
    ) {

        if (emf == null) {
            throw new IllegalArgumentException("EntityManagerFactory не найден (null)");
        }
        
        return new JpaTransactionManager(emf);
    }
}