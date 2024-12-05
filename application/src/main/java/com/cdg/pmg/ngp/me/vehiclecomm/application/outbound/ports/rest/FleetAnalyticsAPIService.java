package com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.DriverPerformanceHistoryResponse;
import java.util.List;

/* Internal class for wrapper API calls to FleetAnalytics Service*/
public interface FleetAnalyticsAPIService {
  /**
   * This method is used to call DriverPerformanceHistory
   *
   * @param driverId driverId
   * @param startDate startDate
   * @return DriverPerformanceHistoryResponse
   */
  List<DriverPerformanceHistoryResponse> getDriverPerformanceHistory(
      String driverId, String startDate);
}
