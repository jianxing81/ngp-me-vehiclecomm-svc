package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import java.io.Serializable;
import lombok.Data;

@Data
public class TariffInfo implements Serializable {
  private String tariffTypeCode;
  private Integer tariffUnit;
  private DiscountedTotal discountedTotal;
}
