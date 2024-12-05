package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationStatus {
  SUCCESS("SUCCESS"),
  FAILED("FAILED");

  private final String value;
}
