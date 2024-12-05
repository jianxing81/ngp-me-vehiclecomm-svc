package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.CustomEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DeviceType;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.Event;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.message.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/** The VehicleEventRequest class. */
@Data
@Builder
@AllArgsConstructor
public class VehicleEventRequest implements VehicleCommApplicationCommand {
  private Integer ivdNo;
  private String driverId;
  private String vehicleId;
  private String status;

  @CustomEnum(enumClass = DeviceType.class, message = "Event Name does not belong to DeviceType")
  private DeviceType deviceType;

  @CustomEnum(enumClass = Event.class, message = "Event Name does not belong to VehicleEvent")
  private Event event;

  @Override
  public Class<? extends DomainEvent> fetchFailedEventClass() {
    return VehicleCommFailedRequest.class;
  }
}
