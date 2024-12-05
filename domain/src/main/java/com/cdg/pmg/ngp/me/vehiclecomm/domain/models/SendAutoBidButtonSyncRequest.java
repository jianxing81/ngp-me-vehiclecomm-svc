package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.message.DomainEvent;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/** CLass to generate Auto Bid button Sync message to driver app. */
@Data
@Builder
@AllArgsConstructor
public class SendAutoBidButtonSyncRequest implements VehicleCommApplicationCommand {
  @Serial private static final long serialVersionUID = 1L;

  private String vehicleId;
  private String driverId;
  private String autoBidStatus;
  private String autoBidBtnEnable;
  private Integer type;

  @Override
  public Class<? extends DomainEvent> fetchFailedEventClass() {
    return VehicleCommFailedRequest.class;
  }
}
