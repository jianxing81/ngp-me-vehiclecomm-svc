package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VehicleMdtEventType {
  CROSSING_ZONE("CROSSING_ZONE"),
  CHANGE_SHIFT_UPDATE("CHANGE_SHIFT_UPDATE"),
  DESTINATION_UPDATE("DESTINATION_UPDATE");
  private final String value;
}
