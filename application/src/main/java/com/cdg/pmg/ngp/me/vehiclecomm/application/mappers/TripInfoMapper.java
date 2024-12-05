package com.cdg.pmg.ngp.me.vehiclecomm.application.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbJob;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.TripType;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.TariffInfo;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.TripInfo;
import java.math.BigDecimal;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(
    builder = @Builder(disableBuilder = true),
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TripInfoMapper {

  @Mapping(source = "esbJob.byteData.jobNo", target = "jobNo")
  @Mapping(source = "esbJob.byteData.tripId", target = "tripId")
  @Mapping(source = "esbJob.byteData.distance", target = "distance")
  @Mapping(source = "esbJob.byteData.totalDistance", target = "totalDistance")
  @Mapping(source = "esbJob.byteData.driverId", target = "driverId")
  @Mapping(source = "esbJob.vehicleDetails.id", target = "vehicleId")
  @Mapping(
      source = "esbJob.byteData.tripType",
      target = "tripType",
      qualifiedByName = "getTripType")
  @Mapping(source = "esbJob.gstAmount.value", target = "gst")
  @Mapping(
      source = "esbJob.byteData.gstInclusive",
      target = "gstInclusive",
      qualifiedByName = "binaryToBoolean")
  @Mapping(source = "esbJob.rewardsAmount.value", target = "rewardsValue")
  @Mapping(source = "esbJob.meterFareAmount.value", target = "meterFare")
  @Mapping(source = "esbJob.creditAdminAmount.value", target = "creditAdmin")
  @Mapping(source = "esbJob.byteData.paymentMethod", target = "paymentMethod")
  @Mapping(
      source = "esbJob.byteData.loyaltyMember",
      target = "loyaltyMember",
      qualifiedByName = "binaryToBoolean")
  @Mapping(source = "esbJob.balanceDueAmount.value", target = "balanceDue")
  @Mapping(source = "esbJob.voucherAmount.value", target = "voucherPaymentAmt")
  @Mapping(source = "esbJob.erpValue.value", target = "erp")
  @Mapping(source = "esbJob.paidAmount.value", target = "amountPaid")
  @Mapping(source = "esbJob.levyAmount.value", target = "levy")
  @Mapping(source = "esbJob.byteData.promoCode", target = "promoCode")
  @Mapping(source = "esbJob.promoAmount.value", target = "promoAmt")
  @Mapping(source = "esbJob.byteData.entryMode", target = "entryMode")
  @Mapping(source = "esbJob.totalFareAmount.value", target = "totalFare")
  @Mapping(source = "esbJob.byteData.productId", target = "productId")
  @Mapping(source = "esbJob.fixedPriceAmount.value", target = "fixedPrice")
  @Mapping(source = "esbJob.adminFeeDiscountAmount.value", target = "adminDiscount")
  @Mapping(source = "esbJob.comfortProtectPremiumValue.value", target = "comfortProtectPremium")
  @Mapping(source = "esbJob.platformFeeValue.value", target = "platformFee")
  @Mapping(source = "esbJob.platformFeeApplicability", target = "platformFeeApplicability")
  @Mapping(source = "esbJob.peakPeriodSurchargeAmount.value", target = "peakPeriod")
  @Mapping(source = "esbJob.cbdSurchargeAmount.value", target = "cbd")
  @Mapping(source = "esbJob.airportSurchargeAmount.value", target = "airport")
  @Mapping(source = "esbJob.locationSurchargeAmount.value", target = "location")
  @Mapping(source = "esbJob.dslcValue.value", target = "dslc")
  @Mapping(source = "esbJob.dropOffLocationSurcharge.value", target = "dropOffLocationSurcharge")
  @Mapping(source = "esbJob.privateBookingSurcharge.value", target = "privateBooking")
  @Mapping(source = "esbJob.publicHolidaySurcharge.value", target = "publicHoliday")
  @Mapping(source = "esbJob.lateNight10SurchargeAmount.value", target = "lateNight10")
  @Mapping(source = "esbJob.lateNight20SurchargeAmount.value", target = "lateNight20")
  @Mapping(source = "esbJob.lateNight35SurchargeAmount.value", target = "lateNight35")
  @Mapping(source = "esbJob.lateNight50SurchargeAmount.value", target = "lateNight50")
  @Mapping(source = "esbJob.preHolidaySurchargeAmount.value", target = "preHoliday")
  @Mapping(source = "esbJob.pickupLatitude.value", target = "pickupLat")
  @Mapping(source = "esbJob.pickupLongitude.value", target = "pickupLng")
  @Mapping(source = "esbJob.byteData.tripStartDt", target = "tripStartDt")
  @Mapping(source = "esbJob.byteData.tripEndDt", target = "tripEndDt")
  @Mapping(source = "esbJob.dropoffLatitude.value", target = "dropoffLat")
  @Mapping(source = "esbJob.dropoffLongitude.value", target = "dropoffLng")
  @Mapping(source = "esbJob.intermediateLatitude.value", target = "intermediateLat")
  @Mapping(source = "esbJob.intermediateLongitude.value", target = "intermediateLng")
  @Mapping(source = "esbJob.comfortProtectFreeInsurance", target = "cpFreeInsurance")
  @Mapping(source = "esbJob.byteData.ccNumber", target = "ccNumber")
  @Mapping(source = "esbJob.byteData.eventTime", target = "messageDate")
  @Mapping(source = "esbJob.byteData.updateType", target = "updateType")
  @Mapping(source = "esbJob.byteData.partnerDiscountType", target = "partnerDiscountType")
  @Mapping(source = "esbJob.partnerDiscountValueInfo.value", target = "partnerDiscountValue")
  @Mapping(source = "esbJob.byteData.partnerOrderId", target = "partnerOrderId")
  @Mapping(source = "esbJob.partnerDiscountAmount.value", target = "partnerDiscountAmt")
  @Mapping(source = "esbJob.vehicleDetails.ivdNo", target = "ivdNo")
  @Mapping(source = "esbJob.vehicleDetails.ipAddress", target = "ipAddress")
  @Mapping(source = "esbJob.discountAmount.value", target = "discount")
  @Mapping(source = "esbJob.byteData.additionalCharges", target = "additionalCharges")
  @Mapping(source = "esbJob.byteData.speed", target = "speed")
  @Mapping(source = "esbJob.byteData.direction", target = "heading")
  @Mapping(
      source = "esbJob.byteData.tariffInfo",
      target = "extraStop",
      qualifiedByName = "mapExtraStop")
  @Mapping(
      source = "esbJob.byteData.tariffInfo",
      target = "extraStopAmt",
      qualifiedByName = "mapExtraStopAmount")
  @Mapping(
      source = "esbJob.byteData.tariffInfo",
      target = "extraDist",
      qualifiedByName = "mapExtraDist")
  @Mapping(
      source = "esbJob.byteData.tariffInfo",
      target = "extraDistAmt",
      qualifiedByName = "mapExtraDistAmount")
  @Mapping(source = "esbJob.byteData.maximumSpeed", target = "maximumSpeed")
  @Mapping(source = "esbJob.byteData.meterEdit", target = "meterEdit")
  TripInfo ivdInbountEventToTripInfo(EsbJob esbJob);

  @Named("binaryToBoolean")
  default boolean binaryToBoolean(String binary) {
    return "1".equals(binary);
  }

  @Named("mapExtraStop")
  default Integer mapExtraStop(List<TariffInfo> tarffInfos) {
    return tarffInfos.stream()
        .filter(t -> VehicleCommAppConstant.EXTRA_STOP.equalsIgnoreCase(t.getTariffTypeCode()))
        .map(TariffInfo::getTariffUnit)
        .findFirst()
        .orElse(null);
  }

  @Named("mapExtraStopAmount")
  default BigDecimal mapExtraStopAmount(List<TariffInfo> tariffInfos) {
    return tariffInfos.stream()
        .filter(t -> VehicleCommAppConstant.EXTRA_STOP.equalsIgnoreCase(t.getTariffTypeCode()))
        .map(t -> BigDecimal.valueOf(t.getDiscountedTotal().getFare()))
        .findFirst()
        .orElse(null);
  }

  @Named("mapExtraDist")
  default Double mapExtraDist(List<TariffInfo> tariffInfos) {
    return tariffInfos.stream()
        .filter(t -> VehicleCommAppConstant.EXTRA_DISTANCE.equalsIgnoreCase(t.getTariffTypeCode()))
        .map(t -> t.getTariffUnit().doubleValue())
        .findFirst()
        .orElse(null);
  }

  @Named("mapExtraDistAmount")
  default BigDecimal mapExtraDistAmount(List<TariffInfo> tariffInfos) {
    return tariffInfos.stream()
        .filter(t -> VehicleCommAppConstant.EXTRA_DISTANCE.equalsIgnoreCase(t.getTariffTypeCode()))
        .map(t -> BigDecimal.valueOf(t.getDiscountedTotal().getFare()))
        .findFirst()
        .orElse(null);
  }

  @Named("getTripType")
  default String getTripType(String tripTypeId) {
    return TripType.getTripTypeById(tripTypeId);
  }
}
