package com.tieto.core.sus;

import com.tieto.core.sus.client.ImdbFeignClient;
import com.tieto.core.sus.config.RabbitConfig;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableDiscoveryClient
@EnableRetry
@EnableConfigurationProperties
@Import(RabbitConfig.class)
@EnableFeignClients(clients = ImdbFeignClient.class)
public class SusApplication {

    public static void main(String[] args) {
        SpringApplication.run(SusApplication.class, args);
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}
