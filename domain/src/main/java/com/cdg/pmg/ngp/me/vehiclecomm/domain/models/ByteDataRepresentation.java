package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ByteDataRepresentation {

  private String jobNo;
  private String tripId;
  private String eta;
  private String updateType;
  private Integer dispatchLevel;
  private Integer reasonCode;
  private Integer ivdNo;
  private String driverId;
  private Double offsetLatitude;
  private Double offsetLongitude;
  private LocalDateTime eventTime;
  private Double speed;
  private Integer direction;
  private Integer serialNumber;
  private Integer messageId;
  private String distance;
  private String totalDistance;
  private String tripType;
  private Integer gst;
  private String gstInclusive;
  private Integer rewardsValue;
  private Integer meterFare;
  private Integer creditAdmin;
  private String paymentMethod;
  private String isLoyaltyMember;
  private Integer balanceDue;
  private Integer voucherPaymentAmount;
  private Integer erp;
  private Integer amountPaid;
  private Integer totalPaid;
  private Integer levy;
  private String promoCode;
  private Integer promoAmt;
  private String entryMode;
  private String productId;
  private Integer fixedPrice;
  private Integer adminFeeDiscount;
  private Integer comfortProtectPremium;
  private Integer platformFee;
  private String platformFeeApplicability;
  private Double pickupLat;
  private Double pickupLng;
  private LocalDateTime tripStartDt;
  private LocalDateTime tripEndDt;
  private Double dropoffLat;
  private Double dropoffLng;
  private Double intermediateLat;
  private Double intermediateLng;
  private String cpFreeInsurance;
  private String ccNumber;
  private String partnerDiscountType;
  private Integer partnerDiscountValue;
  private String partnerOrderId;
  private Integer partnerDiscountAmt;
  private Integer peakPeriod;
  private Integer airport;
  private Integer cbd;
  private Integer location;
  private Integer dropOffLocation;
  private Integer privateBooking;
  private Integer publicHoliday;
  private Integer lateNight10;
  private Integer lateNight20;
  private Integer lateNight35;
  private Integer lateNight50;
  private Integer preHoliday;
  private String dslc;

  private String corpCardNo;
  private String accountNumber;
  private String requestId;
  private List<TariffInfo> tariffInfo;
  private String voiceStreamArray;
  private String mobileNumber;
  private String vehiclePlateNumber;
  private Integer acknowledgement;
  private String emergencyId;
  private String uniqueMsgId;
  private Integer selection;
  private String messageContent;
  private String eventContent;
  private String code;
  private Integer refNo;
  private Integer seqNo;
  private Integer totalMileage;
  private String oldPin;
  private String newPin;
  private String zoneId;
  private Integer ivdStatus;
  private Integer eventId;
  private String receivedMessageId;
  private String receivedMessageSn;
  private Integer discount;
  private Integer noShowEventType;
  private Boolean isValidLocation;
  private boolean ackRequired;
  private String maximumSpeed;
  private Boolean meterEdit;

  // Driver Fee requirement ,jira no : NGPME-9210
  private List<AdditionalChargeItem> additionalCharges;
}
