package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import java.io.Serializable;
import lombok.Data;

@Data
public class PolicyDetails implements Serializable {

  private Double policyPickupLat;
  private Double policyPickupLng;
  private Double policyDestLat;
  private Double policyDestLng;
  private Double policyRadius;
}
