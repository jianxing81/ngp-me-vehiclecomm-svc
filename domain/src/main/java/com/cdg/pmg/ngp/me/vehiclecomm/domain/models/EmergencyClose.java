package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import java.io.Serial;
import java.io.Serializable;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyClose implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private Integer id;

  private Integer mobileId;

  private String ipAddress;

  private Integer ivdNo;

  private String emergId;
}
