package com.cdg.pmg.ngp.me.vehiclecomm.techframework.async;

import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.port.SchedulerTask;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.RetryableEvents;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RetryTaskExecutor {

  private final SchedulerTask schedulerService;

  /**
   * This method is used to asynchronously process retry for the event name passed
   *
   * @param eventName eventName
   */
  @Async("retryEventExecutor")
  @SneakyThrows
  public void processRetry(RetryableEvents eventName) {
    schedulerService.processRetry(eventName);
  }
}
