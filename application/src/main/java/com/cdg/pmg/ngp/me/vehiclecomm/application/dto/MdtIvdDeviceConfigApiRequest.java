package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class MdtIvdDeviceConfigApiRequest implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String messageId;
  private List<IvdDeviceConfig> ivdDeviceConfig;
  private Integer ivdNo;
  private String serialNo;
  private String ipAddress;
  private Boolean isAckRequired;
  private LocalDateTime timeStamp;
}
