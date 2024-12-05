package com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.client.api.models;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class MdtLogOffApiResponseCommand implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private Integer ivdNo;
  private Boolean logOffStatus;
  private String driverId;
  private String ipAddress;
  private LocalDateTime timeStamp;
}
