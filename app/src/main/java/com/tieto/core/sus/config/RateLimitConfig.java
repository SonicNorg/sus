package com.tieto.core.sus.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("service.sus")
@Configuration
@RefreshScope
public class RateLimitConfig {
    @Value("${rateLimitPerSecond:10}")
    private int rateLimitPerSecond;

    public int getRateLimitPerSecond() {
        return rateLimitPerSecond;
    }
}
