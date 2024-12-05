package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
/* The type Job type. */
public enum DeviceType {
  ANDROID("ANDROID"),
  IPHONE("IPHONE"),
  MDT("MDT");

  private String value;

  DeviceType(String value) {
    this.value = value;
  }
}
