package com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RetrySchedulerData;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.entities.SchedulerEntity;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    builder = @Builder(disableBuilder = true),
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RetryDataMapper {

  SchedulerEntity retrySchedulerDataToSchedulerEntity(RetrySchedulerData retrySchedulerData);

  RetrySchedulerData schedulerEntityToRetrySchedulerData(SchedulerEntity schedulerEntity);

  default List<SchedulerEntity> retrySchedulerDataToSchedulerEntity(
      List<RetrySchedulerData> retrySchedulerDataList) {
    if (ObjectUtils.isEmpty(retrySchedulerDataList)) {
      return Collections.emptyList();
    }
    return retrySchedulerDataList.stream().map(this::retrySchedulerDataToSchedulerEntity).toList();
  }

  default List<RetrySchedulerData> schedulerEntityToRetrySchedulerData(
      List<SchedulerEntity> schedulerEntityList) {
    if (ObjectUtils.isEmpty(schedulerEntityList)) {
      return Collections.emptyList();
    }
    return schedulerEntityList.stream().map(this::schedulerEntityToRetrySchedulerData).toList();
  }
}
