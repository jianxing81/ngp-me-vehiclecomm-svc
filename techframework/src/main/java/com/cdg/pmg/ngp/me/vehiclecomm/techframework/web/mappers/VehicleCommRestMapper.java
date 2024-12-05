package com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ChangePinResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.DriverPerformanceHistoryResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.FareTariffResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ForgotPasswordResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobNoBlockResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtApiResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtRequestCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtResponseCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VehicleDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VerifyOtpResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.EmergencyAction;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.MdtAction;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VehicleTrack;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VoiceStream;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.EmergencyClose;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleTrackCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VoiceEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.client.api.models.ChangePinApiResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.client.api.models.DriverPerformanceHistoryAPIResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.client.api.models.FareTariffAPIResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.client.api.models.ForgotPasswordAPIResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.client.api.models.JobNoBlockAPIResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.client.api.models.MDTIVDDeviceConfigAPIResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.client.api.models.MdtLogOffApiResponseCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.client.api.models.MdtLogOnApiResponseCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.client.api.models.MdtPowerUpApiResponseCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.client.api.models.VehicleDetailsAPIResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.client.api.models.VerifyOtpAPIResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.EmergencyActionEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.EmergencyCloseRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.MDTActionEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.MdtRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.MdtResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.VehicleTrackEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.VehicleTrackRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.VoiceStreamEnum;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/** Mapping the request payload from techFramework to application */
@Mapper(
    builder = @Builder(disableBuilder = true),
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VehicleCommRestMapper {
  /**
   * @param vehicleDetailsAPIResponse VehicleDetailsAPIResponse
   * @return VehicleDetailsResponse
   */
  VehicleDetailsResponse mapToSearchFareBreakdownInboundResponse(
      VehicleDetailsAPIResponse vehicleDetailsAPIResponse);

  /**
   * Mapping to VehicleTrackCommand from VehicleTRackRequest
   *
   * @param vehicleTrackRequest vehicleTrackRequest
   * @return vehicleTrackCommand
   */
  VehicleTrackCommand mapToVehicleTrackCommand(VehicleTrackRequest vehicleTrackRequest);

  /**
   * This is to map auto generated vehicleTrackEvent from techFramework to VehicleTrack in domain
   *
   * @param event whether it is START or STOP
   * @return vehicleTrack
   */
  VehicleTrack maptToVehicleTrack(VehicleTrackEnum event);

  /**
   * Method to map emergency close request dto
   *
   * @param emergencyCloseRequest request dto
   * @return EmergencyClose
   */
  EmergencyClose mapEmergencyCloseRequest(EmergencyCloseRequest emergencyCloseRequest);

  /**
   * Method to map emergency action to enum
   *
   * @param emergencyActionEnum -enum value
   * @return EmergencyAction
   */
  EmergencyAction mapEmergencyAction(EmergencyActionEnum emergencyActionEnum);

  VerifyOtpResponse mapToVerifyOtpInboundResponse(VerifyOtpAPIResponse verifyOtpAPIResponse);

  ChangePinResponse mapToChangePinInboundResponse(ChangePinApiResponse changePinApiResponse);

  /**
   * Method used to map VoiceEventRequestDto to VoiceEventRequest
   *
   * @param voiceEventRequest VoiceEventRequest
   * @return VoiceEventRequest
   */
  VoiceEventRequest mapVoiceEventRequestToCommand(
      com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.VoiceEventRequest
          voiceEventRequest);

  /**
   * Method used to map Event to VoiceStream
   *
   * @param event event
   * @return VoiceStream
   */
  VoiceStream mapToVoiceStreamEvent(VoiceStreamEnum event);

  /**
   * Method used to map Event to DriverPerformanceHistoryResponse
   *
   * @param response
   * @return DriverPerformanceHistoryResponse
   */
  List<DriverPerformanceHistoryResponse> mapToDriverPerformanceHistoryResponse(
      List<DriverPerformanceHistoryAPIResponse> response);

  /**
   * Method used to map FareTariffAPIResponse to FareTariffResponse
   *
   * @param fareTariffAPIResponse FareTariffAPIResponse
   * @return FareTariffResponse
   */
  FareTariffResponse mapToFareTariffInboundResponse(FareTariffAPIResponse fareTariffAPIResponse);

  /**
   * Method used to map ForgotPasswordAPIResponse to ForgotPasswordResponse
   *
   * @param forgotPasswordAPIResponse forgotPasswordAPIResponse
   * @return ForgotPasswordResponse
   */
  ForgotPasswordResponse mapToForgotPasswordResponse(
      ForgotPasswordAPIResponse forgotPasswordAPIResponse);

  /**
   * @param jobNoBlockAPIResponse jobNoBlockAPIResponse
   * @return JobNoBlockResponse
   */
  JobNoBlockResponse mapToJobNoBlockResponse(JobNoBlockAPIResponse jobNoBlockAPIResponse);

  /**
   * @param mdtRequest mdtRequest
   * @return MdtRequestCommand
   */
  MdtRequestCommand mapToMDTRequestCommand(MdtRequest mdtRequest);

  /**
   * Method to map MdtResponseCommand to MdtResponse
   *
   * @param mdtResponseCommand mdtResponseCommand
   * @return MdtResponse MdtResponse
   */
  MdtResponse mapToMDTResponse(MdtResponseCommand mdtResponseCommand);

  /**
   * Maps the data from mdtLOGOffAPIResponseCommand to MDTAPIResponse
   *
   * @param mdtLOGOffAPIResponseCommand mdtLOGOffAPIResponseCommand
   * @return MDTAPIResponse
   */
  MdtApiResponse mapToMdtLogOffApiResponse(MdtLogOffApiResponseCommand mdtLOGOffAPIResponseCommand);

  /**
   * Maps th data from MdtLogOnApiResponseCommand to MDTAPIResponse
   *
   * @param mdtLOGOnAPIResponseCommand mdtLOGOnAPIResponseCommand
   * @return MDTAPIResponse
   */
  MdtApiResponse mapToMdtLogOnApiResponse(MdtLogOnApiResponseCommand mdtLOGOnAPIResponseCommand);

  /**
   * Map the data from mdtIVDDeviceConfigAPIResponseCommand to MDTAPIResponse
   *
   * @param mdtIVDDeviceConfigAPIResponseCommand mdtIVDDeviceConfigAPIResponseCommand
   * @return MDTAPIResponse
   */
  MdtApiResponse mapToMdtIVDDeviceConfigApiResponse(
      MDTIVDDeviceConfigAPIResponse mdtIVDDeviceConfigAPIResponseCommand);

  /**
   * Map the data from mdtPowerUpAPIResponseCommand to MDTAPIResponse
   *
   * @param mdtPowerUpAPIResponseCommand mdtPowerUpAPIResponseCommand
   * @return MDTAPIResponse
   */
  @Mapping(source = "mdtPowerUpAPIResponseCommand.ivdNo", target = "ivdNo")
  MdtApiResponse mapToMdtPowerUpApiResponse(
      MdtPowerUpApiResponseCommand mdtPowerUpAPIResponseCommand);

  /**
   * Map the data from MDTActionEnum to MDTAction
   *
   * @param mdtAction mdtAction
   * @return MDTAction
   */
  MdtAction mapToMDTAction(MDTActionEnum mdtAction);
}
