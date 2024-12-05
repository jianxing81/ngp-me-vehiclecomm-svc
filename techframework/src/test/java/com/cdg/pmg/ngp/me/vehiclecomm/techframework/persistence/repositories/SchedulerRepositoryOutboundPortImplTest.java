package com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.repositories;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RetrySchedulerData;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.JsonHelper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.adapters.SchedulerRepositoryOutboundPortImpl;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.entities.SchedulerEntity;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.mappers.RetryDataMapper;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataAccessException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SchedulerRepositoryOutboundPortImplTest {

  @InjectMocks SchedulerRepositoryOutboundPortImpl schedulerRepositoryOutboundPort;

  @Mock SchedulerRepository schedulerRepository;

  @Mock JsonHelper jsonHelper;

  @Mock RetryDataMapper retryDataMapper;

  @Test
  void shouldThrowDataAccessExceptionInModifyUpdatedDate() {
    when(schedulerRepository.findById(anyLong()))
        .thenThrow(new DataAccessException("DatabaseError") {});
    when(jsonHelper.exceptionToJsonString(any(Exception.class))).thenReturn("");

    assertDoesNotThrow(() -> schedulerRepositoryOutboundPort.modifyUpdatedDate(1L));
  }

  @Test
  void shouldThrowDataAccessExceptionInInsertEntity() {
    when(retryDataMapper.retrySchedulerDataToSchedulerEntity(any(RetrySchedulerData.class)))
        .thenReturn(new SchedulerEntity());
    when(schedulerRepository.save(any(SchedulerEntity.class)))
        .thenThrow(new DataAccessException("DatabaseError") {});
    when(jsonHelper.pojoToJson(any(Exception.class))).thenReturn("");
    assertDoesNotThrow(
        () -> schedulerRepositoryOutboundPort.insertEntity(new RetrySchedulerData()));
  }

  @Test
  void shouldThrowDataAccessExceptionInFindAllEntities() {

    when(schedulerRepository.findByEventName(anyString()))
        .thenThrow(new DataAccessException("DatabaseError") {});
    when(jsonHelper.exceptionToJsonString(any(Exception.class))).thenReturn("");
    assertDoesNotThrow(() -> schedulerRepositoryOutboundPort.findAllEntitiesByEventName(""));
  }

  @Test
  void shouldThrowDataAccessExceptionInDeleteByIds() {
    doThrow(new DataAccessException("DatabaseError") {})
        .when(schedulerRepository)
        .deleteAllById(anyList());
    when(jsonHelper.exceptionToJsonString(any(Exception.class))).thenReturn("");
    assertDoesNotThrow(() -> schedulerRepositoryOutboundPort.deleteEntitiesByIds(List.of(1L)));
  }

  @Test
  void shouldThrowDataAccessExceptionInUpdateEntities() {
    when(retryDataMapper.retrySchedulerDataToSchedulerEntity(anyList()))
        .thenReturn(Collections.emptyList());
    when(jsonHelper.pojoToJson(anyList())).thenReturn("");
    doThrow(new DataAccessException("DatabaseError") {})
        .when(schedulerRepository)
        .saveAll(anyList());
    assertDoesNotThrow(
        () -> schedulerRepositoryOutboundPort.updateEntities(List.of(new RetrySchedulerData())));
  }

  @Test
  void shouldThrowDataAccessExceptionInUpdateEntityStatusToSuccess() {
    when(schedulerRepository.findById(anyLong()))
        .thenThrow(new DataAccessException("DatabaseError") {});
    when(jsonHelper.exceptionToJsonString(any(Exception.class))).thenReturn("");
    assertDoesNotThrow(() -> schedulerRepositoryOutboundPort.updateEntityStatusToSuccess(1L));
  }
}
