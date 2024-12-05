package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class VehicleTrackCommand implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private Integer id;

  private Integer interval;
  private Integer duration;
  private String ipAddress;
  private Integer ivdNo;
}
