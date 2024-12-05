package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** The Types of voice streaming event . */
@Getter
@RequiredArgsConstructor
public enum VoiceStream {
  START("START"),
  STOP("STOP");

  private String value;

  VoiceStream(String value) {
    this.value = value;
  }
}
