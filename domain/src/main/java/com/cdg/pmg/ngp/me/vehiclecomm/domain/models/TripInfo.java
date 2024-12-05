package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripInfo {

  private String jobNo;
  private String tripId;
  private Double distance;
  private String totalDistance;
  private String driverId;
  private String vehicleId;
  private String tripType;
  private BigDecimal gst;
  private boolean gstInclusive;
  private BigDecimal rewardsValue;
  private BigDecimal meterFare;
  private BigDecimal creditAdmin;
  private String paymentMethod;
  private boolean isLoyaltyMember;
  private BigDecimal balanceDue;
  private BigDecimal voucherPaymentAmt;
  private BigDecimal erp;
  private BigDecimal amountPaid;
  private BigDecimal levy;
  private String promoCode;
  private BigDecimal promoAmt;
  private String entryMode;
  private Integer extraStop;
  private BigDecimal extraStopAmt;
  private Double extraDist;
  private BigDecimal extraDistAmt;
  private BigDecimal totalFare;
  private String productId;
  private BigDecimal fixedPrice;
  private BigDecimal adminDiscount;
  private BigDecimal comfortProtectPremium;
  private BigDecimal platformFee;
  private String platformFeeApplicability;
  private BigDecimal peakPeriod;
  private BigDecimal cbd;
  private BigDecimal airport;
  private BigDecimal location;
  private BigDecimal dslc;
  private BigDecimal dropOffLocationSurcharge;
  private BigDecimal privateBooking;
  private BigDecimal publicHoliday;
  private BigDecimal lateNight10;
  private BigDecimal lateNight20;
  private BigDecimal lateNight35;
  private BigDecimal lateNight50;
  private BigDecimal preHoliday;
  private String comfortdelgroContactlessCarddata;
  private String comfortdelgroTripInfo;
  private Double pickupLat;
  private Double pickupLng;
  private String pickupAddressRef;
  private String pickupAddress;
  private LocalDateTime pickupDate;
  private LocalDateTime tripStartDt;
  private LocalDateTime tripEndDt;
  private Double dropoffLat;
  private Double dropoffLng;
  private String dropoffAddressRef;
  private String dropoffAddress;
  private Double intermediateLat;
  private Double intermediateLng;
  private Boolean cpFreeInsurance;
  private String ccNumber;
  private LocalDateTime messageDate;
  private String updateType;
  private String privateField;
  private String partnerDiscountType;
  private BigDecimal partnerDiscountValue;
  private String partnerOrderId;
  private BigDecimal partnerDiscountAmt;
  private String ivdNo;
  private String ipAddress;
  private BigDecimal discount;
  private Double speed;
  private Integer heading;
  private String maximumSpeed;
  private Boolean meterEdit;
  // Driver Fee requirement ,jira no : NGPME-9210
  private List<AdditionalChargeItem> additionalCharges;
}
