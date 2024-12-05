package com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.internal;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ChangePinResult;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.FareTariffResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ForgotPasswordResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobNoBlockResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtApiResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtResponseCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VehicleDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VerifyOtpResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbJob;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbVehicle;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.RcsaMessage;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.EmergencyAction;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.MdtAction;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.EmergencyClose;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.GenericEventCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdPingMessageRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.JobDispatchEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleTrackCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VoiceEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.ByteData;

/** Interface to handle jobDispatchEvent */
public interface BeanToByteConverter {

  GenericEventCommand convertToJobModification(JobDispatchEventRequest jobDispatchEventRequest);

  GenericEventCommand convertToByteSyncronizedAutoBid(
      VehicleEventRequest vehicleEventRequest, VehicleDetailsResponse vehicleDetails);

  /**
   * converting to bean to byte
   *
   * @param vehicleTrackCommand vehicleTrackCommand
   * @param isStart isStart - if true then its START else STOP
   * @return genericEventCommand
   */
  GenericEventCommand convertToByteVehicleTrack(
      VehicleTrackCommand vehicleTrackCommand, boolean isStart);

  GenericEventCommand convertToByteVerifyOtp(
      VerifyOtpResponse verifyOtpResponse, EsbVehicle esbVehicle);

  GenericEventCommand convertToByteEmergencyCloseRequest(
      EmergencyAction event, EmergencyClose emergencyClose);

  GenericEventCommand convertToByteCallOut(JobDispatchEventRequest jobDispatchEventRequest);

  /**
   * converting to bean to byte
   *
   * @param voiceEventRequest voiceEventRequest
   * @param isStart isStart - if true then its START else STOP
   * @return genericEventCommand
   */
  GenericEventCommand convertToByteVoiceStreaming(
      VoiceEventRequest voiceEventRequest, boolean isStart);

  GenericEventCommand convertToByteLevyUpdate(JobDispatchEventRequest jobDispatchEventRequest);

  GenericEventCommand convertToBytePingMessage(
      Integer messageId, IvdPingMessageRequest ivdPingMessageRequest);

  GenericEventCommand convertToByteChangePin(
      ChangePinResult changePinResult, int msgId, EsbVehicle esbVehicle);

  GenericEventCommand convertToByteDriverPerformance(
      String driverPerformance, EsbVehicle esbVehicle);

  GenericEventCommand convertToStreetHail(JobDispatchEventRequest jobDispatchEventRequest);

  GenericEventCommand convertToByteFareCalculation(
      ByteData byteData, String ipAddress, FareTariffResponse fareTariffResponse);

  /**
   * converting to bean to byte
   *
   * @param forgotPasswordResponse forgotPasswordResponse
   * @param ivdNo ivdNo
   * @return genericEventCommand
   */
  GenericEventCommand convertToByteForgotPassword(
      ForgotPasswordResponse forgotPasswordResponse, Integer ivdNo);

  GenericEventCommand convertToJobNumberBlock(EsbJob esbJob, JobNoBlockResponse jobNoBlockResponse);

  /**
   * Method to convert to byte for Rcsa Message
   *
   * @param rcsaMessageRequestCommand rcsa request command
   * @return GenericEventCommand
   */
  GenericEventCommand convertToByteRcsaMessage(RcsaMessage rcsaMessageRequestCommand);

  GenericEventCommand convertToJobCancelInIvdResponse(
      JobDispatchEventRequest jobDispatchEventRequest);

  GenericEventCommand convertToJobConfirmation(JobDispatchEventRequest jobDispatchEventRequest);

  /**
   * converting Job offer from bean to byte
   *
   * @param jobDispatchEventRequest jobDispatchEventRequest
   * @return genericEventCommand
   */
  GenericEventCommand convertToJobOffer(JobDispatchEventRequest jobDispatchEventRequest);

  /**
   * This method converts the MdtApiResponses[logon,logoff,IVDHardwareInfo,PowerUp] to Byte
   *
   * @param mdtapiResponse mdtapiResponse
   * @param mdtAction mdtAction
   * @return MdtResponseCommand
   */
  MdtResponseCommand convertMDTResponseToBytes(
      MdtApiResponse mdtapiResponse, MdtAction mdtAction, byte[] ipAddArr, int ivdNo);

  /**
   * Method to formulate the byte message for acknowledgement
   *
   * @param ivdNo - Ivd No
   * @param serialNo - Serial No
   * @param msgId - Message Id
   * @param ipAddress - Ip Address
   * @return Byte msg
   */
  String sendMessageAcknowledgement(Integer ivdNo, String serialNo, String msgId, String ipAddress);
}
