package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import java.util.Objects;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** This class will have all the events which MDT sends to backend */
@Getter
@RequiredArgsConstructor
public enum ByteToBeanEvent {
  POWER_UP("129"),
  LOGON_REQUEST("130"),
  LOGOUT_REQUEST("131"),
  EXPRESSWAY_STATUS_UPDATE("132"),
  CROSSING_ZONE("133"),
  BREAK("134"),
  UPDATE_BUSY("135"),
  UPDATE_REGULAR("136"),
  UPDATE_STC("137"),
  METER_ON_STREETHAIL_JOB("139"),
  METER_OFF_STREETHAIL_JOB("140"),
  JOB_NUMBER_BLOCK_REQUEST("141"),
  IVD_HARDWARE_INFO("143"),
  CHANGE_PIN_REQUEST("150"),
  ACKNOWLEDGE_CONVERT_STREETHAIL("151"),
  JOB_MODIFICATION("152"),
  ACKNOWLEDGE_JOB_CANCELLATION("153"),
  JOB_ACCEPT("154"),
  JOB_REJECT("155"),
  ARRIVAL("156"),
  NOTIFY_STATIC_GPS("157"),
  CALL_OUT("158"),
  NO_SHOW("159"),
  METER_ON_DISPATCH("160"),
  METER_OFF_DISPATCH("161"),
  IVD_PING_RESPONSE("163"),
  EMERGENCY_REPORT("164"),
  SEND_MESSAGE("165"),
  RESPOND_TO_SIMPLE_MESSAGE("166"),
  RESPOND_TO_STRUCTURE_MESSAGE("167"),
  ADVANCED_JOB_REMIND("169"),
  UPDATE_DESTINATION("171"),
  CHANGE_SHIFT_UPDATE("172"),
  MDT_SYNC_REQUEST_EVENT_ID("176"),
  REJECT_JOB_MODIFICATION("179"),
  NOTIFY_ON_CALL("184"),
  EMERGENCY_INITIATED("185"),
  FALSE_ALARM("186"),
  VOICE_STREAMING_MESSAGE("187"),
  JOB_CONFIRM_ACKNOWLEDGE("188"),
  AUTO_ACCEPT_JOB_CONF_ACK("190"),
  FARE_CALCULATION_REQUEST("194"),
  DRIVER_PERFORMANCE_REQUEST("208"),
  REPORT_TOTAL_MILEAGE("212"),
  UPDATE_STOP("213"),
  IVD_EVENT("219"),
  REPORT_TRIP_INFO_NORMAL("221"),
  FORGOT_PASSWORD_REQUEST("226"),
  VERIFY_OTP("227"),
  MESSAGE_ACKNOWLEDGE("251");

  private final String id;

  /**
   * Method to find the enum by ID
   *
   * @param id id
   * @return ByteToBeanEvent
   */
  public static ByteToBeanEvent findById(String id) {
    return Stream.of(values())
        .filter(value -> Objects.equals(value.getId(), id))
        .findAny()
        .orElse(null);
  }
}
