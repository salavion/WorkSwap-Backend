package org.workswap.core.config.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "api")
@Getter @Setter
public class ApiConfig {

    private String url;
}
