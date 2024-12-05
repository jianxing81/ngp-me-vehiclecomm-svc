package com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs.properties;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "vehcomm.connection.pool")
@ToString
public class ConnectionPoolProperties {
  private Integer maxConnections;
  private Duration pendingAcquireTimeout;
  private Integer connectionTimeout;
  private Integer readTimeout;
  private Integer writeTimeout;
  private Duration disposeInterval;
  private Duration poolInactivity;
  private Duration maxIdleTime;
  private Duration evictDuration;
  private Integer pendingAcquireMaxCount;
}
