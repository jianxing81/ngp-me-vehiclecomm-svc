package com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.persistence;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RetrySchedulerData;
import java.util.List;

public interface SchedulerRepositoryOutboundPort {

  /**
   * This method checks if there is an entry corresponding to the retry ID and updates the
   * updated_dt column.
   *
   * @param retryId retryId
   */
  void modifyUpdatedDate(long retryId);

  /**
   * This method saves the event to be retried in vehicle_comm_scheduler table
   *
   * @param retrySchedulerData retrySchedulerData
   */
  void insertEntity(RetrySchedulerData retrySchedulerData);

  /**
   * This method returns all the retry scheduler data corresponding to the event ID passed
   *
   * @param eventName eventName
   * @return List<RetrySchedulerData>
   */
  List<RetrySchedulerData> findAllEntitiesByEventName(String eventName);

  /**
   * This method deletes all retry entities corresponding to the IDs passed
   *
   * @param retryIds retryIds
   */
  void deleteEntitiesByIds(List<Long> retryIds);

  /**
   * This method updates the retry entities passed with the data passed in the request
   *
   * @param retrySchedulerDataList retrySchedulerDataList
   */
  void updateEntities(List<RetrySchedulerData> retrySchedulerDataList);

  /**
   * This method updates the entity status to success corresponding to the event ID passed
   *
   * @param retryId retryId
   */
  void updateEntityStatusToSuccess(long retryId);
}
