package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.*;

/** Request field for calling verify otp in MDT service */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyOtpRequest implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String code;
  private String mobileNo;
  private int ivdNo;
  private int messageId;
}
