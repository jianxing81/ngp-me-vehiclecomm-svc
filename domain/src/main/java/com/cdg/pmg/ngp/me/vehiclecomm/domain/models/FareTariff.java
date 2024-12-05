package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FareTariff {
  private TariffData data;

  @Getter
  @Setter
  public static class TariffData {
    private Double bookingFee;
  }
}
