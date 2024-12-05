package com.cdg.pmg.ngp.me.vehiclecomm.domain.entities;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.message.DomainEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommApplicationCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommFailedRequest;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverEventRequest implements VehicleCommApplicationCommand {

  private String eventIdentifier;

  private String commandType;

  private String commandVariable;

  private List<DriverEventVehicleRequest> vehicleSuspends;

  @Override
  public Class<? extends DomainEvent> fetchFailedEventClass() {
    return VehicleCommFailedRequest.class;
  }
}
