package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationTypeEnum {
  TRANSACTIONAL("transactional"),
  PROMOTION("promotion");

  @JsonValue private final String value;
}
