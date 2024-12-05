package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MdtLogOnApiRequest implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String messageId;
  private String mobileNumber;
  private Double offsetLatitude;
  private Double offsetLongitude;
  private Integer mobileId;
  private LocalDateTime logonTimeStamp;
  private String driverPin;
  private String pendingOnCallJobNo;
  private Integer totalMileage;
  private String driverId;
  private String serialNo;
  private String ipAddress;
  private Boolean isAckRequired;
}
