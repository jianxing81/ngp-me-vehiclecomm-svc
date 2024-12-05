package com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.AdditionalChargeItem;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.TariffInfo;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ByteData implements Serializable {

  @Serial private static final long serialVersionUID = 7725773171700538444L;
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
  private String loyaltyMember;
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
  private String messageContent;
  private String corpCardNo;
  private String accountNumber;
  private String requestId;
  private List<TariffInfo> tariffInfo;
  private String voiceStream;
  private String mobileNumber;
  private String vehiclePlateNumber;
  private Integer acknowledgement;
  private String emergencyId;
  private String uniqueMsgId;
  private Integer selection;
  private String eventContent;
  private String code;
  private Integer refNo;
  private Integer seqNo;
  private Integer totalMileage;
  private String oldPin;
  private String newPin;
  private String zoneId;
  private String ivdEventStatus;
  private Integer eventId;
  private String receivedMessageSn;
  private String receivedMessageId;
  private Integer discount;
  private Integer noShowEventType;
  private Boolean isValidLocation;
  private String maximumSpeed;
  private Boolean meterEdit;
  // Driver Fee requirement ,jira no : NGPME-9210
  private List<AdditionalChargeItem> additionalCharges;
  private boolean ackRequired;
  private Integer ivdStatus;

  // Additional fields in logon
  private Integer mobileId;
  private LocalDateTime logonTimeStamp;
  private String driverPin;
  private String pendingOnCallJobNo;
  private String serialNo;
  private String ipAddress;

  // Additional fields in logoff
  private LocalDateTime timeStamp;

  // Additional fields in powerUp
  private String mobileNo;
  private String firmwareVersion;
  private String modelNo;
  private List<ByteData.FileDetails> fileList;
  private String selfTestResult;
  private String imsi;
  private String telcoId;
  private String meterVersion;
  private String tariffCheckSum;
  private String firmwareCheckSum;
  private String gisVersion;
  private String mapVersion;
  private String screenSaverVersion;
  private String jobNoBlockStart;
  private String jobNoBlockEnd;
  private ByteData.IvdInfoDetail ivdInfoList;

  // Additional Fields for IVD Device Config
  private List<ByteData.IvdDeviceConfig> ivdDeviceConfig;

  @Getter
  @Setter
  public static class FileDetails implements Serializable {
    private String fileName;
    private String fileCheckSum;
  }

  @Getter
  @Setter
  public static class IvdInfoDetail implements Serializable {
    private String ivdModel;
    private String ivdParam;
    private String ivdValue;
  }

  @Getter
  @Setter
  public static class IvdDeviceConfig implements Serializable {
    private Integer deviceInfoType;
    private Integer deviceComponentID;
    private Integer facilityComponentID;
    private Integer deviceAttributeId;
    private String deviceAttributeValue;
  }
}
