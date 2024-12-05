package com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.client.api.service.impl;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.DriverPerformanceHistoryResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.FleetAnalyticsAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.NetworkException;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.JsonHelper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.VehicleCommFrameworkConstants;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fleetAnalytic.client.apis.DriverPerformanceServiceApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fleetAnalytic.client.models.DriverPerformanceHistoryData;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mappers.FleetAnalyticMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/** Class for calling fleetAnalytic service */
@RequiredArgsConstructor
@ServiceComponent
@Slf4j
public class FleetAnalyticAPIServiceImpl implements FleetAnalyticsAPIService {
  private final DriverPerformanceServiceApi driverPerformanceServiceApi;
  private final FleetAnalyticMapper fleetAnalyticMapper;
  private final JsonHelper jsonHelper;

  @Override
  public List<DriverPerformanceHistoryResponse> getDriverPerformanceHistory(
      String driverId, String startDate) {
    try {
      log.info(
          "[getDriverPerformanceHistory] Request - DriverId: {} StartDate: {}",
          driverId,
          startDate);
      List<DriverPerformanceHistoryData> response =
          driverPerformanceServiceApi
              .getDriverPerformanceHistory(driverId, startDate, null, null)
              .collectList()
              .block();
      log.info("[getDriverPerformanceHistory] Response - {}", jsonHelper.pojoToJson(response));
      return fleetAnalyticMapper.mapToDriverPerformanceHistoryData(response);

    } catch (WebClientResponseException exception) {
      log.error(
          VehicleCommFrameworkConstants.LOG_BRACKETS_BRACKETS,
          ErrorCode.FLEET_ANALYTIC_SERVICE_NETWORK_ERROR.getCode(),
          exception.getMessage());
      throw new NetworkException(
          exception.getResponseBodyAsString(),
          ErrorCode.FLEET_ANALYTIC_SERVICE_NETWORK_ERROR.getCode());
    }
  }
}
