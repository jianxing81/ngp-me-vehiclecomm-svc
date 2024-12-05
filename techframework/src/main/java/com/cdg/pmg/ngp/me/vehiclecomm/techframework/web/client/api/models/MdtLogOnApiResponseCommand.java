package com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.client.api.models;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.Product;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RooftopMessages;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class MdtLogOnApiResponseCommand implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private String logonStatus;
  private String driverName;
  private String driverId;
  private String timeLeftForSuspension;
  private Integer retainIvdStatus;
  private Integer cjOfferTimeout;
  private List<Product> productlist;
  private Integer pendingOnCallJobStatus;
  private Boolean loyaltyStatus;
  private Boolean ttsStatus;
  private Boolean taxiTourGuide;
  private RooftopMessages roofTopMessage;
  private Integer autoLogOffTimeout;
  private Integer maxRejectCount;
  private Integer maxMonitorDuration;
  private Integer warningThreshold;
  private Boolean starDriverGreeting;
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
  private String ipAddress;
  private String imsi;
  private Integer mobileId;
}
