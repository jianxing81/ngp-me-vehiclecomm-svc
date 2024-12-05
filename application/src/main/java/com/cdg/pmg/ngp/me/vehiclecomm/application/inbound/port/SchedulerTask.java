package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.port;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.RetryableEvents;

public interface SchedulerTask {

  /**
   * This method will process the retry by looking up for entries in retry_scheduler table
   *
   * @param eventName eventName
   */
  void processRetry(RetryableEvents eventName);
}
