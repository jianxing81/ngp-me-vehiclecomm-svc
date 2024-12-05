package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.impl;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RetrySchedulerData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.port.SchedulerTask;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.EsbJobMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.messaging.VehicleCommChannelProducer;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.persistence.SchedulerRepositoryOutboundPort;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.RetryableEvents;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

@ServiceComponent
@Slf4j
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerTask {

  private final SchedulerRepositoryOutboundPort schedulerRepositoryOutboundPort;
  private final VehicleCommChannelProducer kafkaProducer;
  private final EsbJobMapper esbJobMapper;

  /**
   * This method will process the retry by looking up for entries in retry_scheduler table
   *
   * @param eventName eventName
   */
  @Override
  public void processRetry(RetryableEvents eventName) {

    // Get all entries corresponding to the event name from retry table
    List<RetrySchedulerData> retrySchedulerDataList =
        schedulerRepositoryOutboundPort.findAllEntitiesByEventName(eventName.toString());

    // If no entry in retry table, end the process
    if (ObjectUtils.isEmpty(retrySchedulerDataList)) {
      log.info("[handleRetry] No entry in scheduler table to process");
      return;
    }

    /* If status is success or if the current time is more than 48 hours from created date, we partition the data to true,
      else we partition it to false.
    */
    Map<Boolean, List<RetrySchedulerData>> partitionedSchedulerData =
        retrySchedulerDataList.stream()
            .collect(
                Collectors.partitioningBy(
                    entity -> {
                      LocalDateTime cutOffTime = LocalDateTime.now().minusHours(48);
                      return Objects.equals(
                              RetrySchedulerData.RetryStatus.SUCCESS, entity.getStatus())
                          || cutOffTime.isAfter(entity.getCreatedDt());
                    }));

    List<RetrySchedulerData> entitiesToDelete = partitionedSchedulerData.get(true);

    /* Filter the records which are in the table for more than 48 hours. Update the status of such
      records to FAILED
    */
    List<RetrySchedulerData> entitiesToUpdateStatus =
        entitiesToDelete.stream()
            .filter(
                entity ->
                    ObjectUtils.notEqual(
                        RetrySchedulerData.RetryStatus.SUCCESS, entity.getStatus()))
            .toList();

    if (ObjectUtils.isNotEmpty(entitiesToUpdateStatus)) {
      entitiesToUpdateStatus.forEach(
          entity -> entity.setStatus(RetrySchedulerData.RetryStatus.FAILED));
      schedulerRepositoryOutboundPort.updateEntities(entitiesToUpdateStatus);
    }

    // Delete the entities which are partitioned to true
    if (ObjectUtils.isNotEmpty(entitiesToDelete)) {
      List<Long> retryIdsToDelete =
          entitiesToDelete.stream().map(RetrySchedulerData::getId).toList();
      schedulerRepositoryOutboundPort.deleteEntitiesByIds(retryIdsToDelete);
    }

    List<RetrySchedulerData> entitiesToProcess = partitionedSchedulerData.get(false);

    entitiesToProcess.forEach(this::produceMessageToKafka);
  }

  private void produceMessageToKafka(RetrySchedulerData retrySchedulerData) {
    var ivdMessageRequest = esbJobMapper.retryPayloadToIvdMessageRequest(retrySchedulerData);
    kafkaProducer.sendToIvdJobEvent(ivdMessageRequest);
  }
}
