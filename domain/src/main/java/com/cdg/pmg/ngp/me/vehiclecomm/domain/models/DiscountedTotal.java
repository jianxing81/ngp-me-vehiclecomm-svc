package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import java.io.Serializable;
import lombok.Data;

@Data
public class DiscountedTotal implements Serializable {
  private Double fare;
  private String currency;
}
