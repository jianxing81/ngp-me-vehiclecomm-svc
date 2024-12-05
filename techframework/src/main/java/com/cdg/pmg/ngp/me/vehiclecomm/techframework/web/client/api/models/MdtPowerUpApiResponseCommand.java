package com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.client.api.models;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

@Data
public class MdtPowerUpApiResponseCommand implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private Integer vehicleTypeId;
  private String paymentModuleIp;
  private Integer paymentModulePort;
  private Long jobNumberBlockStart;
  private Long jobNumberBlockEnd;
  private String companyId;
  private Boolean singJb;
  private Boolean enableOtpVerification;
  private String ipAddress;
  private String reasonCode;
  private Integer ivdNo;
}
