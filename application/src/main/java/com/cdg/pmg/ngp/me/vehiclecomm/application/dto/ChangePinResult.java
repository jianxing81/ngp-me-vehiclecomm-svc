package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePinResult implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String ipAddress;
  private boolean isChangePinResult;
}
