package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IvdMessageEnum {

  // NOTE: Add event enums in the increasing order of id
  // outbound enum
  TO_IVD_CHANGEPASSWORD_CONFIRMATION("5"),
  JOB_NUMBER_BLOCK_RESPONSE_MESSAGE_ID("15"),
  VERIFY_OTP_RESPONSE_MESSAGE_ID("29"),
  FARE_CALCULATION_RESPONSE("39"),
  DRIVER_PERFORMANCE_RESPONSE_MESSAGE_ID("108"),
  METER_ON_STREETHAIL_JOB("139"),
  METER_OFF_STREETHAIL_JOB("140"),
  JOB_MODIFICATION("152"),
  ACKNOWLEDGE_CONVERT_STREETHAIL("151"),
  ACKNOWLEDGE_JOB_CANCELLATION("153"),
  JOB_ACCEPT("154"),
  JOB_REJECT("155"),
  ARRIVAL("156"),
  CALL_OUT("158"),
  NO_SHOW("159"),
  METER_ON_DISPATCH("160"),
  METER_OFF_DISPATCH("161"),
  SEND_MESSAGE("165"),
  ADVANCED_JOB_REMIND("169"),
  MDT_SYNC_REQUEST_EVENT_ID("176"),
  REJECT_JOB_MODIFICATION("179"),
  NOTIFY_ONCALL("184"),
  VOICE_STREAMING_MESSAGE("187"),
  FALSE_ALARM("186"),
  EMERGENCY_INITIATED("185"),
  JOB_CONFIRM_ACKNOWLEDGE("188"),
  AUTO_ACCEPT_JOB_CONF_ACK("190"),
  FARE_CALCULATION_REQUEST("194"),
  UPDATE_STOP("213"),
  REPORT_TRIP_INFO_NORMAL("221"),
  MESSAGE_ACKNOWLEDGE("251"),
  MESSAGE_AKNOWLEDGE("125"),
  JOB_NUMBER_BLOCK_REQUEST("141"),
  EMERGENCY_REPORT("164"),
  /** mentions it’s called 'EVT_AJ_REMINDER_NOT_ACK' in the requirement doc */
  EVENT_ADVJOB_REMINDER_NACK("312"),

  /** mentions it’s called 'EVT_AJ_REMINDER_ACK' in the requirement doc */
  EVENT_ADVJOB_REMINDER_ACK("320"),
  RESPOND_TO_STRUCTURE_MESSAGE("167"),
  RESPOND_TO_SIMPLE_MESSAGE("166");
  private final String id;

  public static IvdMessageEnum getEventTypeByEventId(String id) {
    return Arrays.stream(values()).filter(e -> e.getId().equals(id)).findFirst().orElse(null);
  }
}
