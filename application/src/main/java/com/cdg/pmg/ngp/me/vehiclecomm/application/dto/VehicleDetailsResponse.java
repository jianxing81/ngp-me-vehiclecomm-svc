package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/** Response of vehicle details from the MDT service */
public class VehicleDetailsResponse implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private String vehicleId;
  private String imsi;
  private String ipAddress;
  private String driverId;
  private Integer ivdNo;
}
