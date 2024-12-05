package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.*;

/** Response fields for calling the fleetAnalytic service */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverPerformanceHistoryResponse implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String driverId;
  private Double acceptanceRate;
  private Double cancellationRate;
  private Double confirmedRate;
  private Double avgRating;
  private Integer totalCompletedJobs;
  private Integer totalOfferedJobs;
  private Integer totalBidJobs;
  private Integer totalConfirmedJobs;
  private LocalDate capturedDate;
}
