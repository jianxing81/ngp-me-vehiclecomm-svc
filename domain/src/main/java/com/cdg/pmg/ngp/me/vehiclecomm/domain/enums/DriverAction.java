package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** The type Job type. */
@Getter
@RequiredArgsConstructor
public enum DriverAction {
  ACCEPT("ACCEPT"),
  MODIFY_REJECT("MODIFY_REJECT"),
  REJECT("REJECT"),
  TIME_OUT("TIME_OUT"),
  SYSTEM_REJECT("SYSTEM_REJECT"),
  CONFIRM_ACK("CONFIRM_ACK"),
  CANCEL_ACK("CANCEL_ACK"),
  MODIFY_ACK("MODIFY_ACK"),
  CALL_OUT("CALL_OUT"),
  JOB_SYNC_ACK("JOB_SYNC_ACK"),
  CONFIRM_AUTOACCEPT_ACK("CONFIRM_AUTOACCEPT_ACK"),
  STREET_CONVERT_ACK("STREET_CONVERT_ACK");

  private String value;

  DriverAction(String value) {
    this.value = value;
  }
}
