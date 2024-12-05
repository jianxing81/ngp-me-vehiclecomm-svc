package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobEventLogEvent {
  NOTIFICATION_EVENT_CODE_CSA_ANNOUNCEMENT("NOTIFICATION_EVENT_CODE_CSA_ANNOUNCEMENT"),

  NOTIFICATION_EVENT_CODE_RESPONSE_FAILED("NOTIFICATION_EVENT_CODE_RESPONSE_FAILED"),

  TO_IVD_CALL_OUT_RESPONSE("TO_IVD_CALL_OUT_RESPONSE"),

  EVT_AJ_REMINDER_NOT_ACK("EVT_AJ_REMINDER_NOT_ACK"),

  EVT_AJ_REMINDER_ACK("EVT_AJ_REMINDER_ACK");

  private final String id;
}
