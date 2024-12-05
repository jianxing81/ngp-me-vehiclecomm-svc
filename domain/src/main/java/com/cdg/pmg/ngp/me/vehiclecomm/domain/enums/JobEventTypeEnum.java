package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobEventTypeEnum {
  JOB_MODIFY("JOB_MODIFY"),
  CALLOUT_RESULT("CALLOUT_RESULT"),
  MDT_ALIVE("MDT_ALIVE"),
  LEVY_UPDATE("LEVY_UPDATE"),
  JOB_OFFER("JOB_OFFER"),
  STREET_JOB("STREET_JOB"),
  JOB_CANCEL("JOB_CANCEL"),
  JOB_CONFIRM("JOB_CONFIRM"),
  MDT_COMPLETED("MDT_COMPLETED");
  private final String value;
}
