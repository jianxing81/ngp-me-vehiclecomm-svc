package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalChargeItem implements Serializable {
  private Integer chargeId;
  private String chargeType;
  private Double chargeAmt;
  private Double chargeThreshold;
  private Double chargeUpperLimit;
  private Double chargeLowerLimit;
}
