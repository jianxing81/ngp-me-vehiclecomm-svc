package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DeviceType;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DriverAction;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.*;

/** Request fields for calling the JobDispatch service */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobDispatchDetailsRequest implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String driverId;
  private String vehicleId;
  private Double latitude;
  private Double longitude;
  private String eta;
  private DriverAction driverAction;
  private LocalDateTime eventTime;
  private DeviceType deviceType;
}
