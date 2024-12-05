package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** Enum class for vehicle track event */
@Getter
@RequiredArgsConstructor
public enum VehicleTrack {
  START("START"),
  STOP("STOP");
  private final String value;
}
