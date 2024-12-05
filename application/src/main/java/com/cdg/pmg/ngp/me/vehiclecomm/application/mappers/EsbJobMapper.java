package com.cdg.pmg.ngp.me.vehiclecomm.application.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RetrySchedulerData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VehicleDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbJob;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.entities.IvdMessageRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.ByteDataRepresentation;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.GenericEventCommand;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
    builder = @Builder(disableBuilder = true),
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EsbJobMapper {

  /**
   * Maps byteDataRepresentation to aggregate root
   *
   * @param esbJob esbJob
   * @param byteDataRepresentation byteDataRepresentation
   */
  @Mapping(source = "byteDataRepresentation.jobNo", target = "esbJob.byteData.jobNo")
  @Mapping(source = "byteDataRepresentation.tripId", target = "esbJob.byteData.tripId")
  @Mapping(source = "byteDataRepresentation.eta", target = "esbJob.byteData.eta")
  @Mapping(source = "byteDataRepresentation.updateType", target = "esbJob.byteData.updateType")
  @Mapping(
      source = "byteDataRepresentation.dispatchLevel",
      target = "esbJob.byteData.dispatchLevel")
  @Mapping(source = "byteDataRepresentation.reasonCode", target = "esbJob.byteData.reasonCode")
  @Mapping(source = "byteDataRepresentation.ivdNo", target = "esbJob.byteData.ivdNo")
  @Mapping(source = "byteDataRepresentation.driverId", target = "esbJob.byteData.driverId")
  @Mapping(
      source = "byteDataRepresentation.offsetLatitude",
      target = "esbJob.byteData.offsetLatitude")
  @Mapping(
      source = "byteDataRepresentation.offsetLongitude",
      target = "esbJob.byteData.offsetLongitude")
  @Mapping(source = "byteDataRepresentation.eventTime", target = "esbJob.byteData.eventTime")
  @Mapping(source = "byteDataRepresentation.speed", target = "esbJob.byteData.speed")
  @Mapping(source = "byteDataRepresentation.direction", target = "esbJob.byteData.direction")
  @Mapping(source = "byteDataRepresentation.serialNumber", target = "esbJob.byteData.serialNumber")
  @Mapping(source = "byteDataRepresentation.messageId", target = "esbJob.byteData.messageId")
  @Mapping(source = "byteDataRepresentation.distance", target = "esbJob.byteData.distance")
  @Mapping(
      source = "byteDataRepresentation.totalDistance",
      target = "esbJob.byteData.totalDistance")
  @Mapping(source = "byteDataRepresentation.tripType", target = "esbJob.byteData.tripType")
  @Mapping(source = "byteDataRepresentation.gst", target = "esbJob.byteData.gst")
  @Mapping(source = "byteDataRepresentation.gstInclusive", target = "esbJob.byteData.gstInclusive")
  @Mapping(source = "byteDataRepresentation.rewardsValue", target = "esbJob.byteData.rewardsValue")
  @Mapping(source = "byteDataRepresentation.meterFare", target = "esbJob.byteData.meterFare")
  @Mapping(source = "byteDataRepresentation.creditAdmin", target = "esbJob.byteData.creditAdmin")
  @Mapping(
      source = "byteDataRepresentation.paymentMethod",
      target = "esbJob.byteData.paymentMethod")
  @Mapping(
      source = "byteDataRepresentation.isLoyaltyMember",
      target = "esbJob.byteData.loyaltyMember")
  @Mapping(source = "byteDataRepresentation.balanceDue", target = "esbJob.byteData.balanceDue")
  @Mapping(
      source = "byteDataRepresentation.voucherPaymentAmount",
      target = "esbJob.byteData.voucherPaymentAmount")
  @Mapping(source = "byteDataRepresentation.erp", target = "esbJob.byteData.erp")
  @Mapping(source = "byteDataRepresentation.amountPaid", target = "esbJob.byteData.amountPaid")
  @Mapping(source = "byteDataRepresentation.totalPaid", target = "byteData.totalPaid")
  @Mapping(source = "byteDataRepresentation.levy", target = "esbJob.byteData.levy")
  @Mapping(source = "byteDataRepresentation.promoCode", target = "esbJob.byteData.promoCode")
  @Mapping(source = "byteDataRepresentation.promoAmt", target = "esbJob.byteData.promoAmt")
  @Mapping(source = "byteDataRepresentation.entryMode", target = "esbJob.byteData.entryMode")
  @Mapping(source = "byteDataRepresentation.productId", target = "esbJob.byteData.productId")
  @Mapping(source = "byteDataRepresentation.fixedPrice", target = "esbJob.byteData.fixedPrice")
  @Mapping(source = "byteDataRepresentation.discount", target = "esbJob.byteData.discount")
  @Mapping(
      source = "byteDataRepresentation.adminFeeDiscount",
      target = "esbJob.byteData.adminFeeDiscount")
  @Mapping(
      source = "byteDataRepresentation.comfortProtectPremium",
      target = "esbJob.byteData.comfortProtectPremium")
  @Mapping(source = "byteDataRepresentation.platformFee", target = "esbJob.byteData.platformFee")
  @Mapping(
      source = "byteDataRepresentation.platformFeeApplicability",
      target = "esbJob.byteData.platformFeeApplicability")
  @Mapping(source = "byteDataRepresentation.pickupLat", target = "esbJob.byteData.pickupLat")
  @Mapping(source = "byteDataRepresentation.pickupLng", target = "esbJob.byteData.pickupLng")
  @Mapping(source = "byteDataRepresentation.tripStartDt", target = "esbJob.byteData.tripStartDt")
  @Mapping(source = "byteDataRepresentation.tripEndDt", target = "esbJob.byteData.tripEndDt")
  @Mapping(source = "byteDataRepresentation.dropoffLat", target = "esbJob.byteData.dropoffLat")
  @Mapping(source = "byteDataRepresentation.dropoffLng", target = "esbJob.byteData.dropoffLng")
  @Mapping(
      source = "byteDataRepresentation.intermediateLat",
      target = "esbJob.byteData.intermediateLat")
  @Mapping(
      source = "byteDataRepresentation.intermediateLng",
      target = "esbJob.byteData.intermediateLng")
  @Mapping(
      source = "byteDataRepresentation.cpFreeInsurance",
      target = "esbJob.byteData.cpFreeInsurance")
  @Mapping(source = "byteDataRepresentation.ccNumber", target = "esbJob.byteData.ccNumber")
  @Mapping(
      source = "byteDataRepresentation.partnerDiscountType",
      target = "esbJob.byteData.partnerDiscountType")
  @Mapping(
      source = "byteDataRepresentation.partnerDiscountValue",
      target = "esbJob.byteData.partnerDiscountValue")
  @Mapping(
      source = "byteDataRepresentation.partnerOrderId",
      target = "esbJob.byteData.partnerOrderId")
  @Mapping(
      source = "byteDataRepresentation.partnerDiscountAmt",
      target = "esbJob.byteData.partnerDiscountAmt")
  @Mapping(source = "byteDataRepresentation.peakPeriod", target = "esbJob.byteData.peakPeriod")
  @Mapping(source = "byteDataRepresentation.airport", target = "esbJob.byteData.airport")
  @Mapping(source = "byteDataRepresentation.cbd", target = "esbJob.byteData.cbd")
  @Mapping(source = "byteDataRepresentation.location", target = "esbJob.byteData.location")
  @Mapping(
      source = "byteDataRepresentation.dropOffLocation",
      target = "esbJob.byteData.dropOffLocation")
  @Mapping(
      source = "byteDataRepresentation.privateBooking",
      target = "esbJob.byteData.privateBooking")
  @Mapping(
      source = "byteDataRepresentation.publicHoliday",
      target = "esbJob.byteData.publicHoliday")
  @Mapping(source = "byteDataRepresentation.lateNight10", target = "esbJob.byteData.lateNight10")
  @Mapping(source = "byteDataRepresentation.lateNight20", target = "esbJob.byteData.lateNight20")
  @Mapping(source = "byteDataRepresentation.lateNight35", target = "esbJob.byteData.lateNight35")
  @Mapping(source = "byteDataRepresentation.lateNight50", target = "esbJob.byteData.lateNight50")
  @Mapping(source = "byteDataRepresentation.preHoliday", target = "esbJob.byteData.preHoliday")
  @Mapping(source = "byteDataRepresentation.dslc", target = "esbJob.byteData.dslc")
  @Mapping(source = "byteDataRepresentation.mobileNumber", target = "esbJob.byteData.mobileNumber")
  @Mapping(
      source = "byteDataRepresentation.vehiclePlateNumber",
      target = "esbJob.byteData.vehiclePlateNumber")
  @Mapping(
      source = "byteDataRepresentation.acknowledgement",
      target = "esbJob.byteData.acknowledgement")
  @Mapping(
      source = "byteDataRepresentation.receivedMessageSn",
      target = "esbJob.byteData.receivedMessageSn")
  @Mapping(
      source = "byteDataRepresentation.receivedMessageId",
      target = "esbJob.byteData.receivedMessageId")
  @Mapping(
      source = "byteDataRepresentation.noShowEventType",
      target = "esbJob.byteData.noShowEventType")
  @Mapping(
      source = "byteDataRepresentation.isValidLocation",
      target = "esbJob.byteData.isValidLocation")
  @Mapping(
      source = "byteDataRepresentation.additionalCharges",
      target = "esbJob.byteData.additionalCharges")
  @Mapping(source = "byteDataRepresentation.ackRequired", target = "esbJob.byteData.ackRequired")
  @Mapping(source = "byteDataRepresentation.tariffInfo", target = "esbJob.byteData.tariffInfo")
  @Mapping(source = "byteDataRepresentation.maximumSpeed", target = "esbJob.byteData.maximumSpeed")
  @Mapping(source = "byteDataRepresentation.meterEdit", target = "esbJob.byteData.meterEdit")
  void byteDataRepresentationToIvdInboundEvent(
      @MappingTarget EsbJob esbJob, ByteDataRepresentation byteDataRepresentation);

  @Mapping(source = "vehicleDetailsResponse.vehicleId", target = "esbJob.vehicleDetails.id")
  @Mapping(source = "vehicleDetailsResponse.ipAddress", target = "esbJob.vehicleDetails.ipAddress")
  @Mapping(source = "vehicleDetailsResponse.imsi", target = "esbJob.vehicleDetails.imsi")
  @Mapping(source = "vehicleDetailsResponse.ivdNo", target = "esbJob.vehicleDetails.ivdNo")
  @Mapping(source = "vehicleDetailsResponse.driverId", target = "esbJob.vehicleDetails.driverId")
  void vehicleDetailsResponseToIvdInboundEvent(
      @MappingTarget EsbJob esbJob, VehicleDetailsResponse vehicleDetailsResponse);

  @Mapping(source = "jobNumber", target = "esbJob.byteData.jobNo")
  void jobNumberToIvdInboundEvent(@MappingTarget EsbJob esbJob, String jobNumber);

  @Mapping(source = "genericEventCommand.byteArray", target = "esbJob.byteArrayData.byteArray")
  @Mapping(
      source = "genericEventCommand.byteArraySize",
      target = "esbJob.byteArrayData.byteArraySize")
  @Mapping(
      source = "genericEventCommand.byteArrayMessage",
      target = "esbJob.byteArrayData.byteArrayMessage")
  void genericEventCommandToByteArrayData(
      @MappingTarget EsbJob esbJob, GenericEventCommand genericEventCommand);

  /**
   * Maps vehicleId to EsbJob
   *
   * @param esbJob esbJob (target)
   * @param productId productId(source)
   */
  @Mapping(source = "productId", target = "esbJob.byteData.productId")
  void productIdToIvdInboundEvent(@MappingTarget EsbJob esbJob, String productId);

  @Mapping(source = "id", target = "retryId")
  @Mapping(source = "payload.eventId", target = "eventId")
  @Mapping(source = "payload.occurredAt", target = "occurredAt")
  @Mapping(source = "payload.eventIdentifier", target = "eventIdentifier")
  @Mapping(source = "payload.eventDate", target = "eventDate")
  @Mapping(source = "payload.message", target = "message")
  IvdMessageRequest retryPayloadToIvdMessageRequest(RetrySchedulerData retrySchedulerData);
}
