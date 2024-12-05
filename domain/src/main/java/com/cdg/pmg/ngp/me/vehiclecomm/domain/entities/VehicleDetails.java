package com.cdg.pmg.ngp.me.vehiclecomm.domain.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class VehicleDetails extends BaseEntity<String> {

  private String imsi;
  private String ipAddress;
  private String ivdNo;
  private String driverId;
}
