package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
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
public class MdtApiResponse implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private Integer ivdNo;
  private Boolean logOffStatus;
  private String driverId;
  private String ipAddress;
  private LocalDateTime timeStamp;
  private String logonStatus;
  private String driverName;
  private String timeLeftForSuspension;
  private Integer retainIvdStatus;
  private Integer cjOfferTimeout;
  private List<Product> productlist;
  private Integer pendingOnCallJobStatus;
  private Boolean loyaltyStatus;
  private Boolean ttsStatus;
  private Boolean taxiTourGuide;
  private List<RooftopMessages> rooftopMessages;
  private BigDecimal autoLogOffTimeout;
  private Integer maxRejectCount;
  private Integer maxMonitorDuration;
  private Integer warningThreshold;
  private Boolean starDriver;
  private String starDriverGreeting;
  private Integer mdtBlockingSyncInterval;
  private String paymentAnnouncementMessage;
  private Integer autoBidFlag;
  private Integer defaultAutoBidAccept;
  private Integer autoBidJobOfferTimeOut;
  private Integer autoStcDistance;
  private Integer stcTrackingDistance;
  private Integer autoAcceptDialogBoxTimeOut;
  private String uniqueDriverId;
  private Boolean adminGstFlag;
  private Boolean forceChangePwd;
  private Boolean forgotPwdwithOtp;
  private String vehiclePlateNum;
  private String imsi;
  private Integer mobileId;
  private Integer vehicleTypeId;
  private String paymentModuleIp;
  private Integer paymentModulePort;
  private Long jobNumberBlockStart;
  private Long jobNumberBlockEnd;
  private String companyId;
  private Boolean singJb;
  private Boolean enableOtpVerification;
  private String reasonCode;
  private LocalDateTime powerUpDt;
}
