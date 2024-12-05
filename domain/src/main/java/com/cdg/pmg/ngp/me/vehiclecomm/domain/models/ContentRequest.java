package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContentRequest implements Serializable {
  private String driverID;
  private String jobNo;
  private String vehicleID;
  private String jobType;
  private LocalDateTime pickUpTime;
  private String pickupAddr;
  private String pickupPt;
  private Double pickupLat;
  private Double pickupLng;
  private String destAddr;
  private String destPt;
  private Double bookingFee;
  private Integer jobStatus;
  private Double destLat;
  private Double destLng;
  private String notes;
  private String passengerName;
  private LocalDateTime bookingDate;
  private Integer eta;
  private String promoCode;
  private Double promoAmount;
  private Double loyaltyAmount;
  private String paymentMode;
  private Integer paymentModeID;
  private String productType;
  private String productDesc;
  private String pdtID;
  private String ccNumber;
  private String ccExpiry;
  private Boolean autoAssignFlag;
  private Integer autoAcceptFlag;
  private Long offerTimeOut;
  private String jobSurgeSigns;
  private Long routingCost;
  private String privateField;
  private String cpFreeInsurance;
  private Double comfortProtectPremium;
  private String intermediateAddr;
  private Double intermediateLat;
  private Double intermediateLng;
  private String intermediatePt;
  private String intermediateZoneId;
  private List<PlatformFeeItem> platformFeeItem;
  private Integer type;
  private Boolean advanceBooking;
  private String alert;
}
