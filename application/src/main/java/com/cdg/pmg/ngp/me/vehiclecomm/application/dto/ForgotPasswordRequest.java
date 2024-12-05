package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.*;

/** Request of forgot-password from the MDT service */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgotPasswordRequest implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String mobileNo;
  private Integer ivdNo;
  private String vehiclePlateNum;
  private String messageId;
}
