package com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ChangePinRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ForgotPasswordRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ForgotPasswordResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobNoBlockResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtApiResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtIvdDeviceConfigApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtLogOffApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtLogOnApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtPowerUpApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VehicleDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VerifyOtpRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VerifyOtpResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ZoneInfoDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.IVDConfigRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.IVDConfigResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.IvdPingUpdateRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.IvdZoneInfoResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.JobNumberBlockRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MDTLogOnRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MDTLogOnResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MdtChangePinRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MdtForgotPasswordRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MdtForgotPasswordResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MdtLogOffRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MdtLogOffResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MdtPowerUpRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MdtPowerUpResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.VehicleByIvdResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.VerifyOtpForgotPasswordRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.VerifyOtpForgotPasswordResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MdtMapper {

  /**
   * Mapping from vehicleByIvdResponse to VehicleDetailsResponse
   *
   * @param vehicleByIvdResponse vehicleByIvdResponse
   * @return vehicleDetailsResponse
   */
  VehicleDetailsResponse mapToVehicleDetailsResponse(VehicleByIvdResponse vehicleByIvdResponse);

  /**
   * Mapping from ivdZoneInfoResponse to zoneInfoDetailsResponse
   *
   * @param ivdZoneInfoResponse ivdZoneInfoResponse
   * @return zoneInfoDetailsResponse
   */
  ZoneInfoDetailsResponse mapToZoneInfoDetailsResponse(IvdZoneInfoResponse ivdZoneInfoResponse);

  /**
   * map IvdPingUpdateRequest to auto generated IvdPingUpdateRequest
   *
   * @param ivdPingUpdateRequest ivdPingUpdateRequest
   * @return IvdPingUpdateRequest
   */
  IvdPingUpdateRequest mapToIvdPingUpdateRequest(
      com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdPingUpdateRequest ivdPingUpdateRequest);

  @Mapping(source = "mobileNo", target = "mobileNum")
  @Mapping(source = "code", target = "otp")
  VerifyOtpForgotPasswordRequest mapToVerifyOtpForgotPasswordResponse(
      VerifyOtpRequest verifyOtpRequest);

  VerifyOtpResponse mapToVerifyOtpResponse(
      VerifyOtpForgotPasswordResponse verifyOtpForgotPasswordResponse);

  MdtChangePinRequest mapToMdtChangePinRequest(ChangePinRequest changePinRequest);

  MdtForgotPasswordRequest mapToMdtForgotPasswordRequest(
      ForgotPasswordRequest forgotPasswordRequest);

  ForgotPasswordResponse forgotPasswordResponse(
      MdtForgotPasswordResponse mdtForgotPasswordResponse);

  @Mapping(source = "offsetLongitude", target = "longitude")
  @Mapping(source = "offsetLatitude", target = "latitude")
  MdtLogOffRequest mapToMdtLogOffRequest(MdtLogOffApiRequest mdtLogOffAPIRequest);

  @Mapping(source = "vehicleId", target = "vehiclePlateNum")
  MdtApiResponse mdtApiResponse(MdtLogOffResponse mdtLogOffResponse);

  @Mapping(source = "offsetLongitude", target = "xOffset")
  @Mapping(source = "offsetLatitude", target = "yOffset")
  @Mapping(source = "mobileId", target = "ivdNo")
  MDTLogOnRequest mapToMDTLogOnRequest(MdtLogOnApiRequest mdtLogOnAPIRequest);

  @Mapping(source = "product", target = "productlist")
  MdtApiResponse mapToMdtLogOnApiResponse(MDTLogOnResponse mdtLogOnAPIResponseCommand);

  IVDConfigRequest mapToIVDConfigRequest(MdtIvdDeviceConfigApiRequest ivdDeviceConfigAPiRequest);

  MdtApiResponse mapToMdtIVDDeviceConfigApiResponse(
      IVDConfigResponse mdtIVDDeviceConfigAPIResCommand);

  MdtPowerUpRequest mapToMdtPowerUp(MdtPowerUpApiRequest powerUpAPiRequest);

  MdtApiResponse mapToMdtPowerUpApiResponse(MdtPowerUpResponse mdtPowerUpAPIResponseCommand);

  JobNumberBlockRequest mapToJobNoBlockRequest(
      com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobNoBlockRequest jobNoBlockRequest);

  JobNoBlockResponse mapToJobNoBlockResponse(
      com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.JobNumberBlockResponse
          jobNoBlockAPIResponse);
}
