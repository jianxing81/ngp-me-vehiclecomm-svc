package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RcsaEvents {
  SEND_MESSAGE("SEND_MESSAGE"),
  VOICE_STREAMING_MESSAGE("VOICE_STREAMING_MESSAGE"),
  FALSE_ALARM("FALSE_ALARM"),
  EMERGENCY_REPORT("EMERGENCY_REPORT"),
  EMERGENCY_INITIATED("EMERGENCY_INITIATED"),
  RESPOND_TO_STRUCTURE_MESSAGE("RESPOND_TO_STRUCTURE_MESSAGE"),
  RESPOND_TO_SIMPLE_MESSAGE("RESPOND_TO_SIMPLE_MESSAGE");
  private final String value;
}
