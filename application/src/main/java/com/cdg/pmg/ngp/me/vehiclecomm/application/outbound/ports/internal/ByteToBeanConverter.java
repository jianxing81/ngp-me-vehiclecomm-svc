package com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.internal;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtIvdDeviceConfigApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtLogOffApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtLogOnApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtPowerUpApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.ByteDataRepresentation;

/*
   This interface is to convert the Byte message to a Java Bean
*/
public interface ByteToBeanConverter {

  /**
   * This is a generic method to convert byte data to a pojo
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation jobAcceptConverter(String hexString);

  /**
   * This method will convert the byte data of REPORT_TRIP_INFO_NORMAL(221) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation reportTripInfoNormalConverter(String hexString);

  /**
   * This method will convert the byte data of REJECT_JOB_MODIFICATION_EVENT(179) event to a Java
   * bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation rejectJobModificationConverter(String hexString);

  /**
   * This method will convert the byte data of JOB_REJECT_EVENT (155) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation jobRejectConverter(String hexString);

  /**
   * This method will convert the byte data of ACKNOWLEDGE_JOB_CANCELLATION_EVENT(153) event to a
   * Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation acknowledgeJobCancellationConverter(String hexString);

  /**
   * This method will convert the byte data of JOB_CONFIRMATION_EVENT(14) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation jobConfirmationAcknowledgeConverter(String hexString);

  /**
   * This method will convert the byte data of JOB_MODIFICATION_EVENT(152) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation jobModificationConverter(String hexString);

  /**
   * This method will convert the byte data of MESSAGE_ACKNOWLEDGE(251) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation messageAcknowledgeConverter(String hexString);

  /**
   * This method will convert the byte data of ARRIVAL_EVENT(156) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation arrivalEventConverter(String hexString);

  /**
   * This method will convert the byte data of JOB_NUMBER_BLOCK_REQUEST(141) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation jobNumberBlockRequestEventConverter(String hexString);

  /**
   * This method will convert the byte data of NO_SHOW_REQUEST_EVENT(159) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation noShowRequestConverter(String hexString);

  /**
   * This method will convert the byte data of NOTIFY_ON_CALL_EVENT(184) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation notifyOnCallEventConverter(String hexString);

  /**
   * This method will convert the byte data of UPDATE_STOP_EVENT_ID(213) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation updateStopEventConverter(String hexString);

  /**
   * This method will convert the byte data of METER_ON_DISPATCH_JOB(160) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation meterOnDispatchConverter(String hexString);

  /**
   * This method will convert the byte data of AUTO_ACCEPT_JOB_CONF_ACK_EVENT(190) event to a Java
   * bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation autoAcceptJobConfirmationAckEvent(String hexString);

  /**
   * This method will convert the byte data of CALL_OUT_EVENT(158)event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation callOutEvent(String hexString);

  /**
   * This method will convert the byte data of METER_OFF_DISPATCH_JOB_EVENT(161) event to a Java
   * bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation meterOffDispatchJobEvent(String hexString);

  /**
   * This method will convert the byte data of METER_OFF_STREETHAIL_JOB_EVENT(140) event to a Java
   * bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation meterOffStreetHailJobEvent(String hexString);

  /**
   * This method will convert the byte data of ACKNOWLEDGE_CONVERT_STREET_HAIL_EVENT(151) event to a
   * Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation acknowledgeConvertStreetHailEvent(String hexString);

  /**
   * This method will convert the byte data of METER_ON_STREETHAIL_JOB_EVENT(139) event to a Java
   * bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation meterOnStreetHailJobEvent(String hexString);

  /**
   * This method will convert the byte data of VOICE_STREAMING_MESSAGE(187) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation voiceStreamConverter(String hexString);

  /**
   * This method will convert the byte data of RESPONSE_TO_SIMPLE_MESSAGE(166) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation responseToSimpleMessageConverter(String hexString);

  /**
   * Method to convert byte for send message
   *
   * @param hexString hexString
   * @return ByteDataRepresentation
   */
  ByteDataRepresentation sendMessageConverter(String hexString);

  /**
   * This method will convert the byte data of EMERGENCY_REPORT(164) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation emergencyReportConverter(String hexString);

  /**
   * This method will convert the byte data of FARE_CALCULATION_REQUEST_EVENT_ID(194) event to a
   * Java bean
   *
   * @param hexString hexString
   * @return JobEventCommand
   */
  ByteDataRepresentation fareCalculationRequestConverter(String hexString);

  /**
   * This method will convert the byte data of FORGET_PASSWORD_REQUEST(226) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation
   */
  ByteDataRepresentation forgotPasswordRequestConverter(String hexString);

  /**
   * This method will convert the byte data of ADVANCED_JOB_REMIND("169") event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation
   */
  ByteDataRepresentation advanceJobRemindEventConverter(String hexString);

  /**
   * This method will convert the byte data of Rcsa False alarm("186") event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation
   */
  ByteDataRepresentation emergencyRequestConverter(String hexString);

  /**
   * This method will convert the byte data of Rcsa Respond to structure("167") event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation
   */
  ByteDataRepresentation respondToStructureConverter(String hexString);

  /**
   * This method will convert the byte data of MDTLogOffAPIRequest to java bean
   *
   * @param byteString byteString
   * @param ipAddArr ipAddArr
   * @return MDTLogOffAPIRequest
   */
  MdtLogOffApiRequest convertLogOffByteToBeanConverter(String byteString, byte[] ipAddArr);

  /**
   * This method will convert the byte data of MDTLogOnAPIRequest to java bean
   *
   * @param byteString byteString
   * @param ipAddArr ipAddArr
   * @return MDTLogOnAPIRequest
   */
  MdtLogOnApiRequest convertLogOnByteToBeanConverter(String byteString, byte[] ipAddArr);

  /**
   * This method will convert the byte data of MDTIVDDeviceConfigRequest to java bean
   *
   * @param byteString byteString
   * @param ipAddArr ipAddArr
   * @return IVDDeviceConfigAPIRequest
   */
  MdtIvdDeviceConfigApiRequest convertIVDDeviceConfigByteToBeanConverter(
      String byteString, byte[] ipAddArr);

  /**
   * This method will convert the byte data of MDTPowerUpRequest to java bean
   *
   * @param byteString byteString
   * @param ipAddArr ipAddArr
   * @return MDTPowerUpAPIRequest
   */
  MdtPowerUpApiRequest convertPowerUpByteToBeanConverter(String byteString, byte[] ipAddArr);

  /**
   * This method will convert the byte data of UPDATE_STC(137) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation updateStcConverter(String hexString);

  /**
   * This method will convert the byte data of BREAK(134) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation breakConverter(String hexString);

  /**
   * This method will convert the byte data of UPDATE_BUSY(135) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation busyConverter(String hexString);

  /**
   * This method will convert the byte data of Regular Report(136) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation regularConverter(String hexString);

  /**
   * This method will convert the byte data of IVD_EVENT(219) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation ivdEventConverter(String hexString);

  /**
   * This method will convert the byte data of VERIFY_OTP(227) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation verifyOtpConverter(String hexString);

  /**
   * This method will convert the byte data of IVD_PING_RESPONSE(163) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation ivdPingResponseConverter(String hexString);

  /**
   * This method will convert the byte data of REPORT_TOTAL_MILEAGE(212) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation reportTotalMileageConverter(String hexString);

  /**
   * This method will convert the byte data of CHANGE_PIN_REQUEST(150) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation changePinRequestConverter(String hexString);

  /**
   * This method will convert the byte data of DRIVER_PERFORMANCE_REQUEST(108) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation driverPerformanceRequestConverter(String hexString);

  /**
   * This method will convert the byte data of CROSSING_ZONE(133) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation crossingZoneConverter(String hexString);

  /**
   * This method will convert the byte data of CHANGE_SHIFT_UPDATE(172) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation changeShiftUpdateConverter(String hexString);

  /**
   * This method will convert the byte data of DESTINATION_UPDATE(171) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation destinationUpdateConverter(String hexString);

  /**
   * This method will convert the byte data of EXPRESSWAY_STATUS_UPDATE(132) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation expresswayStatusUpdateConverter(String hexString);

  /**
   * This method will convert the byte data of NOTIFY_STATIC_GPS(157) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation ByteDataRepresentation
   */
  ByteDataRepresentation notifyStaticGpsConverter(String hexString);

  /**
   * This method will extract ip address from byte array
   *
   * @param bytesMsg - Byte Message
   * @return ip
   */
  String extractMdtIpAddress(String bytesMsg);

  /**
   * This method will convert the byte data of MDT_SYNC_REQUEST(176) event to a Java bean
   *
   * @param hexString hexString
   * @return ByteDataRepresentation
   */
  ByteDataRepresentation mdtSyncRequestConverter(String hexString);
}
