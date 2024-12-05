package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequestServiceTypeEnum {
  NORMAL_STRUCTURE_MESSAGE(0),
  LOST_AND_FOUND_MESSAGE(1);

  private final int value;
}
