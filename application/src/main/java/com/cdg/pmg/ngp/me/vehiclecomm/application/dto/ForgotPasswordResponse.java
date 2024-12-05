package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.*;

/** Response of forgot-password from the MDT service */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgotPasswordResponse implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private IvdInfo ivdInfo;
  private String passApproval;

  @Getter
  @Setter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class IvdInfo implements Serializable {
    @Serial private static final long serialVersionUID = 1L;
    private String imsi;
    private String ipAddress;
    private Boolean tlvFlag;
  }
}
