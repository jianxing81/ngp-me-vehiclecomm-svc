package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.CabchargePolicy;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.DiscountedTotal;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.ExtraStopsInfo;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.MultiStop;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobOffersResponse implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private LocalDateTime eventDate;
  private String jobNo;
  private String ivdNo;
  private String ipAddress;
  private String driverId;
  private String vehicleId;
  private String messageSource;
  private String jobType;
  private String paymentMethod;
  private Integer dispatchMethod;
  private Boolean priorityCustomer;
  private Boolean autoAcceptSuspend;
  private LocalDateTime pickupTime;
  private Double pickupLat;
  private Double pickupLng;
  private String pickupAddr;
  // JobEvent doesn't have it
  private String bookingChannel;
  private Double bookingFee;
  private Integer fareType;
  private Double levy;
  private String jobMeritPoint;
  private Double deposit;
  private Integer noShowTiming;
  private String accountId;
  private String companyName;
  private String passengerName;
  private String passengerContactNumber;
  private Double destLat;
  private Double destLng;
  private String destAddr;
  private String notes;
  private String waitingPoint;
  private String remark;
  private String productId;
  private List<TariffInfo> tariffInfo;
  private List<ExtraStopsInfo> extraStopsInfo;
  private MultiStop multiStop;
  private CabchargePolicy cabchargePolicy;
  private Long newGst;
  private Integer newGstIncl;
  private LocalDateTime newGstEffectiveDate;
  private Long newAdminValue;
  private Long newAdminDiscountVal;
  private Integer newAdminType;
  private Integer newAdminGstMsg;
  private LocalDateTime newAdminEffectiveDate;
  private Long currentGst;
  private Integer currentGstIncl;
  private Long currentAdminVal;
  private Long currentAdminDiscountVal;
  private Integer currentAdminType;
  private Integer currentAdminGstMsg;
  private String ccNumber;
  private String ccExpiry;
  private String promoCode;
  private Double promoAmount;
  private Boolean loyaltyEnable;
  private Double loyaltyAmount;
  private String eta;
  private Boolean collectFare;
  private Boolean autoAssignFlag;
  private String paymentPlus;
  private Boolean enableCalloutButton;
  private Double cbdSurchargeAmount;
  private String dynamicPriceIndicator;
  private String privateField;
  private List<PlatformFeeItem> platformFeeItem;
  private String extendedOfferDisplayTime;
  private String productDesc;
  private String offerableDevice;
  private String jobStatus;
  private String alert;

  @Data
  public static class PlatformFeeItem implements Serializable {

    private String platformFeeApplicability;
    private Double thresholdLimit;
    private Double feeBelowThresholdLimit;
    private Double feeAboveThresholdLimit;
  }

  @Data
  public static class TariffInfo implements Serializable {

    private String tariffTypeCode;
    private Integer tariffUnit;
    private DiscountedTotal discountedTotal;
  }
}
