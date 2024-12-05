package com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.adapters;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RetrySchedulerData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.persistence.SchedulerRepositoryOutboundPort;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.JsonHelper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.entities.SchedulerEntity;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.enums.RetryStatus;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.mappers.RetryDataMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.repositories.SchedulerRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerRepositoryOutboundPortImpl implements SchedulerRepositoryOutboundPort {

  private final SchedulerRepository schedulerRepository;
  private final RetryDataMapper retryDataMapper;
  private final JsonHelper jsonHelper;

  /**
   * This method checks if there is an entry corresponding to the retry ID and updates the
   * updated_dt column.
   *
   * @param retryId retryId
   */
  @Override
  public void modifyUpdatedDate(long retryId) {
    try {
      schedulerRepository
          .findById(retryId)
          .ifPresent(
              entity -> {
                entity.setUpdatedDt(LocalDateTime.now());
                schedulerRepository.save(entity);
              });

      log.info("[modifyUpdatedDate] Updated retry entry with ID {}", retryId);
    } catch (DataAccessException ex) {
      log.error("[modifyUpdatedDate] {}", jsonHelper.exceptionToJsonString(ex));
    }
  }

  /**
   * This method saves the event to be retried in vehicle_comm_scheduler table
   *
   * @param retrySchedulerData retrySchedulerData
   */
  @Override
  public void insertEntity(RetrySchedulerData retrySchedulerData) {
    try {
      log.info(
          "[insertRetryData] Retry data insert request {}",
          jsonHelper.pojoToJson(retrySchedulerData));
      SchedulerEntity schedulerEntity =
          retryDataMapper.retrySchedulerDataToSchedulerEntity(retrySchedulerData);
      schedulerRepository.save(schedulerEntity);
    } catch (DataAccessException ex) {
      log.error("[insertRetryData] {}", jsonHelper.exceptionToJsonString(ex));
    }
  }

  /**
   * This method returns all the retry scheduler data corresponding to the event ID passed
   *
   * @param eventName eventName
   * @return List<RetrySchedulerData>
   */
  @Override
  public List<RetrySchedulerData> findAllEntitiesByEventName(String eventName) {
    try {
      List<SchedulerEntity> schedulerEntityList = schedulerRepository.findByEventName(eventName);
      log.info(
          "[findAllEntitiesByEventName] Scheduler entities from table {}",
          jsonHelper.pojoToJson(schedulerEntityList));
      return retryDataMapper.schedulerEntityToRetrySchedulerData(schedulerEntityList);
    } catch (DataAccessException ex) {
      log.error("[findAllEntitiesByEventName] {}", jsonHelper.exceptionToJsonString(ex));
    }
    return Collections.emptyList();
  }

  /**
   * This method deletes all retry entities corresponding to the IDs passed
   *
   * @param retryIds retryIds
   */
  @Override
  public void deleteEntitiesByIds(List<Long> retryIds) {
    log.info("[deleteEntitiesByIds] Scheduler IDs to delete {}", retryIds);
    try {
      schedulerRepository.deleteAllById(retryIds);
    } catch (DataAccessException ex) {
      log.error("[deleteEntitiesByIds] {}", jsonHelper.exceptionToJsonString(ex));
    }
  }

  /**
   * This method updates the retry entities passed with the data passed in the request
   *
   * @param retrySchedulerDataList retrySchedulerDataList
   */
  @Override
  public void updateEntities(List<RetrySchedulerData> retrySchedulerDataList) {
    try {
      List<SchedulerEntity> schedulerEntityList =
          retryDataMapper.retrySchedulerDataToSchedulerEntity(retrySchedulerDataList);
      log.info(
          "[updateEntities] Scheduler entities to update {}",
          jsonHelper.pojoToJson(retrySchedulerDataList));
      schedulerRepository.saveAll(schedulerEntityList);
    } catch (DataAccessException ex) {
      log.error("[updateEntities] {}", jsonHelper.exceptionToJsonString(ex));
    }
  }

  /**
   * This method updates the entity status to success corresponding to the event ID passed
   *
   * @param retryId retryId
   */
  @Override
  public void updateEntityStatusToSuccess(long retryId) {
    try {
      schedulerRepository
          .findById(retryId)
          .ifPresent(
              entity -> {
                entity.setStatus(RetryStatus.SUCCESS);
                schedulerRepository.save(entity);
              });
      log.info("[updateEntityStatusToSuccess] Entity with ID {} marked as SUCCESS", retryId);
    } catch (DataAccessException ex) {
      log.error("[updateEntityStatusToSuccess] {}", jsonHelper.exceptionToJsonString(ex));
    }
  }
}
