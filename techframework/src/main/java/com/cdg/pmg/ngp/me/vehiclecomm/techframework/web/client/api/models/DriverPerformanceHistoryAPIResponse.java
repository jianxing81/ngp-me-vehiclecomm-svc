package com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.client.api.models;

import java.io.Serial;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverPerformanceHistoryAPIResponse implements Serializable {

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
  private String capturedDate;
}
