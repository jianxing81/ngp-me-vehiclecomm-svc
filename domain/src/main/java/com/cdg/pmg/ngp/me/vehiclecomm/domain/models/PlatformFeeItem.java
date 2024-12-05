package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import java.io.Serializable;
import lombok.Data;

@Data
public class PlatformFeeItem implements Serializable {
  private String platformFeeApplicability;
  private Double thresholdLimit;
  private Double feeBelowThresholdLimit;
  private Double feeAboveThresholdLimit;
}
