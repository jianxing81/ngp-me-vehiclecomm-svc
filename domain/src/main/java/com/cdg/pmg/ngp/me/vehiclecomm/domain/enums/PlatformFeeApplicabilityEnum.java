package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlatformFeeApplicabilityEnum {
  YES('Y'),
  Y('Y'),
  NO('N'),
  N('N'),
  WAIVE('W'),
  W('W');

  private final char shortCode;
}
