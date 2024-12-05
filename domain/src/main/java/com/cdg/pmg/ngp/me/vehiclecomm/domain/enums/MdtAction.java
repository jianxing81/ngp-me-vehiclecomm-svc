package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** The Types of MDT Action . */
@Getter
@RequiredArgsConstructor
public enum MdtAction {
  LOGON_REQUEST("LOGON_REQUEST"),

  LOGOUT_REQUEST("LOGOUT_REQUEST"),

  POWER_UP("POWER_UP"),

  IVD_HARDWARE_INFO("IVD_HARDWARE_INFO");

  private String value;

  MdtAction(String value) {
    this.value = value;
  }
}
