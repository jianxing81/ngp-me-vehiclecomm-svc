package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.impl;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RetrySchedulerData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.service.SchedulerTransactionService;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.persistence.SchedulerRepositoryOutboundPort;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.entities.RetryDomainEntity;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.RetryableEvents;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ServiceComponent
@Slf4j
@RequiredArgsConstructor
public class SchedulerTransactionServiceImpl implements SchedulerTransactionService {

  private final SchedulerRepositoryOutboundPort schedulerRepositoryOutboundPort;

  /**
   * This method will insert or update the retry entries based on the request
   *
   * @param retryDomainEntity retryDomainEntity
   */
  @Override
  public void insertOrUpdateRetryEntity(RetryDomainEntity retryDomainEntity) {
    if (retryDomainEntity == null) {
      log.info("RetryDomainEntity is null. Hence retry logic is skipped");
      return;
    }
    RetryableEvents eventName =
        RetryableEvents.getEventByEventId(retryDomainEntity.getEventIdentifier());

    // If eventId is not one of RetryableEvents, then end the process
    if (Objects.isNull(eventName)) {
      log.info(
          "[insertOrUpdateRetryEntity] eventId - {} is not available in retryable events",
          retryDomainEntity.getEventIdentifier());
      return;
    }

    /*If retryId is available in the request then update the updated_dt column, otherwise make a
    new entry*/
    Optional.ofNullable(retryDomainEntity.getRetryId())
        .ifPresentOrElse(
            schedulerRepositoryOutboundPort::modifyUpdatedDate,
            () -> buildAndInsertSchedulerData(eventName, retryDomainEntity));
  }

  private void buildAndInsertSchedulerData(
      RetryableEvents eventName, RetryDomainEntity retryDomainEntity) {
    RetrySchedulerData retrySchedulerData =
        RetrySchedulerData.builder()
            .eventName(eventName.toString())
            .status(RetrySchedulerData.RetryStatus.PENDING)
            .payload(
                RetrySchedulerData.Payload.builder()
                    .eventId(retryDomainEntity.getEventId())
                    .occurredAt(retryDomainEntity.getOccurredAt())
                    .eventIdentifier(retryDomainEntity.getEventIdentifier())
                    .eventDate(retryDomainEntity.getEventDate())
                    .message(retryDomainEntity.getMessage())
                    .build())
            .build();
    schedulerRepositoryOutboundPort.insertEntity(retrySchedulerData);
  }
}
