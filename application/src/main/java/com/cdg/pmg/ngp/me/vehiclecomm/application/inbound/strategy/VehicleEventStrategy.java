package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbVehicle;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdVehicleEventEnum;

public interface VehicleEventStrategy {
  void handleVehicleEvent(EsbVehicle esbVehicle, String ivdVehicleEventTopic);

  IvdVehicleEventEnum vehicleEventType();
}
