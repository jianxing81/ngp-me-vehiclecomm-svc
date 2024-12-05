package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.message.DomainEvent;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/** CLass to generate Send Call-out message to driver app. */
@Data
@Builder
@AllArgsConstructor
public class DriverAppNotificationRequest implements VehicleCommApplicationCommand {

  @Serial private static final long serialVersionUID = 1L;

  private String vehicleId;
  private String driverId;
  private String jobNumber;
  private String broadcastMessage;
  private int autoBidStatus;
  private int autoBidBtnEnable;
  private Boolean isLogoutEvent;
  private Boolean isLoginEvent;
  private Integer type;

  @Override
  public Class<? extends DomainEvent> fetchFailedEventClass() {
    return VehicleCommFailedRequest.class;
  }
}
