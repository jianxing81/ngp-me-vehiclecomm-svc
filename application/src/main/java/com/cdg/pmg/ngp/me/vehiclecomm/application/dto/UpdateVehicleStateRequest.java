package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DeviceType;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
/** Request field for calling the Vehicle service */
public class UpdateVehicleStateRequest implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String jobNo;
  private String driverId;
  private DeviceType deviceType;
  private LocalDateTime eventTime;
  private Double latitude;
  private Double longitude;
  private Double speed;
  private Integer heading;
  private Integer noShowEventType;
}
