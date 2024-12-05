package com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.client.api.service.impl;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ReportTotalMileageRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.UpdateMdtRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.UpdateVehicleStateRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.VehicleAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.NetworkException;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.JsonHelper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.VehicleCommFrameworkConstants;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mappers.VehicleMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.vehicle.client.apis.VehicleManagementApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/** Class for calling Vehicle service */
@RequiredArgsConstructor
@ServiceComponent
@Slf4j
public class VehicleAPIServiceImpl implements VehicleAPIService {
  private final VehicleManagementApi vehicleManagementApi;

  @Qualifier("vehicleManagementRegularReportApi")
  private final VehicleManagementApi vehicleManagementRegularReportApi;

  private final VehicleMapper vehicleMapper;
  private final JsonHelper jsonHelper;

  /**
   * This method is used to update vehicle state
   *
   * @param vehicleId vehicleId
   * @param event event
   * @param updateVehicleStateRequest updateVehicleStateRequest
   */
  @Override
  public void updateVehicleState(
      String vehicleId, VehicleEvent event, UpdateVehicleStateRequest updateVehicleStateRequest) {
    try {
      log.info(
          "[updateVehicleState] Request - vehicleId: {} vehicleEvent {} {}",
          vehicleId,
          event,
          jsonHelper.pojoToJson(updateVehicleStateRequest));
      vehicleManagementApi
          .updateVehicleState(
              vehicleId,
              event.getValue(),
              vehicleMapper.mapToUpdateVehicleStateRequest(updateVehicleStateRequest))
          .block();
    } catch (WebClientResponseException exception) {
      log.error(
          VehicleCommFrameworkConstants.LOG_BRACKETS_BRACKETS,
          ErrorCode.VEHICLE_SERVICE_NETWORK_ERROR.getCode(),
          exception.getMessage());
      throw new NetworkException(
          exception.getResponseBodyAsString(), ErrorCode.VEHICLE_SERVICE_NETWORK_ERROR.getCode());
    }
  }

  /**
   * This method is used to update vehicle state
   *
   * @param vehicleId vehicleId
   * @param event event
   * @param updateVehicleStateRequest updateVehicleStateRequest
   */
  @Override
  public void updateVehicleStateForPowerUp(
      String vehicleId, VehicleEvent event, UpdateVehicleStateRequest updateVehicleStateRequest) {
    try {
      log.info(
          "[updateVehicleStateForPowerUp] Request - vehicleId: {} vehicleEvent {} {}",
          vehicleId,
          event,
          jsonHelper.pojoToJson(updateVehicleStateRequest));
      vehicleManagementApi
          .updateVehicleState(
              vehicleId,
              event.getValue(),
              vehicleMapper.mapToUpdateVehicleStateRequest(updateVehicleStateRequest))
          .block();
    } catch (WebClientResponseException exception) {
      /* According to https://comfortdelgrotaxi.atlassian.net/browse/ME-2052.
      The logic behind creating this the final bytearray remains unaffected
      by the outcome of the vehicle-service API response associated with this ticket. */
      log.error(
          VehicleCommFrameworkConstants.LOG_BRACKETS_BRACKETS,
          ErrorCode.VEHICLE_SERVICE_NETWORK_ERROR.getCode(),
          exception.getMessage());
    }
  }

  /**
   * This method is used to update vehicle mdt
   *
   * @param vehicleId vehicleId
   * @param updateMdtRequest updateMdtRequest
   */
  @Override
  public void updateVehicleMdt(String vehicleId, UpdateMdtRequest updateMdtRequest) {
    try {
      log.info(
          "[updateVehicleMdt] Request - vehicleId: {} {}",
          vehicleId,
          jsonHelper.pojoToJson(updateMdtRequest));
      vehicleManagementApi
          .updateMdtInformation(
              vehicleId, vehicleMapper.mapToUpdateMdtInformationRequest(updateMdtRequest))
          .block();
    } catch (WebClientResponseException exception) {
      log.error(
          VehicleCommFrameworkConstants.LOG_BRACKETS_BRACKETS,
          ErrorCode.VEHICLE_SERVICE_NETWORK_ERROR.getCode(),
          exception.getMessage());
      throw new NetworkException(
          exception.getResponseBodyAsString(), ErrorCode.VEHICLE_SERVICE_NETWORK_ERROR.getCode());
    }
  }

  @Override
  public void reportTotalMileage(String vehicleId, ReportTotalMileageRequest request) {
    try {
      log.info(
          "[reportTotalMileage] Request - vehicleId {} {}",
          vehicleId,
          jsonHelper.pojoToJson(request));
      vehicleManagementApi
          .reportVehicleTotalMileage(
              vehicleId, vehicleMapper.mapToReportVehicleTotalMileageRequest(request))
          .block();
    } catch (WebClientResponseException exception) {
      log.error(
          VehicleCommFrameworkConstants.LOG_BRACKETS_BRACKETS,
          ErrorCode.VEHICLE_SERVICE_NETWORK_ERROR.getCode(),
          exception.getMessage());
      throw new NetworkException(
          exception.getResponseBodyAsString(), ErrorCode.VEHICLE_SERVICE_NETWORK_ERROR.getCode());
    }
  }

  /**
   * This method is used to update vehicle state
   *
   * @param vehicleId vehicleId
   * @param event event
   * @param updateVehicleStateRequest updateVehicleStateRequest
   */
  @Override
  public void updateIVDVehicleState(
      String vehicleId, String event, UpdateVehicleStateRequest updateVehicleStateRequest) {
    try {
      log.debug(
          "[updateIVDVehicleState] Request - VehicleId: {} Event: {} {}",
          vehicleId,
          event,
          jsonHelper.pojoToJson(updateVehicleStateRequest));
      vehicleManagementRegularReportApi
          .updateVehicleState(
              vehicleId,
              event,
              vehicleMapper.mapToUpdateVehicleStateRequest(updateVehicleStateRequest))
          .block();
    } catch (WebClientResponseException exception) {
      log.error(
          VehicleCommFrameworkConstants.LOG_BRACKETS_BRACKETS,
          ErrorCode.VEHICLE_SERVICE_NETWORK_ERROR.getCode(),
          exception.getMessage());
      throw new NetworkException(
          exception.getResponseBodyAsString(), ErrorCode.VEHICLE_SERVICE_NETWORK_ERROR.getCode());
    }
  }
}
