package com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest;

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
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdPingUpdateRequest;
import java.util.Optional;

public interface MdtAPIService {

  /**
   * Getting the vehicle details by ivdNo
   *
   * @param ivdNo ivdNo
   * @param isRegularReport
   * @return VehicleDetailsResponse
   */
  Optional<VehicleDetailsResponse> vehicleDetails(Integer ivdNo, boolean isRegularReport);

  /**
   * Getting the zone info details by zoneId
   *
   * @param zoneId zoneId
   * @return zoneInfoDetailsResponse
   */
  Optional<ZoneInfoDetailsResponse> zoneInfoDetails(Integer zoneId);

  void updateIvdPing(IvdPingUpdateRequest ivdPingUpdateRequest);

  Optional<VerifyOtpResponse> verifyOtpDetails(VerifyOtpRequest verifyOtpRequest);

  ChangePinResult changePin(ChangePinRequest changePinRequest);

  /**
   * This method encapsulates the forgotten password function used to call the MDT service.
   *
   * @param forgotPasswordRequest - api request
   * @return ForgotPasswordResponse - api response
   */
  ForgotPasswordResponse forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

  /**
   * This Method is to call MDT Service for performing Logoff
   *
   * @param mdtLogOffAPIRequest mdtLogOffRequest
   * @return MDTAPIResponse
   */
  Optional<MdtApiResponse> mdtLogOffAction(MdtLogOffApiRequest mdtLogOffAPIRequest);

  /**
   * This Method is to call MDT Service for performing Logoff
   *
   * @param mdtLogOnAPIRequest MDTLogOnRequest
   * @return MDTAPIResponse
   */
  Optional<MdtApiResponse> mdtLogOnAction(MdtLogOnApiRequest mdtLogOnAPIRequest);

  /**
   * This Method is to call MDT Service for performing IVDDeviceConfigAction
   *
   * @param ivdDeviceConfigAPiRequest ivdDeviceConfigAPiRequest
   * @return MDTAPIResponse
   */
  Optional<MdtApiResponse> mdtIVDDeviceConfigAction(
      MdtIvdDeviceConfigApiRequest ivdDeviceConfigAPiRequest, Integer ivdNo);

  /**
   * This Method is to call MDT Service for performing PowerUp
   *
   * @param powerUpAPiRequest powerUpAPiRequest
   * @return MDTAPIResponse
   */
  Optional<MdtApiResponse> mdtPowerUpAction(MdtPowerUpApiRequest powerUpAPiRequest);

  /**
   * This method is used to call mdt service
   *
   * @param jobNoBlockRequest - api request
   * @return JobNoBlockResponse
   */
  Optional<JobNoBlockResponse> getJobNoBlock(JobNoBlockRequest jobNoBlockRequest);
}
