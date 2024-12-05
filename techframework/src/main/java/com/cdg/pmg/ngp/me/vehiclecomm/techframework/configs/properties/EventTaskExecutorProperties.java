package com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs.properties;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "task.execution")
@Setter
@Getter
public class EventTaskExecutorProperties {
  private ExecutorConfig ivdJobEvent;
  private ExecutorConfig ivdVehicleEvent;
  private ExecutorConfig ivdRcsaEvent;
  private ExecutorConfig ivdRegularReportEvent;
  private ExecutorConfig commonEvent;
  private ExecutorConfig retryEvent;

  @Setter
  @Getter
  public static class ExecutorConfig {
    private int coreSize;
    private int maxSize;
    private int queueCapacity;
    private Duration keepAlive;
    private boolean awaitTermination;
    private Duration awaitTerminationPeriod;
  }
}
