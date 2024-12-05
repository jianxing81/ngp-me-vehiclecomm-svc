package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class CabchargePolicy implements Serializable {

  private Double amountCap;
  private List<PolicyDetails> policyDetails = new ArrayList<>();
}
