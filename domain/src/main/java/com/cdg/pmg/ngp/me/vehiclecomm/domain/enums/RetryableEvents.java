package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** This enum will store all the events which are applicable for retry */
@Getter
@RequiredArgsConstructor
public enum RetryableEvents {
  REPORT_TRIP_INFO_NORMAL("221");

  private final String eventId;

  public static RetryableEvents getEventByEventId(String id) {
    return Arrays.stream(values()).filter(e -> e.getEventId().equals(id)).findFirst().orElse(null);
  }
}
