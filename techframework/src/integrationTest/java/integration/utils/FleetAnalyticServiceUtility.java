package integration.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fleetAnalytic.client.models.DriverPerformanceHistoryData;
import java.math.BigDecimal;
import reactor.core.publisher.Flux;

public class FleetAnalyticServiceUtility {
  private FleetAnalyticServiceUtility() {}

  public static Flux<DriverPerformanceHistoryData> driverPerformanceHistoryAPIResponse() {
    DriverPerformanceHistoryData driverPerformanceHistoryData = new DriverPerformanceHistoryData();
    driverPerformanceHistoryData.setDriverId("1234567");
    driverPerformanceHistoryData.setAcceptanceRate(0.9);
    driverPerformanceHistoryData.setCancellationRate(0.1);
    driverPerformanceHistoryData.setConfirmedRate(0.9);
    driverPerformanceHistoryData.setAvgRating(1.0);
    driverPerformanceHistoryData.setTotalCompletedJobs(BigDecimal.valueOf(1));
    driverPerformanceHistoryData.setTotalOfferedJobs(BigDecimal.valueOf(10));
    driverPerformanceHistoryData.setTotalBidJobs(BigDecimal.valueOf(10));
    driverPerformanceHistoryData.setTotalConfirmedJobs(BigDecimal.valueOf(10));
    return Flux.just(driverPerformanceHistoryData);
  }
}
