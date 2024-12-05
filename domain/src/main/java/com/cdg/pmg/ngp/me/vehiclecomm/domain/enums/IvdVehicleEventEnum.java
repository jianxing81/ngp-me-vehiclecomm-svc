package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IvdVehicleEventEnum {
  UPDATE_STC("137"),
  BREAK("134"),
  UPDATE_BUSY("135"),
  UPDATE_REGULAR("136"),
  IVD_EVENT("219"),
  VERIFY_OTP("227"),
  IVD_PING_RESPONSE("163"),
  REPORT_TOTAL_MILEAGE("212"),
  CHANGE_PIN_REQUEST("150"),
  DRIVER_PERFORMANCE_REQUEST("208"),
  CROSSING_ZONE("133"),
  CHANGE_SHIFT_UPDATE("172"),
  UPDATE_DESTINATION("171"),
  EXPRESSWAY_STATUS_UPDATE("132"),
  NOTIFY_STATIC_GPS("157"),
  FORGOT_PASSWORD_REQUEST("226");
  private final String id;
}
