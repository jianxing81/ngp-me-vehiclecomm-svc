package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IvdStatusEnum {
  FREE("0", "FREE"),
  BUSY("1", "BUSY"),
  BREAK("2", "BREAK"),
  STC("3", "STC"),
  ONCALL("4", "ONCALL"),
  POB("5", "POB"),
  ARRIVAL("6", "ARRIVAL"),
  NO_SHOW("7", "NO_SHOW"),
  PAYMENT("8", "PAYMENT"),
  FORWARD("9", "FORWARD"),
  POWEROFF("10", "POWEROFF"),
  OFFLINE("11", "OFFLINE"),
  OFFERED("12", "OFFERED"),
  QUERY("13", "QUERY"),
  SUSPEND("14", "SUSPEND"),
  SUSPEND_ACK("15", "SUSPENDACK"),
  SUSPEND_NO_ACK("16", "SUSPENDNA");

  private final String id;
  private final String value;

  public static String getIvdStatusById(String id) {
    return Arrays.stream(values())
        .filter(ivdStatusId -> ivdStatusId.getId().equals(id))
        .map(IvdStatusEnum::getValue)
        .findFirst()
        .orElse(null);
  }
}
