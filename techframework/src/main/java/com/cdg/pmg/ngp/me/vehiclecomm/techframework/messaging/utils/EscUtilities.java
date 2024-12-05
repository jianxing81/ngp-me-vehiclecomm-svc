package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EscUtilities {

  private static final Double EPSILON = 0.0000001;

  public static int toBoolOneOrZero(boolean s) {
    if (s) {
      return 1;
    } else {
      return 0;
    }
  }

  public static boolean doubleEquals(Double d1, Double d2) {
    return Math.abs(d1 - d2) < EPSILON;
  }
}
