package org.workswap.security.config;

import org.workswap.security.jwt.BaseJwtTokenConverter;
import org.workswap.security.jwt.JwtAuthenticationConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@AutoConfiguration
@Slf4j
@Import(SecurityWebMvcConfig.class)
public class SecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        return new BaseJwtTokenConverter();
    }

    @PostConstruct
    public void init() {
        log.info("securityAutoConfiguration is loaded");
    }
}