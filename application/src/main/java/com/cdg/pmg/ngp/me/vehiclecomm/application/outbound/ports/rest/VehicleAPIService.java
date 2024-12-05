package com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ReportTotalMileageRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.UpdateMdtRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.UpdateVehicleStateRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VehicleEvent;

/* Internal class for wrapper API calls to Vehicle Service*/
public interface VehicleAPIService {
  /**
   * This method is used to call job dispatch service
   *
   * @param vehicleId - api request
   * @param event event
   * @param updateVehicleStateRequest updateVehicleStateRequest
   */
  void updateVehicleState(
      String vehicleId, VehicleEvent event, UpdateVehicleStateRequest updateVehicleStateRequest);

  /**
   * This method is used to call vehicle service
   *
   * @param vehicleId - api request
   * @param event event
   * @param updateVehicleStateRequest updateVehicleStateRequest
   */
  void updateVehicleStateForPowerUp(
      String vehicleId, VehicleEvent event, UpdateVehicleStateRequest updateVehicleStateRequest);

  /**
   * This method is used to call vehicle service
   *
   * @param vehicleId vehicleId
   * @param updateMdtRequest updateMdtRequest
   */
  void updateVehicleMdt(String vehicleId, UpdateMdtRequest updateMdtRequest);

  /**
   * This method is used to call vehicle service to report total mileage
   *
   * @param vehicleId vehicleId
   * @param request ReportTotalMileageRequest
   */
  void reportTotalMileage(String vehicleId, ReportTotalMileageRequest request);

  /**
   * This method is used to call vehicle service
   *
   * @param vehicleId - api request
   * @param event event
   * @param updateVehicleStateRequest updateVehicleStateRequest
   */
  void updateIVDVehicleState(
      String vehicleId, String event, UpdateVehicleStateRequest updateVehicleStateRequest);
}
