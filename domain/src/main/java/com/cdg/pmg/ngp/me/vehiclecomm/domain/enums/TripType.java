package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TripType {
  IMMEDIATE("0", "IMMEDIATE"),
  ADVANCE("1", "ADVANCE"),
  STREET("2", "STREET");

  private final String id;
  private final String value;

  public static String getTripTypeById(String id) {
    return Arrays.stream(values())
        .filter(tripTypeId -> tripTypeId.getId().equals(id))
        .map(TripType::getValue)
        .findFirst()
        .orElse(null);
  }
}
