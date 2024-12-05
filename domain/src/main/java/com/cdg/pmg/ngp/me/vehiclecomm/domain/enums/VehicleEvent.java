package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VehicleEvent {
  ARRIVE_PICKUP("ARRIVE_PICKUP"),
  START_TRIP("START_TRIP"),
  NO_SHOW("NO_SHOW"),
  NOTIFY_ONCALL("ONCALL"),
  ARRIVE_INTERMEDIATE("ARRIVE_INTERMEDIATE"),
  END_TRIP("END_TRIP"),
  STC("STC"),
  REGULAR("REGULAR_REPORT"),
  DISABLE_AUTO_BID("DISABLE_AUTO_BID"),
  ENABLE_AUTO_BID("ENABLE_AUTO_BID"),
  ENABLE_AUTO_ACCEPT("ENABLE_AUTO_ACCEPT"),
  FALSE_ALARM("FALSE_ALARM"),
  EMERGENCY_REPORT("EMERGENCY_REPORT"),
  BREAK("BREAK"),
  BUSY("BUSY"),
  EXPRESSWAY_STATUS_UPDATE("EXPRESSWAY_STATUS_UPDATE"),
  NOTIFY_STATIC_GPS("NOTIFY_STATIC_GPS"),
  POWER_UP("POWER_UP");

  private final String value;
}
