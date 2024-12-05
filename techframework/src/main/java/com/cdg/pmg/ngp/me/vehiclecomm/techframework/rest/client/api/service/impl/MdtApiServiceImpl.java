package com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.client.api.service.impl;

import static java.lang.Boolean.FALSE;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ChangePinRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ChangePinResult;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ForgotPasswordRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ForgotPasswordResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobNoBlockRequest;
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
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.MdtAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.NetworkException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdPingUpdateRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.JsonHelper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.VehicleCommFrameworkConstants;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mappers.MdtMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.apis.MdtControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.BadRequestErrorResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.IVDConfigResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.IVDPingResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.IvdZoneInfoResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MDTLogOnRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MDTLogOnResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MdtChangePinResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MdtChangePinResponseIvdInfo;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MdtForgotPasswordResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MdtLogOffResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MdtPowerUpResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.VehicleByIvdResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.VerifyOtpForgotPasswordRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.VerifyOtpForgotPasswordResponse;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ServiceComponent
@RequiredArgsConstructor
@Slf4j
public class MdtApiServiceImpl implements MdtAPIService {
  private final MdtControllerApi mdtControllerApi;
  private final MdtMapper mdtMapper;
  private final JsonHelper jsonHelper;

  @Override
  public Optional<VehicleDetailsResponse> vehicleDetails(Integer ivdNo, boolean isRegularReport) {
    try {
      if (!isRegularReport) log.info("[vehicleDetails] Request - IVDNo: {} ", ivdNo);
      VehicleByIvdResponse vehicleByIvdResponse = mdtControllerApi.getVehicleByIVDNo(ivdNo).block();
      if (!isRegularReport)
        log.info("[vehicleDetails] Response - {}", jsonHelper.pojoToJson(vehicleByIvdResponse));
      return Optional.ofNullable(mdtMapper.mapToVehicleDetailsResponse(vehicleByIvdResponse));
    } catch (WebClientResponseException e) {
      log.error(
          VehicleCommFrameworkConstants.LOG_BRACKETS_BRACKETS,
          ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode(),
          e.getMessage());
      throw new NetworkException(
          e.getResponseBodyAsString(), ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode());
    }
  }

  @Override
  public Optional<ZoneInfoDetailsResponse> zoneInfoDetails(Integer zoneId) {
    try {
      log.info("[zoneInfoDetails] Request - zoneId: {} ", zoneId);
      IvdZoneInfoResponse zoneInfoDetailsResponse =
          mdtControllerApi.getIvdZoneInfoDetailsByZoneIvdNo(zoneId).block();
      log.info("[zoneInfoDetails] Response - {}", jsonHelper.pojoToJson(zoneInfoDetailsResponse));
      return Optional.ofNullable(mdtMapper.mapToZoneInfoDetailsResponse(zoneInfoDetailsResponse));
    } catch (WebClientResponseException e) {
      log.error(
          VehicleCommFrameworkConstants.LOG_BRACKETS_BRACKETS,
          ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode(),
          e.getMessage());
      throw new NetworkException(
          e.getResponseBodyAsString(), ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode());
    }
  }

  @Override
  public void updateIvdPing(IvdPingUpdateRequest ivdPingUpdateRequest) {
    try {
      log.info("[updateIvdPing] Request - {}", jsonHelper.pojoToJson(ivdPingUpdateRequest));
      IVDPingResponse ivdPingResponse =
          mdtControllerApi
              .mdtIvdPingUpdate(mdtMapper.mapToIvdPingUpdateRequest(ivdPingUpdateRequest))
              .block();
      log.info("[updateIvdPing] Response - {}", jsonHelper.pojoToJson(ivdPingResponse));
    } catch (WebClientResponseException exception) {
      log.error(
          VehicleCommFrameworkConstants.LOG_BRACKETS_BRACKETS,
          ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode(),
          exception.getMessage());
      throw new NetworkException(
          exception.getResponseBodyAsString(), ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode());
    }
  }

  @Override
  public Optional<VerifyOtpResponse> verifyOtpDetails(VerifyOtpRequest verifyOtpRequest) {
    try {
      VerifyOtpForgotPasswordRequest verifyOtpForgotPasswordRequest =
          mdtMapper.mapToVerifyOtpForgotPasswordResponse(verifyOtpRequest);
      log.info(
          "[verifyOtpDetails] Request - {}", jsonHelper.pojoToJson(verifyOtpForgotPasswordRequest));
      VerifyOtpForgotPasswordResponse verifyOtpForgotPasswordResponse =
          mdtControllerApi.mdtVerifyOtp(verifyOtpForgotPasswordRequest).block();
      log.info(
          "[verifyOtpDetails] Response - {}",
          jsonHelper.pojoToJson(verifyOtpForgotPasswordResponse));
      return Optional.ofNullable(mdtMapper.mapToVerifyOtpResponse(verifyOtpForgotPasswordResponse));
    } catch (WebClientResponseException exception) {
      log.error(
          VehicleCommFrameworkConstants.LOG_BRACKETS_BRACKETS,
          ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode(),
          exception.getMessage());
      throw new NetworkException(
          exception.getResponseBodyAsString(), ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode());
    }
  }

  @Override
  public ChangePinResult changePin(ChangePinRequest changePinRequest) {
    boolean changePinResult = false;
    try {
      log.info("[changePin] Request - {}", jsonHelper.pojoToJson(changePinRequest));
      MdtChangePinResponse mdtChangePinResponse =
          mdtControllerApi
              .mdtChangePin(mdtMapper.mapToMdtChangePinRequest(changePinRequest))
              .block();
      log.info("[changePin] Response - {}", jsonHelper.pojoToJson(mdtChangePinResponse));
      changePinResult = true;
      return ChangePinResult.builder()
          .isChangePinResult(changePinResult)
          .ipAddress(
              Optional.ofNullable(mdtChangePinResponse)
                  .map(MdtChangePinResponse::getIvdInfo)
                  .map(MdtChangePinResponseIvdInfo::getIpAddress)
                  .orElse(null))
          .build();
    } catch (WebClientResponseException exception) {
      MdtChangePinResponse pinResponse = exception.getResponseBodyAs(MdtChangePinResponse.class);
      log.info(
          "[changePin] Change PIN response after WebClient exception {}",
          jsonHelper.pojoToJson(pinResponse));
      return ChangePinResult.builder()
          .isChangePinResult(changePinResult)
          .ipAddress(
              Optional.ofNullable(pinResponse)
                  .map(MdtChangePinResponse::getIvdInfo)
                  .map(MdtChangePinResponseIvdInfo::getIpAddress)
                  .orElse(null))
          .build();
    } catch (Exception exception) {
      log.error(exception.getMessage(), ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode());
      throw new NetworkException(ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode());
    }
  }

  /**
   * This method encapsulates the forgotten password function used to call the MDT service.
   *
   * @param forgotPasswordRequest - api request
   * @return ForgotPasswordResponse - api response
   */
  @Override
  public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
    try {
      log.info("[forgotPassword] Request - {}", jsonHelper.pojoToJson(forgotPasswordRequest));
      MdtForgotPasswordResponse mdtForgotPasswordResponse =
          mdtControllerApi
              .mdtForgotPassword(mdtMapper.mapToMdtForgotPasswordRequest(forgotPasswordRequest))
              .block();
      log.info("[forgotPassword] Response - {}", jsonHelper.pojoToJson(mdtForgotPasswordResponse));
      return mdtMapper.forgotPasswordResponse(mdtForgotPasswordResponse);
    } catch (WebClientResponseException exception) {
      log.error(
          VehicleCommFrameworkConstants.LOG_BRACKETS_BRACKETS,
          ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode(),
          exception.getMessage());
      throw new NetworkException(
          exception.getResponseBodyAsString(), ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode());
    }
  }

  /**
   * This Method is to call MDT Service for performing Logoff
   *
   * @param mdtLogOffAPIRequest mdtLogOffRequest
   * @return MDTAPIResponse
   */
  @Override
  public Optional<MdtApiResponse> mdtLogOffAction(MdtLogOffApiRequest mdtLogOffAPIRequest) {
    try {
      log.info("[mdtLogOffAction] Request - {} ", jsonHelper.pojoToJson(mdtLogOffAPIRequest));
      MdtLogOffResponse mdtLogOffResponse =
          mdtControllerApi.mdtLogoff(mdtMapper.mapToMdtLogOffRequest(mdtLogOffAPIRequest)).block();
      log.info("[mdtLogOffAction] Response - {} ", jsonHelper.pojoToJson(mdtLogOffResponse));
      return Optional.ofNullable(mdtMapper.mdtApiResponse(mdtLogOffResponse));
    } catch (WebClientResponseException exception) {
      MdtApiResponse mdtApiResponse;
      BadRequestErrorResponse badRequestErrorResponse =
          exception.getResponseBodyAs(BadRequestErrorResponse.class);
      log.info(
          "[mdtLogOffAction] Response after Web Client exception {} ",
          jsonHelper.pojoToJson(badRequestErrorResponse));
      mdtApiResponse =
          MdtApiResponse.builder()
              .logOffStatus(
                  (badRequestErrorResponse != null && badRequestErrorResponse.getStatus() != null)
                      ? Boolean.valueOf(badRequestErrorResponse.getStatus())
                      : FALSE)
              .build();

      return Optional.of(mdtApiResponse);
    } catch (Exception exception) {
      log.error(exception.getMessage(), ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode());
      throw new NetworkException(ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode());
    }
  }

  @Override
  public Optional<MdtApiResponse> mdtLogOnAction(MdtLogOnApiRequest mdtLogOnAPIRequest) {
    try {
      MDTLogOnRequest mdtLogOnRequest = mdtMapper.mapToMDTLogOnRequest(mdtLogOnAPIRequest);
      log.info("[mdtLogOnAction] Request - {}", jsonHelper.pojoToJson(mdtLogOnRequest));
      MDTLogOnResponse mdtLogOnAPIResponseCommand =
          mdtControllerApi.mdtLogOn(mdtLogOnRequest).block();
      log.info("[mdtLogOnAction] Response - {}", jsonHelper.pojoToJson(mdtLogOnAPIResponseCommand));
      return Optional.ofNullable(mdtMapper.mapToMdtLogOnApiResponse(mdtLogOnAPIResponseCommand));
    } catch (WebClientResponseException exception) {
      MdtApiResponse mdtApiResponse;
      BadRequestErrorResponse badRequestErrorResponse =
          exception.getResponseBodyAs(BadRequestErrorResponse.class);
      log.info(
          "[mdtLogOnAction] Response after web client exception {}",
          jsonHelper.pojoToJson(badRequestErrorResponse));
      mdtApiResponse =
          MdtApiResponse.builder()
              .logonStatus(
                  (badRequestErrorResponse != null && badRequestErrorResponse.getStatus() != null)
                      ? badRequestErrorResponse.getStatus()
                      : "U")
              .build();
      return Optional.of(mdtApiResponse);
    } catch (Exception exception) {
      log.error(exception.getMessage(), ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode());
      throw new NetworkException(ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode());
    }
  }

  /**
   * This Method is to call MDT Service for performing IVDDeviceConfigAction
   *
   * @param ivdDeviceConfigAPiRequest ivdDeviceConfigAPiRequest
   * @return MDTAPIResponse
   */
  @Override
  public Optional<MdtApiResponse> mdtIVDDeviceConfigAction(
      MdtIvdDeviceConfigApiRequest ivdDeviceConfigAPiRequest, Integer ivdNo) {
    try {
      log.info(
          "[mdtIVDDeviceConfigAction] Request - IVD Number: {} {} ",
          ivdNo,
          jsonHelper.pojoToJson(ivdDeviceConfigAPiRequest));
      IVDConfigResponse mdtIVDDeviceConfigAPIResCommand =
          mdtControllerApi
              .mdtIvdDeviceConfig(ivdNo, mdtMapper.mapToIVDConfigRequest(ivdDeviceConfigAPiRequest))
              .block();
      log.info(
          "[mdtIVDDeviceConfigAction] Response - {} ",
          jsonHelper.pojoToJson(mdtIVDDeviceConfigAPIResCommand));
      return Optional.ofNullable(
          mdtMapper.mapToMdtIVDDeviceConfigApiResponse(mdtIVDDeviceConfigAPIResCommand));
    } catch (WebClientResponseException exception) {
      return Optional.empty();
    } catch (Exception exception) {
      log.error("{} - {}", ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode(), exception.getMessage());
      throw new NetworkException(ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode());
    }
  }

  /**
   * This Method is to call MDT Service for performing PowerUp
   *
   * @param powerUpAPiRequest powerUpAPiRequest
   * @return MDTAPIResponse
   */
  @Override
  public Optional<MdtApiResponse> mdtPowerUpAction(MdtPowerUpApiRequest powerUpAPiRequest) {
    try {
      log.info("[mdtPowerUpAction] Request - {}", jsonHelper.pojoToJson(powerUpAPiRequest));
      MdtPowerUpResponse mdtPowerUpAPIResponseCommand =
          mdtControllerApi.mdtPowerUp(mdtMapper.mapToMdtPowerUp(powerUpAPiRequest)).block();
      log.info(
          "[mdtPowerUpAction] Response - {}", jsonHelper.pojoToJson(mdtPowerUpAPIResponseCommand));
      return Optional.ofNullable(
          mdtMapper.mapToMdtPowerUpApiResponse(mdtPowerUpAPIResponseCommand));
    } catch (WebClientResponseException exception) {
      MdtApiResponse mdtApiResponse;
      if (exception.getStatusCode().is4xxClientError()) {
        BadRequestErrorResponse badRequestErrorResponse =
            exception.getResponseBodyAs(BadRequestErrorResponse.class);
        log.info(
            "[mdtPowerUpAction] Power up failed response : {} ",
            jsonHelper.pojoToJson(badRequestErrorResponse));
        mdtApiResponse =
            MdtApiResponse.builder()
                .ivdNo(0)
                .reasonCode(
                    (badRequestErrorResponse != null && badRequestErrorResponse.getStatus() != null)
                        ? badRequestErrorResponse.getStatus()
                        : VehicleCommAppConstant.POWERUP_ERR_UNKNOWN)
                .build();
      } else {
        mdtApiResponse =
            MdtApiResponse.builder()
                .ivdNo(0)
                .reasonCode(VehicleCommAppConstant.POWERUP_ERR_UNKNOWN)
                .build();
      }
      return Optional.of(mdtApiResponse);
    } catch (Exception exception) {
      log.error("{} - {}", ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode(), exception.getMessage());
      throw new NetworkException(ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode());
    }
  }

  @NotNull
  private MdtPowerUpResponse setMdtPowerUpAPIResponseCommand(
      MdtPowerUpApiRequest powerUpAPiRequest, String ipAddress) {
    MdtPowerUpResponse mdtPowerUpAPIResponse = new MdtPowerUpResponse();
    mdtPowerUpAPIResponse.setIpAddress(
        Objects.requireNonNullElse(ipAddress, VehicleCommFrameworkConstants.DEFAULT_IP));
    return mdtPowerUpAPIResponse;
  }

  @NotNull
  private static MDTLogOnResponse setMdtLogOnAPIResponseCommand(
      MdtLogOnApiRequest mdtLogOnAPIRequest, String ipAddress) {
    MDTLogOnResponse mdtLogOnAPIResCommand = new MDTLogOnResponse();
    mdtLogOnAPIResCommand.setLogonStatus(String.valueOf(FALSE));
    mdtLogOnAPIResCommand.setDriverId(mdtLogOnAPIRequest.getDriverId());
    mdtLogOnAPIResCommand.setIpAddress(
        Objects.requireNonNullElse(ipAddress, VehicleCommFrameworkConstants.DEFAULT_IP));
    return mdtLogOnAPIResCommand;
  }

  @NotNull
  private static MdtLogOffResponse setMdtLogOffAPIResponseCommand(
      MdtLogOffApiRequest mdtLogOffAPIRequest, String ipAddress) {
    MdtLogOffResponse mdtLogOffAPIResCommand = new MdtLogOffResponse();
    mdtLogOffAPIResCommand.setDriverId(mdtLogOffAPIRequest.getDriverId());
    mdtLogOffAPIResCommand.setLogOffStatus(FALSE);
    mdtLogOffAPIResCommand.setIvdNo(mdtLogOffAPIRequest.getIvdNo());
    mdtLogOffAPIResCommand.setIpAddress(
        Objects.requireNonNullElse(ipAddress, VehicleCommFrameworkConstants.DEFAULT_IP));
    return mdtLogOffAPIResCommand;
  }

  /**
   * This method is used to call job no block api
   *
   * @param jobNoBlockRequest jobNoBlockRequest
   * @return JobNoBlockResponse - get job no api response
   */
  @Override
  public Optional<JobNoBlockResponse> getJobNoBlock(JobNoBlockRequest jobNoBlockRequest) {
    try {
      log.info("[getJobNoBlock] Request - {}", jsonHelper.pojoToJson(jobNoBlockRequest));
      com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.JobNumberBlockResponse
          jobNoBlockAPIResponse =
              mdtControllerApi
                  .jobNoBlockRequest(mdtMapper.mapToJobNoBlockRequest(jobNoBlockRequest))
                  .block();
      log.info("[getJobNoBlock] Response - {}", jsonHelper.pojoToJson(jobNoBlockAPIResponse));
      return Optional.ofNullable(mdtMapper.mapToJobNoBlockResponse(jobNoBlockAPIResponse));
    } catch (WebClientResponseException exception) {
      log.error(
          VehicleCommFrameworkConstants.LOG_BRACKETS_BRACKETS,
          ErrorCode.JOB_DISPATCH_SERVICE_NETWORK_ERROR.getCode(),
          exception.getMessage());
      throw new NetworkException(
          exception.getResponseBodyAsString(),
          ErrorCode.JOB_DISPATCH_SERVICE_NETWORK_ERROR.getCode());
    }
  }
}
