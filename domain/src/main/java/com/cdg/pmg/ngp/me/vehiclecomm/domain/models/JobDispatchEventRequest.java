package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.CustomEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.JobEventTypeEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.JobStatusEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.message.DomainEvent;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class JobDispatchEventRequest implements VehicleCommApplicationCommand {

  @Serial private static final long serialVersionUID = 1L;
  private UUID eventId;
  private LocalDateTime occurredAt;
  private LocalDateTime eventDate;

  @CustomEnum(
      enumClass = JobEventTypeEnum.class,
      message = "Event Name does not belong to JobEventTypeEnum")
  private JobEventTypeEnum eventName;

  private Integer messageId;
  private String jobNo;
  private String ivdNo;
  private String ipAddress;
  private String driverId;
  private String vehicleId;
  private Boolean ackFlag;
  private String broadcastMsg;
  private String messageSource;
  private String jobType;
  private String paymentMethod;
  private Boolean jobAssigned;
  private Double routingCost;
  private Boolean autobid;
  private Integer dispatchMethod;
  private Boolean priorityCustomer;
  private Boolean autoAcceptSuspend;
  private Integer autoAcceptFlag;
  private LocalDateTime pickupTime;
  private Double pickupLat;
  private Double pickupLng;
  private String pickupAddr;
  private Integer arrivalRequired;
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
  private String notes;
  private String waitingPoint;
  private String remark;
  private String productId;
  private List<TariffInfo> tariffInfo;
  private List<ExtraStopsInfo> extraStopsInfo;
  private String partnerDiscountType;
  private String partnerDiscountValue;
  private String partnerOrderId;
  private MultiStop multiStop;
  private CabchargePolicy cabchargePolicy;
  private Long newGst;
  private Integer newGstIncl;
  private String promoWaiveBookingFee;
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
  private Boolean sosFlag;
  private String ccNumber;
  private String ccExpiry;
  private String promoCode;
  private Double promoAmount;
  private Boolean loyaltyEnable;
  private Double loyaltyAmount;
  private Integer eta;
  private Boolean collectFare;
  private Boolean autoAssignFlag;
  private String paymentPlus;
  private Boolean enableCalloutButton;
  private Boolean cbdFlag;
  private Double cbdSurchargeAmount;
  private String dynamicPriceIndicator;
  private String privateField;
  private List<PlatformFeeItem> platformFeeItem;
  private String extendedOfferDisplayTime;
  private Boolean canReject;
  private Integer calloutStatus;
  private String offerableDevice;
  private String pickupPt;
  private JobStatusEnum jobStatus;
  private Double comfortProtectPremium;
  private LocalDateTime bookingDate;
  private String productType;
  private String productDesc;
  private Long offerTimeout;
  private String jobSurgeSigns;
  private String intermediatePt;
  private String intermediateZoneId;
  private String destAddr;
  private String destPoint;
  private String cpFreeInsurance;
  private Double levyWaiver;
  private Boolean isPremierAllowed;
  private String alert;
  private Integer displayInterval;
  private List<AdditionalChargeItem> additionalCharges;
  private Double totalDistance;
  private Integer tripDistance;
  private Integer pickupDistance;
  private Double nettFare;

  @Override
  public Class<? extends DomainEvent> fetchFailedEventClass() {
    return VehicleCommFailedRequest.class;
  }
}
