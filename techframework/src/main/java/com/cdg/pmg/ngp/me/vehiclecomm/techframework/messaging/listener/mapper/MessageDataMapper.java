package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.mapper;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobOffersResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtLogOffApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtLogOnApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtPowerUpApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.entities.IvdMessageRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.ByteDataRepresentation;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdResponseData;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.JobDispatchEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.RcsaEventProducerData;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.RcsaMessageEventProducerData;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.TripInfo;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommFailedRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.esbjobevent.EsbJobEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ivdresponse.IvdResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.jobdispatchevent.JobDispatchEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.producercsaevent.ProduceRcsaEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.rcsamessageevent.RcsaMessageEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.tripupload.UploadTripEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.vehicleevent.VehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.IVDMessageHeader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/** interface MessageDataMapper */
@Mapper(
    builder = @Builder(disableBuilder = true),
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageDataMapper {

  /**
   * vehicleCommFailedToTechMapper
   *
   * @param vehicleCommFailedRequest vehicleCommFailedRequest
   * @return VehicleCommFailedRequest
   */
  com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.vehiclecommfailedrequest
          .VehicleCommFailedRequest
      vehicleCommFailedToTechMapper(VehicleCommFailedRequest vehicleCommFailedRequest);

  /**
   * Maps JobDispatchEvent to JobDispatchEventRequest
   *
   * @param jobDispatchEvent jobDispatchEvent
   * @return JobDispatchEventRequest
   */
  @Mapping(source = "sosFlag", target = "sosFlag", defaultValue = "true")
  @Mapping(source = "enableCalloutButton", target = "enableCalloutButton", defaultValue = "true")
  @Mapping(source = "collectFare", target = "collectFare", defaultValue = "true")
  JobDispatchEventRequest mapToJobDispatchEventRequest(JobDispatchEvent jobDispatchEvent);

  /**
   * Maps VehicleEvent to VehicleEventRequest
   *
   * @param vehicleEvent vehicleEvent
   * @return VehicleEventRequest
   */
  VehicleEventRequest mapToVehicleEventRequest(VehicleEvent vehicleEvent);

  /**
   * Maps JobOffersResponse to JobDispatchEvent
   *
   * @param jobOffersResponse jobOffersResponse
   * @return JobDispatchEvent
   */
  @Mapping(target = "sosFlag", constant = "false")
  @Mapping(target = "ackFlag", constant = "true")
  @Mapping(target = "autoAcceptFlag", constant = "0")
  @Mapping(source = "jobStatus", target = "jobStatus", qualifiedByName = "mapJobStatus")
  JobDispatchEvent joboffersResponseToJobDispatchEvent(JobOffersResponse jobOffersResponse);

  /**
   * This method maps tripInfo to the generated class of UploadTrip schema
   *
   * @param tripInfo tripInfo
   * @return uploadTrip
   */
  UploadTripEvent tripInfoToUploadTrip(TripInfo tripInfo);

  /**
   * This method maps converted byte values to a POJO
   *
   * @param header header
   * @param stringMap stringMap
   * @param integerMap integerMap
   * @param dateMap dateMap
   * @param booleanMap booleanMap
   */
  @Mapping(target = "voiceStreamArray", ignore = true)
  @Mapping(source = "header.mobileId", target = "ivdNo")
  @Mapping(
      source = "header.timestamp",
      target = "eventTime",
      qualifiedByName = "convertEpochMillisToLocalDateTime")
  @Mapping(source = "header.offsetLatitude", target = "offsetLatitude")
  @Mapping(source = "header.offsetLongitude", target = "offsetLongitude")
  @Mapping(source = "header.speed", target = "speed")
  @Mapping(source = "header.direction", target = "direction")
  @Mapping(source = "header.serialNum", target = "serialNumber")
  @Mapping(source = "header.type.id", target = "messageId")
  @Mapping(source = "stringMap.jobNumber", target = "jobNo")
  @Mapping(source = "stringMap.rejectReasonCode", target = "reasonCode")
  @Mapping(source = "stringMap.source", target = "dispatchLevel")
  @Mapping(source = "stringMap.hiredDistance", target = "distance")
  @Mapping(source = "stringMap.tripNumber", target = "tripId")
  @Mapping(source = "stringMap.totalDistance", target = "totalDistance")
  @Mapping(source = "stringMap.driverId", target = "driverId")
  @Mapping(source = "stringMap.tripType", target = "tripType")
  @Mapping(source = "stringMap.gstInclusive", target = "gstInclusive")
  @Mapping(source = "stringMap.paymentMethod", target = "paymentMethod")
  @Mapping(source = "stringMap.promoCode", target = "promoCode")
  @Mapping(source = "stringMap.entryMode", target = "entryMode")
  @Mapping(source = "stringMap.productId", target = "productId")
  @Mapping(source = "stringMap.updateType", target = "updateType")
  @Mapping(source = "stringMap.platformFeeIsApplicable", target = "platformFeeApplicability")
  @Mapping(source = "stringMap.isFeeWaived", target = "cpFreeInsurance")
  @Mapping(source = "stringMap.cardNumber", target = "ccNumber")
  @Mapping(source = "stringMap.partnerDiscountType", target = "partnerDiscountType")
  @Mapping(source = "stringMap.partnerOrderId", target = "partnerOrderId")
  @Mapping(source = "stringMap.cabRewardsFlag", target = "isLoyaltyMember")
  @Mapping(source = "stringMap.corporateCardNumber", target = "corpCardNo")
  @Mapping(source = "stringMap.accountId", target = "accountNumber")
  @Mapping(source = "stringMap.requestId", target = "requestId")
  @Mapping(source = "stringMap.mobileNumber", target = "mobileNumber")
  @Mapping(source = "stringMap.vehiclePlateNumber", target = "vehiclePlateNumber")
  @Mapping(source = "stringMap.emergencyId", target = "emergencyId")
  @Mapping(source = "integerMap.eta", target = "eta", qualifiedByName = "convertSecondsToMinutes")
  @Mapping(source = "integerMap.gst", target = "gst")
  @Mapping(source = "integerMap.cabRewardsAmount", target = "rewardsValue")
  @Mapping(source = "integerMap.meterFare", target = "meterFare")
  @Mapping(source = "integerMap.adminFee", target = "creditAdmin")
  @Mapping(source = "integerMap.balanceDue", target = "balanceDue")
  @Mapping(source = "integerMap.voucherPaymentAmount", target = "voucherPaymentAmount")
  @Mapping(source = "integerMap.erpFee", target = "erp")
  @Mapping(source = "integerMap.amountPaid", target = "amountPaid")
  @Mapping(source = "integerMap.totalPaid", target = "totalPaid")
  @Mapping(source = "integerMap.levy", target = "levy")
  @Mapping(source = "integerMap.promoAmount", target = "promoAmt")
  @Mapping(source = "integerMap.fixedPrice", target = "fixedPrice")
  @Mapping(source = "integerMap.discount", target = "discount")
  @Mapping(source = "integerMap.adminFeeDiscount", target = "adminFeeDiscount")
  @Mapping(source = "integerMap.hlaFee", target = "comfortProtectPremium")
  @Mapping(source = "integerMap.platformFee", target = "platformFee")
  @Mapping(source = "integerMap.pickupX", target = "pickupLng")
  @Mapping(source = "integerMap.pickupY", target = "pickupLat")
  @Mapping(source = "integerMap.destinationX", target = "dropoffLng")
  @Mapping(source = "integerMap.destinationY", target = "dropoffLat")
  @Mapping(source = "integerMap.intermediateX", target = "intermediateLng")
  @Mapping(source = "integerMap.intermediateY", target = "intermediateLat")
  @Mapping(source = "integerMap.partnerDiscountValue", target = "partnerDiscountValue")
  @Mapping(source = "integerMap.partnerDiscountAmt", target = "partnerDiscountAmt")
  @Mapping(source = "integerMap.peakPeriod", target = "peakPeriod")
  @Mapping(source = "integerMap.airport", target = "airport")
  @Mapping(source = "integerMap.cbd", target = "cbd")
  @Mapping(source = "integerMap.location", target = "location")
  @Mapping(source = "integerMap.dropOffLocation", target = "dropOffLocation")
  @Mapping(source = "integerMap.privateBooking", target = "privateBooking")
  @Mapping(source = "integerMap.publicHoliday", target = "publicHoliday")
  @Mapping(source = "integerMap.lateNight10", target = "lateNight10")
  @Mapping(source = "integerMap.lateNight20", target = "lateNight20")
  @Mapping(source = "integerMap.lateNight35", target = "lateNight35")
  @Mapping(source = "integerMap.lateNight50", target = "lateNight50")
  @Mapping(source = "integerMap.preHoliday", target = "preHoliday")
  @Mapping(source = "integerMap.acknowledgement", target = "acknowledgement")
  @Mapping(target = "dslc", constant = "0")
  @Mapping(target = "tariffInfo", ignore = true)
  @Mapping(
      source = "integerMap.pickupTime",
      target = "tripStartDt",
      qualifiedByName = "convertEpochSecondsToLocalDateTime")
  @Mapping(
      source = "dateMap.tripEndTime",
      target = "tripEndDt",
      qualifiedByName = "convertDateToLocalDateTime")
  @Mapping(source = "stringMap.canMessageId", target = "uniqueMsgId")
  @Mapping(source = "integerMap.messageSelection", target = "selection")
  @Mapping(source = "stringMap.messageContent", target = "messageContent")
  @Mapping(source = "stringMap.eventContent", target = "eventContent")
  @Mapping(source = "stringMap.otp", target = "code")
  @Mapping(source = "integerMap.referenceNumber", target = "refNo")
  @Mapping(source = "integerMap.sequenceNumber", target = "seqNo")
  @Mapping(source = "integerMap.totalMileage", target = "totalMileage")
  @Mapping(source = "stringMap.oldPin", target = "oldPin")
  @Mapping(source = "stringMap.newPin", target = "newPin")
  @Mapping(source = "stringMap.zoneId", target = "zoneId")
  @Mapping(source = "header.ivdStatus", target = "ivdStatus")
  @Mapping(source = "integerMap.eventId", target = "eventId")
  @Mapping(source = "stringMap.receivedMessageId", target = "receivedMessageId")
  @Mapping(source = "stringMap.receivedMessageSn", target = "receivedMessageSn")
  @Mapping(source = "integerMap.noShowEventType", target = "noShowEventType")
  @Mapping(source = "booleanMap.validLocationFlag", target = "isValidLocation")
  @Mapping(target = "additionalCharges", ignore = true)
  @Mapping(target = "ackRequired", source = "header.acknowledgement")
  @Mapping(source = "stringMap.maximumSpeed", target = "maximumSpeed")
  @Mapping(source = "stringMap.meterEditFlag", target = "meterEdit")
  ByteDataRepresentation mapToByteDataRepresentation(
      IVDMessageHeader header,
      Map<String, String> stringMap,
      Map<String, Integer> integerMap,
      Map<String, Date> dateMap,
      Map<String, Boolean> booleanMap);

  @Mapping(source = "header.mobileId", target = "ivdNo")
  @Mapping(source = "header.serialNum", target = "serialNumber")
  @Mapping(source = "header.type.id", target = "messageId")
  @Mapping(target = "acknowledgement", ignore = true)
  @Mapping(target = "ackRequired", source = "header.acknowledgement")
  ByteDataRepresentation mapTByteDataRepresentation(
      IVDMessageHeader header, String voiceStreamArray);

  @Named("convertEpochMillisToLocalDateTime")
  default LocalDateTime convertEpochMillisToLocalDateTime(Long epochMilli) {
    if (Objects.isNull(epochMilli)) {
      return null;
    }
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.of("UTC"));
  }

  @Named("convertEpochSecondsToLocalDateTime")
  default LocalDateTime convertUnixTimeStamp(Long epochSeconds) {
    if (Objects.isNull(epochSeconds)) {
      return null;
    }
    return Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  @Named("convertDateToLocalDateTime")
  default LocalDateTime convertDateToLocalDateTime(Date date) {
    if (Objects.isNull(date)) {
      return null;
    }
    return LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
  }

  @Named("convertSecondsToMinutes")
  default String convertSecondsToMinutes(Integer seconds) {
    if (seconds == null) {
      return null;
    }
    return String.valueOf(TimeUnit.MINUTES.convert(seconds, TimeUnit.SECONDS));
  }

  /**
   * ivdResponseTechMapper
   *
   * @param ivdResponse ivdResponse
   * @return IvdResponse
   */
  IvdResponse ivdResponseTechMapper(IvdResponseData ivdResponse);

  ProduceRcsaEvent rcsaResponseTechMapper(RcsaEventProducerData rcsaEventProducerData);

  @Mapping(source = "header.mobileId", target = "ivdNo")
  @Mapping(source = "integerMap.totalMileage", target = "totalMileage")
  @Mapping(source = "stringMap.driverId", target = "driverId")
  @Mapping(target = "timeStamp", expression = "java(getInstantDate())")
  @Mapping(target = "messageId", source = "header.type.id")
  @Mapping(target = "offsetLatitude", source = "header.offsetLatitude")
  @Mapping(target = "offsetLongitude", source = "header.offsetLongitude")
  @Mapping(target = "serialNo", source = "header.serialNum")
  @Mapping(target = "isAckRequired", source = "header.acknowledgement")
  @Mapping(target = "ipAddress", source = "mdtIpAddress")
  MdtLogOffApiRequest mapToMdtLogOffRequest(
      IVDMessageHeader header,
      Map<String, String> stringMap,
      Map<String, Integer> integerMap,
      String mdtIpAddress,
      Object o);

  default LocalDateTime getInstantDate() {
    ZonedDateTime instant = ZonedDateTime.now(ZoneId.of("UTC"));
    return instant.toLocalDateTime();
  }

  @Mapping(source = "header.type.id", target = "messageId")
  @Mapping(source = "stringMap.driverId", target = "mobileNumber")
  @Mapping(source = "header.mobileId", target = "mobileId")
  @Mapping(source = "header.offsetLatitude", target = "offsetLatitude")
  @Mapping(source = "header.offsetLongitude", target = "offsetLongitude")
  @Mapping(target = "logonTimeStamp", expression = "java(getInstantDate())")
  @Mapping(source = "stringMap.driverPin", target = "driverPin")
  @Mapping(source = "stringMap.pendingOncallJobno", target = "pendingOnCallJobNo")
  @Mapping(source = "integerMap.totalMileage", target = "totalMileage")
  @Mapping(source = "stringMap.driverId", target = "driverId")
  @Mapping(target = "serialNo", source = "header.serialNum")
  @Mapping(target = "isAckRequired", source = "header.acknowledgement")
  @Mapping(target = "ipAddress", source = "mdtIpAddress")
  MdtLogOnApiRequest mapToMdtLogOnRequest(
      IVDMessageHeader header,
      Map<String, String> stringMap,
      Map<String, Integer> integerMap,
      String mdtIpAddress,
      Object o);

  @Mapping(source = "header.type.id", target = "messageId")
  @Mapping(source = "stringMap.mobileId", target = "mobileNo")
  @Mapping(source = "header.mobileId", target = "ivdNo")
  @Mapping(target = "timeStamp", expression = "java(getInstantDate())")
  @Mapping(source = "stringMap.ivdFirmwareVersion", target = "firmwareVersion")
  @Mapping(source = "stringMap.selfTestResult", target = "selfTestResult")
  @Mapping(source = "stringMap.imsi", target = "imsi")
  @Mapping(source = "stringMap.vehiclePlateNumber", target = "vehiclePlateNum")
  @Mapping(source = "stringMap.telcoId", target = "telcoId")
  @Mapping(source = "stringMap.meterVersion", target = "meterVersion")
  @Mapping(source = "stringMap.tariffChecksum", target = "tariffCheckSum")
  @Mapping(source = "stringMap.firmwareChecksum", target = "firmwareCheckSum")
  @Mapping(source = "stringMap.gisVersion", target = "gisVersion")
  @Mapping(source = "stringMap.mapdataVersion", target = "mapVersion")
  @Mapping(source = "stringMap.screenSaverVer", target = "screenSaverVersion")
  @Mapping(source = "stringMap.jobNumberBlockStart", target = "jobNoBlockStart")
  @Mapping(source = "stringMap.jobNumberBlockEnd", target = "jobNoBlockEnd")
  @Mapping(source = "stringMap.ipAddress", target = "ipAddr")
  @Mapping(source = "stringMap.ivdInfoModel", target = "modelNo")
  @Mapping(target = "fileList", ignore = true)
  @Mapping(source = "stringMap.ivdModel", target = "ivdInfoList.ivdModel")
  @Mapping(source = "stringMap.ivdParam", target = "ivdInfoList.ivdParam")
  @Mapping(source = "stringMap.ivdValue", target = "ivdInfoList.ivdValue")
  @Mapping(source = "header.offsetLatitude", target = "offsetLatitude")
  @Mapping(source = "header.offsetLongitude", target = "offsetLongitude")
  @Mapping(source = "header.speed", target = "speed")
  @Mapping(source = "header.direction", target = "heading")
  @Mapping(target = "serialNo", source = "header.serialNum")
  @Mapping(target = "isAckRequired", source = "header.acknowledgement")
  MdtPowerUpApiRequest mapToMdtPowerUpRequest(
      IVDMessageHeader header, Map<String, String> stringMap, String mdtIp, Object o);

  @Mapping(source = "ivdNo", target = "ivdNo")
  RcsaMessageEvent cancelMessageToRcsaMessage(
      RcsaMessageEventProducerData rcsaMessageEventProducerData);

  @Named("mapJobStatus")
  default JobDispatchEvent.JobStatusEnum mapJobStatus(String jobStatus) {
    return JobDispatchEvent.JobStatusEnum.fromValue(jobStatus);
  }

  EsbJobEvent ivdMessageRequestToEsbJobEvent(IvdMessageRequest ivdMessageRequest);
}
