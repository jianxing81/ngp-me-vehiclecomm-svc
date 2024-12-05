package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationChannel {
  EMAIL("EMAIL"),
  SMS("SMS"),
  PUSH("PUSH"),
  OPENAPI("OPENAPI");

  private final String value;
}
