package com.tieto.core.sus;

import com.tieto.core.sus.config.RabbitConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableDiscoveryClient
@EnableRetry
@EnableConfigurationProperties
@Import(RabbitConfig.class)
public class SusApplication {

	public static void main(String[] args) {
		SpringApplication.run(SusApplication.class, args);
	}

}
