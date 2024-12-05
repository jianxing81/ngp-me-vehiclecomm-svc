package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka.properties.schema.registry")
@Getter
@Setter
public class SchemaRegistryConfig {
  private String url;
  private String credentialsSource;
  private String keySecret;
}
