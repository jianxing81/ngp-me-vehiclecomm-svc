package com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ReportTotalMileageRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.UpdateMdtRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.vehicle.client.models.ReportVehicleTotalMileageRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.vehicle.client.models.UpdateMdtInformationRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.vehicle.client.models.UpdateVehicleStateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VehicleMapper {

  UpdateVehicleStateRequest mapToUpdateVehicleStateRequest(
      com.cdg.pmg.ngp.me.vehiclecomm.application.dto.UpdateVehicleStateRequest
          updateVehicleStateRequest);

  UpdateMdtInformationRequest mapToUpdateMdtInformationRequest(UpdateMdtRequest updateMdtRequest);

  ReportVehicleTotalMileageRequest mapToReportVehicleTotalMileageRequest(
      ReportTotalMileageRequest request);
}
