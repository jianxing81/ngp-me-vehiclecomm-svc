package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PingMessageEvent {
  ESB_IVD_RESV("ESB_IVD_RESV"),
  ESB_APP_SEND("ESB_APP_SEND"),
  APP_RESV("APP_RESV"),
  APP_SEND("APP_SEND");
  private final String value;
}
