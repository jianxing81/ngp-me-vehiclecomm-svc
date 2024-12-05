package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils;

import java.math.BigDecimal;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DataConversion {

  private static final int COORD_MULTIPLIER = 10000000;

  public static Integer toCoordData(double value) {
    double coord = value * COORD_MULTIPLIER;
    return (int) coord;
  }

  public static int toIVDMoneyType(double amount) {
    if (amount > Integer.MAX_VALUE) return Integer.MAX_VALUE;

    return BigDecimal.valueOf(amount).multiply(new BigDecimal(100)).intValue();
  }

  /**
   * @Description converting int amount to double amount
   *
   * @param amount amount
   * @return double
   */
  public static double toBackendMoneyType(int amount) {
    return ((double) amount) / 100;
  }

  public static long toMeter(long radius) {
    return radius * 1000;
  }
}
