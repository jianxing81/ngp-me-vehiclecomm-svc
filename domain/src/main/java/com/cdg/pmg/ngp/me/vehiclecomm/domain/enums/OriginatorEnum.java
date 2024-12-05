package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OriginatorEnum {
  IVR("IVR"),

  MDT("MDT");

  private String value;

  OriginatorEnum(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
