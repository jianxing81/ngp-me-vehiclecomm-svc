package com.cdg.pmg.ngp.me.vehiclecomm.domain.entities;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DeviceType;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverEventVehicleRequest implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String driverId;
  private Integer ivdNo;
  private String vehicleId;
  private DeviceType deviceType;
  private String suspendTimeInMinutes;
  private Boolean autoBidFlag;
  private Boolean autoAcceptFlag;
}
