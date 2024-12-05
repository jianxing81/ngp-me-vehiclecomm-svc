package com.cdg.pmg.ngp.me.vehiclecomm.application.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VehicleDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ZoneInfoDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbVehicle;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdStatusEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.ByteDataRepresentation;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.GenericEventCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.JobDispachNotificationRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.JobDispatchEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.PlatformFeeItem;
import java.util.Objects;
import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(
    builder = @Builder(disableBuilder = true),
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EsbVehicleMapper {

  /**
   * Maps byteDataRepresentation to EsbVehicle
   *
   * @param esbVehicle esbVehicle (target)
   * @param byteDataRepresentation (source)
   */
  @Mapping(source = "byteDataRepresentation.jobNo", target = "esbVehicle.byteData.jobNo")
  @Mapping(source = "byteDataRepresentation.ivdNo", target = "esbVehicle.byteData.ivdNo")
  @Mapping(source = "byteDataRepresentation.driverId", target = "esbVehicle.byteData.driverId")
  @Mapping(
      source = "byteDataRepresentation.offsetLatitude",
      target = "esbVehicle.byteData.offsetLatitude")
  @Mapping(
      source = "byteDataRepresentation.offsetLongitude",
      target = "esbVehicle.byteData.offsetLongitude")
  @Mapping(source = "byteDataRepresentation.eventTime", target = "esbVehicle.byteData.eventTime")
  @Mapping(source = "byteDataRepresentation.speed", target = "esbVehicle.byteData.speed")
  @Mapping(source = "byteDataRepresentation.direction", target = "esbVehicle.byteData.direction")
  @Mapping(source = "byteDataRepresentation.code", target = "esbVehicle.byteData.code")
  @Mapping(source = "byteDataRepresentation.refNo", target = "esbVehicle.byteData.refNo")
  @Mapping(source = "byteDataRepresentation.seqNo", target = "esbVehicle.byteData.seqNo")
  @Mapping(source = "byteDataRepresentation.oldPin", target = "esbVehicle.byteData.oldPin")
  @Mapping(source = "byteDataRepresentation.newPin", target = "esbVehicle.byteData.newPin")
  @Mapping(source = "byteDataRepresentation.zoneId", target = "esbVehicle.byteData.zoneId")
  @Mapping(
      source = "byteDataRepresentation.mobileNumber",
      target = "esbVehicle.byteData.mobileNumber")
  @Mapping(
      source = "byteDataRepresentation.totalMileage",
      target = "esbVehicle.byteData.totalMileage")
  @Mapping(
      source = "byteDataRepresentation.serialNumber",
      target = "esbVehicle.byteData.serialNumber")
  @Mapping(source = "byteDataRepresentation.messageId", target = "esbVehicle.byteData.messageId")
  @Mapping(
      source = "byteDataRepresentation.eventContent",
      target = "esbVehicle.byteData.eventContent")
  @Mapping(
      source = "byteDataRepresentation.vehiclePlateNumber",
      target = "esbVehicle.byteData.vehiclePlateNumber")
  @Mapping(
      source = "byteDataRepresentation.ivdStatus",
      target = "esbVehicle.byteData.ivdEventStatus",
      qualifiedByName = "mapIvdEventStatus")
  @Mapping(source = "byteDataRepresentation.eventId", target = "esbVehicle.byteData.eventId")
  @Mapping(
      source = "byteDataRepresentation.ackRequired",
      target = "esbVehicle.byteData.ackRequired")
  void byteDataRepresentationToIvdInboundEvent(
      @MappingTarget EsbVehicle esbVehicle, ByteDataRepresentation byteDataRepresentation);

  /**
   * Maps vehicleDetailsResponse to EsbVehicle
   *
   * @param esbVehicle esbVehicle (target)
   * @param vehicleDetailsResponse (source)
   */
  @Mapping(source = "vehicleDetailsResponse.vehicleId", target = "esbVehicle.vehicleDetails.id")
  @Mapping(
      source = "vehicleDetailsResponse.ipAddress",
      target = "esbVehicle.vehicleDetails.ipAddress")
  @Mapping(source = "vehicleDetailsResponse.driverId", target = "esbVehicle.byteData.driverId")
  void vehicleDetailsResponseToVehicleInboundEvent(
      @MappingTarget EsbVehicle esbVehicle, VehicleDetailsResponse vehicleDetailsResponse);

  /**
   * Maps zoneInfoDetailsResponse to EsbVehicle
   *
   * @param esbVehicle esbVehicle (target)
   * @param zoneInfoDetailsResponse (source)
   */
  @Mapping(source = "zoneInfoDetailsResponse.zoneId", target = "esbVehicle.zoneInfoDetails.id")
  @Mapping(
      source = "zoneInfoDetailsResponse.zoneIvdDesc",
      target = "esbVehicle.zoneInfoDetails.zoneIvdDesc")
  @Mapping(
      source = "zoneInfoDetailsResponse.roofTopIndex",
      target = "esbVehicle.zoneInfoDetails.roofTopIndex")
  void zoneInfoDetailsResponseToVehicleInboundEvent(
      @MappingTarget EsbVehicle esbVehicle, ZoneInfoDetailsResponse zoneInfoDetailsResponse);

  /**
   * Maps genericEventCommand to ByteArrayData
   *
   * @param esbVehicle esbVehicle (target)
   * @param genericEventCommand (source)
   */
  @Mapping(source = "genericEventCommand.byteArray", target = "esbVehicle.byteArrayData.byteArray")
  @Mapping(
      source = "genericEventCommand.byteArraySize",
      target = "esbVehicle.byteArrayData.byteArraySize")
  @Mapping(
      source = "genericEventCommand.byteArrayMessage",
      target = "esbVehicle.byteArrayData.byteArrayMessage")
  void genericEventCommandToByteArrayData(
      @MappingTarget EsbVehicle esbVehicle, GenericEventCommand genericEventCommand);

  /**
   * Maps jobDispatchEventRequest to JobDispachNotificationRequest
   *
   * @param jobDispatchEventRequest jobDispatchEventRequest (source)
   * @return JobDispachNotificationRequest (target)
   */
  @Mapping(
      source = "jobDispatchEventRequest",
      target = "jobStatus",
      qualifiedByName = "convertJobStatusEnumToInteger")
  @Mapping(source = "destAddr", target = "dest")
  @Mapping(source = "notes", target = "driverNotes")
  @Mapping(source = "productDesc", target = "pdtDesc")
  @Mapping(source = "productId", target = "pdtID")
  @Mapping(source = "pickupAddr", target = "pickupLoc")
  @Mapping(source = "promoAmount", target = "promoAmt")
  @Mapping(source = "jobSurgeSigns", target = "surgeSignCode")
  @Mapping(source = "autoAcceptFlag", target = "autoaccept")
  @Mapping(source = "vehicleId", target = "vehicleID")
  @Mapping(
      source = "jobDispatchEventRequest",
      target = "bookingFee",
      qualifiedByName = "convertDoubleToString")
  @Mapping(
      source = "jobDispatchEventRequest",
      target = "partnerDiscountValue",
      qualifiedByName = "convertStringToDouble")
  @Mapping(source = "multiStop.intermediateAddr", target = "intermediate")
  @Mapping(source = "multiStop.intermediateLat", target = "intermediateLat")
  @Mapping(source = "multiStop.intermediateLng", target = "intermediateLng")
  @Mapping(
      source = "jobDispatchEventRequest",
      target = "platformFeeApplicability",
      qualifiedByName = "mapToPlatformFeeApplicability")
  @Mapping(
      source = "jobDispatchEventRequest",
      target = "platformFeeThresholdLimit",
      qualifiedByName = "mapToPlatformFeeThresholdLimit")
  @Mapping(
      source = "jobDispatchEventRequest",
      target = "lowerPlatformFee",
      qualifiedByName = "mapToLowerPlatformFee")
  @Mapping(
      source = "jobDispatchEventRequest",
      target = "upperPlatformFee",
      qualifiedByName = "mapToUpperPlatformFee")
  @Mapping(source = "offerTimeout", target = "offerTimeOut")
  @Mapping(source = "pickupTime", target = "pickUpTime")
  @Mapping(source = "paymentMethod", target = "paymentMode")
  @Mapping(source = "driverId", target = "driverID")
  @Mapping(source = "sosFlag", target = "sosFlag", defaultValue = "true")
  @Mapping(source = "enableCalloutButton", target = "enableCalloutButton", defaultValue = "true")
  @Mapping(source = "collectFare", target = "collectFare", defaultValue = "true")
  @Mapping(source = "totalDistance", target = "totalDistance")
  @Mapping(source = "tripDistance", target = "tripDistance")
  @Mapping(source = "pickupDistance", target = "pickupDistance")
  JobDispachNotificationRequest mapJobDispachForNotification(
      JobDispatchEventRequest jobDispatchEventRequest);

  /**
   * Maps jobDispatchEventRequest to JobDispatchEventRequest
   *
   * @param jobDispatchEventRequest jobDispatchEventRequest (source)
   * @return JobDispatchEventRequest (target)
   */
  @Named("convertJobStatusEnumToInteger")
  default Integer convertJobStatusEnumToInteger(JobDispatchEventRequest jobDispatchEventRequest) {
    if (null == jobDispatchEventRequest.getJobStatus()) {
      return null;
    }
    return jobDispatchEventRequest.getJobStatus().getId();
  }

  @Named(value = "convertDoubleToString")
  default String convertDoubleToString(JobDispatchEventRequest jobDispatchEventRequest) {
    return jobDispatchEventRequest.getBookingFee() != null
        ? Double.toString(jobDispatchEventRequest.getBookingFee())
        : null;
  }

  @Named(value = "convertStringToDouble")
  default Double convertStringToDouble(JobDispatchEventRequest jobDispatchEventRequest) {
    return jobDispatchEventRequest.getPartnerDiscountValue() != null
        ? Double.parseDouble(jobDispatchEventRequest.getPartnerDiscountValue())
        : null;
  }

  @Named(value = "mapToPlatformFeeThresholdLimit")
  default Double mapToPlatformFeeThresholdLimit(JobDispatchEventRequest jobDispatchEventRequest) {
    if (Objects.nonNull(jobDispatchEventRequest)
        && ObjectUtils.isNotEmpty(jobDispatchEventRequest.getPlatformFeeItem())) {
      PlatformFeeItem platformFeeItem = jobDispatchEventRequest.getPlatformFeeItem().get(0);
      if (platformFeeItem != null) {
        return platformFeeItem.getThresholdLimit();
      }
      return null;
    }
    return null;
  }

  @Named(value = "mapToPlatformFeeApplicability")
  default String mapToPlatformFeeApplicability(JobDispatchEventRequest jobDispatchEventRequest) {
    if (Objects.nonNull(jobDispatchEventRequest)
        && ObjectUtils.isNotEmpty(jobDispatchEventRequest.getPlatformFeeItem())) {
      PlatformFeeItem platformFeeItem = jobDispatchEventRequest.getPlatformFeeItem().get(0);
      if (platformFeeItem != null) {
        return platformFeeItem.getPlatformFeeApplicability();
      }
      return null;
    }
    return null;
  }

  @Named(value = "mapToLowerPlatformFee")
  default Double mapToLowerPlatformFee(JobDispatchEventRequest jobDispatchEventRequest) {
    if (Objects.nonNull(jobDispatchEventRequest)
        && ObjectUtils.isNotEmpty(jobDispatchEventRequest.getPlatformFeeItem())) {
      PlatformFeeItem platformFeeItem = jobDispatchEventRequest.getPlatformFeeItem().get(0);
      if (platformFeeItem != null) {
        return platformFeeItem.getFeeBelowThresholdLimit();
      }
      return null;
    }
    return null;
  }

  @Named(value = "mapToUpperPlatformFee")
  default Double mapToUpperPlatformFee(JobDispatchEventRequest jobDispatchEventRequest) {
    if (Objects.nonNull(jobDispatchEventRequest)
        && ObjectUtils.isNotEmpty(jobDispatchEventRequest.getPlatformFeeItem())) {
      PlatformFeeItem platformFeeItem = jobDispatchEventRequest.getPlatformFeeItem().get(0);
      if (platformFeeItem != null) {
        return platformFeeItem.getFeeAboveThresholdLimit();
      }
      return null;
    }
    return null;
  }

  @Named(value = "mapIvdEventStatus")
  default String mapIvdEventStatus(String ivdStatusId) {
    if (Objects.nonNull(ivdStatusId) && ObjectUtils.isNotEmpty(ivdStatusId)) {
      return IvdStatusEnum.getIvdStatusById(ivdStatusId);
    }
    return null;
  }
}
