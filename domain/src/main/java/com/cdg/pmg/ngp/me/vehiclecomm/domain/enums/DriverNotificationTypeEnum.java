package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DriverNotificationTypeEnum {
  /* Driver notification type */
  APP_AUTO_BID_BTN(9),
  APP_AUTO_MSG(10),
  APP_AUTO_BID_STATUS(8),
  JOB_MODIFICATION(5),
  MDT_ALIVE_TYPE(6),
  JOB_CANCELLED(4),
  JOB_CONFIRMED(2),
  AVAILABLE_JOB(1),
  APP_STATUS_SYNC(12);

  private Integer value;

  DriverNotificationTypeEnum(Integer value) {
    this.value = value;
  }
}
