package com.cdg.pmg.ngp.me.vehiclecomm.domain.enums;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Surcharge {
  PEAK_PERIOD("PKHC", "peakPeriod"),
  AIRPORT("CHAC", "airport"),
  CBD_AREA("CBD", "cbd"),
  LOCATION("LOCC", "location"),
  DROP_OFF_LOCATION("DRLC", "dropOffLocation"),
  PRIVATE_BOOKING("PBKC", "privateBooking"),
  PUBLIC_HOLIDAY("PHDC", "publicHoliday"),
  LATE_NIGHT_10("LN10", "lateNight10"),
  LATE_NIGHT_20("LN20", "lateNight20"),
  LATE_NIGHT_35("LN35", "lateNight35"),
  LATE_NIGHT_50("LN50", "lateNight50"),
  PRE_HOLIDAY("PRHC", "preHoliday");

  private final String id;

  private final String name;

  public static String findNameById(String id) {
    return Arrays.stream(values())
        .filter(surcharge -> surcharge.getId().equals(id))
        .map(Surcharge::getName)
        .findFirst()
        .orElse(null);
  }
}
