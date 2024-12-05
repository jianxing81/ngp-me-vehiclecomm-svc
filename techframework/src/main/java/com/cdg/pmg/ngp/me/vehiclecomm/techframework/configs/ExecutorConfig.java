package com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs.properties.EventTaskExecutorProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ExecutorConfig {
  private final EventTaskExecutorProperties ivdEventProperties;

  @Bean(name = "ivdJobEventExecutor")
  public ThreadPoolTaskExecutor ivdJobEventExecutor() {
    return createExecutor(ivdEventProperties.getIvdJobEvent());
  }

  @Bean(name = "ivdVehicleEventExecutor")
  public ThreadPoolTaskExecutor ivdVehicleEventExecutor() {
    return createExecutor(ivdEventProperties.getIvdVehicleEvent());
  }

  @Bean(name = "ivdRcsaEventExecutor")
  public ThreadPoolTaskExecutor ivdRcsaEventExecutor() {
    return createExecutor(ivdEventProperties.getIvdRcsaEvent());
  }

  @Bean(name = "ivdRegularReportEventExecutor")
  public ThreadPoolTaskExecutor ivdRegularReportEventExecutor() {
    return createExecutor(ivdEventProperties.getIvdRegularReportEvent());
  }

  @Bean(name = "commonEventExecutor")
  public ThreadPoolTaskExecutor commonEventExecutor() {
    return createExecutor(ivdEventProperties.getCommonEvent());
  }

  @Bean(name = "retryEventExecutor")
  public ThreadPoolTaskExecutor retryEventExecutor() {
    return createExecutor(ivdEventProperties.getRetryEvent());
  }

  private ThreadPoolTaskExecutor createExecutor(
      EventTaskExecutorProperties.ExecutorConfig properties) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(properties.getCoreSize());
    executor.setMaxPoolSize(properties.getMaxSize());
    executor.setQueueCapacity(properties.getQueueCapacity());
    executor.setKeepAliveSeconds((int) properties.getKeepAlive().getSeconds());
    executor.setAwaitTerminationSeconds((int) properties.getAwaitTerminationPeriod().getSeconds());
    executor.setWaitForTasksToCompleteOnShutdown(properties.isAwaitTermination());
    executor.initialize();
    return executor;
  }
}
