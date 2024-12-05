package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.service;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.entities.RetryDomainEntity;

public interface SchedulerTransactionService {

  /**
   * This method will insert or update the retry entries based on the request
   *
   * @param retryDomainEntity retryDomainEntity
   */
  void insertOrUpdateRetryEntity(RetryDomainEntity retryDomainEntity);
}
