package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** Enum class for emergency action */
@Getter
@RequiredArgsConstructor
public enum EmergencyAction {
  CLOSED("CLOSED");
  private final String value;
}
