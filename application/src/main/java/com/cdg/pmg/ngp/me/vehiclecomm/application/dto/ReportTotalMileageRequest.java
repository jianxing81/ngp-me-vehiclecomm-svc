package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request field for calling the Vehicle service */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportTotalMileageRequest implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private Integer totalMileage;
}
