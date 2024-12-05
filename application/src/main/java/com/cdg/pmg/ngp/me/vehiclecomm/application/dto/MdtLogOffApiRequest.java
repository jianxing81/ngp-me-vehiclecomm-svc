package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MdtLogOffApiRequest implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String messageId;
  private Integer ivdNo;
  private LocalDateTime timeStamp;
  private String driverId;
  private Integer totalMileage;
  private Double offsetLatitude;
  private Double offsetLongitude;
  private String serialNo;
  private String ipAddress;
  private Boolean isAckRequired;
}
