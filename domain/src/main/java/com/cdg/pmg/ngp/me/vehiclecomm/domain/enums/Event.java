package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Event {
  DISABLE_AUTO_BID(0),
  ENABLE_AUTO_BID(1),
  ENABLE_AUTO_ACCEPT(2),
  APP_STATUS_SYNC(12);
  private final Integer value;
}
