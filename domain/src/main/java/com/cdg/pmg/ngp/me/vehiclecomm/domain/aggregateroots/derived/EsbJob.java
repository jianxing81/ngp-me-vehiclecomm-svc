package com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.AggregateRoot;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.constants.VehicleCommDomainConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.entities.IvdMessageRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.entities.VehicleDetails;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DriverAction;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.BadRequestException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.ByteArrayData;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.ByteData;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.CmsConfiguration;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.Coordinate;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.Money;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EsbJob extends AggregateRoot<String> {

  private IvdMessageRequest ivdMessageRequest;
  private transient CmsConfiguration cmsConfiguration;
  private VehicleDetails vehicleDetails;
  private ByteData byteData;
  private Coordinate vehicleLatitude;
  private Coordinate vehicleLongitude;
  private Coordinate pickupLatitude;
  private Coordinate pickupLongitude;
  private Coordinate dropoffLatitude;
  private Coordinate dropoffLongitude;
  private Coordinate intermediateLatitude;
  private Coordinate intermediateLongitude;
  private Money gstAmount;
  private Money rewardsAmount;
  private Money meterFareAmount;
  private Money creditAdminAmount;
  private Money balanceDueAmount;
  private Money voucherAmount;
  private Money erpValue;
  private Money paidAmount;
  private Money levyAmount;
  private Money promoAmount;
  private Money totalFareAmount;
  private Money fixedPriceAmount;
  private Money adminFeeDiscountAmount;
  private Money comfortProtectPremiumValue;
  private Money platformFeeValue;
  private Money partnerDiscountValueInfo;
  private Money partnerDiscountAmount;
  private Money peakPeriodSurchargeAmount;
  private Money cbdSurchargeAmount;
  private Money airportSurchargeAmount;
  private Money locationSurchargeAmount;
  private Money dslcValue;
  private Money dropOffLocationSurcharge;
  private Money privateBookingSurcharge;
  private Money publicHolidaySurcharge;
  private Money lateNight10SurchargeAmount;
  private Money lateNight20SurchargeAmount;
  private Money lateNight35SurchargeAmount;
  private Money lateNight50SurchargeAmount;
  private Money preHolidaySurchargeAmount;
  private Boolean comfortProtectFreeInsurance;
  private String platformFeeApplicability;
  private DriverAction driverAction;
  private ByteArrayData byteArrayData;
  private String driverId;
  private Integer responseMessageId;
  private Money discountAmount;
  private Integer heading;

  public void validateByteData() {
    if (!isByteDataValid()) {
      throw new BadRequestException(ErrorCode.INVALID_BYTE_DATA.getCode());
    }
  }

  public void validateIvdNo() {
    if (Objects.isNull(byteData.getIvdNo()))
      throw new BadRequestException(ErrorCode.IVD_NO_REQUIRED.getCode());
  }

  public boolean isAckRequired() {
    return checkIfAckRequired();
  }

  public void parseGeoLocations() {
    setGeoLocations();
  }

  public void parseMoney() {
    setMoneyValues();
  }

  public void validateVehicleDetails() {
    if (!isVehicleDetailsValid()) {
      throw new BadRequestException(ErrorCode.INVALID_VEHICLE_DETAILS.getCode());
    }
  }

  private void setMoneyValues() {
    gstAmount = adjustValueWithPrecision(byteData.getGst());
    rewardsAmount = adjustValueWithPrecision(byteData.getRewardsValue());
    meterFareAmount = adjustValueWithPrecision(byteData.getMeterFare());
    creditAdminAmount = adjustValueWithPrecision(byteData.getCreditAdmin());
    balanceDueAmount = adjustValueWithPrecision(byteData.getBalanceDue());
    voucherAmount = adjustValueWithPrecision(byteData.getVoucherPaymentAmount());
    paidAmount = adjustValueWithPrecision(byteData.getAmountPaid());
    erpValue = adjustValueWithPrecision(byteData.getErp());
    levyAmount = adjustValueWithPrecision(byteData.getLevy());
    promoAmount = adjustValueWithPrecision(byteData.getPromoAmt());
    totalFareAmount = adjustValueWithPrecision(byteData.getTotalPaid());
    fixedPriceAmount = adjustValueWithPrecision(byteData.getFixedPrice());
    discountAmount = adjustValueWithPrecision(byteData.getDiscount());
    platformFeeValue = adjustValueWithPrecision(byteData.getPlatformFee());
    adminFeeDiscountAmount = adjustValueWithPrecision(byteData.getAdminFeeDiscount());
    comfortProtectPremiumValue = adjustValueWithPrecision(byteData.getComfortProtectPremium());
    partnerDiscountAmount = adjustValueWithPrecision(byteData.getPartnerDiscountAmt());
    peakPeriodSurchargeAmount = adjustValueWithPrecision(byteData.getPeakPeriod());
    cbdSurchargeAmount = adjustValueWithPrecision(byteData.getCbd());
    airportSurchargeAmount = adjustValueWithPrecision(byteData.getAirport());
    locationSurchargeAmount = adjustValueWithPrecision(byteData.getLocation());
    dslcValue = adjustValueWithPrecision(Integer.parseInt(byteData.getDslc()));
    dropOffLocationSurcharge = adjustValueWithPrecision(byteData.getDropOffLocation());
    privateBookingSurcharge = adjustValueWithPrecision(byteData.getPrivateBooking());
    publicHolidaySurcharge = adjustValueWithPrecision(byteData.getPublicHoliday());
    lateNight10SurchargeAmount = adjustValueWithPrecision(byteData.getLateNight10());
    lateNight20SurchargeAmount = adjustValueWithPrecision(byteData.getLateNight20());
    lateNight35SurchargeAmount = adjustValueWithPrecision(byteData.getLateNight35());
    lateNight50SurchargeAmount = adjustValueWithPrecision(byteData.getLateNight50());
    preHolidaySurchargeAmount = adjustValueWithPrecision(byteData.getPreHoliday());

    // Custom method to adjust partner discount value based on partner discount type
    partnerDiscountValueInfo = adjustPartnerDiscountValue(byteData.getPartnerDiscountValue());
  }

  private Money adjustPartnerDiscountValue(Integer partnerDiscountValue) {
    if (partnerDiscountValue == null) {
      return null;
    }
    String partnerDiscountType = byteData.getPartnerDiscountType();
    return "F".equalsIgnoreCase(partnerDiscountType)
        ? adjustValueWithPrecision(partnerDiscountValue)
        : new Money(BigDecimal.valueOf(partnerDiscountValue));
  }

  private void setGeoLocations() {
    Double latitudeOrigin = cmsConfiguration.getLatitudeOrigin();
    Double longitudeOrigin = cmsConfiguration.getLongitudeOrigin();
    Long offsetMultiplier = cmsConfiguration.getOffsetMultiplier();
    Long coordinateMultiplier = cmsConfiguration.getCoordinateMultiplier();
    if (offsetMultiplier == null || offsetMultiplier == 0L) {
      throw new BadRequestException(ErrorCode.INVALID_OFFSET_MULTIPLIER.getCode());
    }
    vehicleLatitude =
        adjustOffsetWithOriginAndMultiplier(
            byteData.getOffsetLongitude(), latitudeOrigin, offsetMultiplier);
    vehicleLongitude =
        adjustOffsetWithOriginAndMultiplier(
            byteData.getOffsetLatitude(), longitudeOrigin, offsetMultiplier);
    pickupLatitude =
        adjustCoordinateWithOriginAndMultiplier(byteData.getPickupLat(), coordinateMultiplier);
    pickupLongitude =
        adjustCoordinateWithOriginAndMultiplier(byteData.getPickupLng(), coordinateMultiplier);
    dropoffLatitude =
        adjustCoordinateWithOriginAndMultiplier(byteData.getDropoffLat(), coordinateMultiplier);
    dropoffLongitude =
        adjustCoordinateWithOriginAndMultiplier(byteData.getDropoffLng(), coordinateMultiplier);
    intermediateLatitude =
        adjustCoordinateWithOriginAndMultiplier(
            byteData.getIntermediateLat(), coordinateMultiplier);
    intermediateLongitude =
        adjustCoordinateWithOriginAndMultiplier(
            byteData.getIntermediateLng(), coordinateMultiplier);
    heading =
        Objects.isNull(byteData.getDirection())
            ? 0
            : (int) (byteData.getDirection() * VehicleCommDomainConstant.DIRECTION_MULTIPLIER);
  }

  public void validateComfortProtect() {
    adjustComfortProtect();
  }

  public void validatePlatformFeeApplicability() {
    adjustPlatformFeeApplicability();
  }

  private boolean isByteDataValid() {
    return byteData != null;
  }

  private boolean isVehicleDetailsValid() {
    return vehicleDetails != null && vehicleDetails.getId() != null;
  }

  private boolean checkIfAckRequired() {
    return byteData.isAckRequired()
        || List.of(cmsConfiguration.getStoreForwardEvents().split(","))
            .contains(ivdMessageRequest.getEventIdentifier());
  }

  private void adjustComfortProtect() {
    boolean cpFreeInsurance =
        VehicleCommDomainConstant.BOOLEAN_TRUE_AS_BINARY.equals(byteData.getCpFreeInsurance());
    if (comfortProtectPremiumValue != null
        && comfortProtectPremiumValue.getValue().equals(BigDecimal.ZERO)
        && !cpFreeInsurance) {
      comfortProtectPremiumValue = null;
      comfortProtectFreeInsurance = null;
    }
  }

  private void adjustPlatformFeeApplicability() {
    platformFeeApplicability =
        byteData.getPlatformFeeApplicability() != null
                && VehicleCommDomainConstant.PLATFORM_FEE_APPLICABILITY.contains(
                    byteData.getPlatformFeeApplicability())
            ? byteData.getPlatformFeeApplicability()
            : null;
  }

  private Coordinate adjustOffsetWithOriginAndMultiplier(
      Double coordinate, double origin, long offsetMultiplier) {
    return coordinate == null ? null : new Coordinate(coordinate / offsetMultiplier + origin);
  }

  private Coordinate adjustCoordinateWithOriginAndMultiplier(
      Double coordinate, Long coordinateMultiplier) {
    return coordinate == null ? null : new Coordinate(coordinate / coordinateMultiplier);
  }

  public void setDriverAction(DriverAction driverAction) {
    setDriverActionType(driverAction);
  }

  private void setDriverActionType(DriverAction driverActions) {
    driverAction = driverActions;
  }

  public DriverAction findDriverActionForJobReject(Integer reasonCode) {
    return findDriverActionTypeForJobRejectType(reasonCode);
  }

  private DriverAction findDriverActionTypeForJobRejectType(Integer reasonCode) {
    switch (reasonCode) {
      case 4 -> {
        return DriverAction.REJECT;
      }
      case 5 -> {
        return DriverAction.TIME_OUT;
      }
      default -> {
        return DriverAction.SYSTEM_REJECT;
      }
    }
  }

  /**
   * Method to adjust value with precision
   *
   * @param value integer input
   * @return Money object
   */
  private Money adjustValueWithPrecision(Integer value) {
    return value == null
        ? null
        : new Money(
            BigDecimal.valueOf(value).divide(BigDecimal.valueOf(100), 2, RoundingMode.UNNECESSARY));
  }

  @Override
  public boolean equals(final Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * Method used to check cache required parameter is available or not
   *
   * @return true/false
   */
  public boolean validateCacheRequiredFields() {
    return (Objects.nonNull(byteData.getIvdNo()))
        && (Objects.nonNull(byteData.getReceivedMessageId()))
        && (Objects.nonNull(byteData.getReceivedMessageSn()));
  }
}
