package com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.controller;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.async.RetryTaskExecutor;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.mappers.EventRetryMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.apis.RetryApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.RetryableEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class RetryController implements RetryApi {

  private final RetryTaskExecutor retryTaskExecutor;
  private final EventRetryMapper eventRetryMapper;

  /**
   * This API will be called by event bridge scheduler to perform retries of vehicle comm events
   *
   * @param eventName Events which can be retried (required)
   * @return ResponseEntity<Void>
   */
  @Override
  public ResponseEntity<Void> retryEvents(RetryableEvents eventName) {
    retryTaskExecutor.processRetry(eventRetryMapper.mapToDomainEnum(eventName));
    return ResponseEntity.ok().build();
  }
}
