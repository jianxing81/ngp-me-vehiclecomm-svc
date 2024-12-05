package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.vehicleevent;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.DriverPerformanceHistoryResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VehicleDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.AbstractVehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.EsbVehicleMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.FleetAnalyticsAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbVehicle;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdMessageEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdVehicleEventEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.GenericEventCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdResponseData;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.service.VehicleCommDomainService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * For VehicleComm requirement "4.2.1.15.26 Driver Performance Request Event Use Case Design
 * Document"
 *
 * @see <a
 *     href="https://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1284998699/4.2.1.15.26+Driver+Performance+Request+Event+Use+Case+Design+Document">4.2.1.15.26
 *     Driver Performance Request Event Use Case Design Document</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DriverPerformanceRequest extends AbstractVehicleEvent {

  private final EsbVehicleMapper esbVehicleMapper;
  private final VehicleCommDomainService vehicleCommDomainService;
  private final FleetAnalyticsAPIService fleetAnalyticsAPIService;

  @Override
  public void handleVehicleEvent(EsbVehicle esbVehicle, String ivdVehicleEventTopic) {
    // Convert byte data to a pojo
    var byteDataRepresentation =
        byteToBeanConverter.driverPerformanceRequestConverter(
            esbVehicle.getIvdVehicleEventMessageRequest().getMessage());

    // Map the pojo to the aggregate root
    esbVehicleMapper.byteDataRepresentationToIvdInboundEvent(esbVehicle, byteDataRepresentation);

    // Get IP Address by IVD number from MDT service
    VehicleDetailsResponse vehicleDetailsResponse =
        getVehicleDetailsByIvdNo(esbVehicle.getByteData().getIvdNo(), Boolean.FALSE);

    // Map MDT service response to the aggregate root
    esbVehicleMapper.vehicleDetailsResponseToVehicleInboundEvent(
        esbVehicle, vehicleDetailsResponse);

    // Validate the MDT service response
    vehicleCommDomainService.validateVehicleDetails(esbVehicle);

    // One day before Yesterday's date
    LocalDate startDate = LocalDate.now().minusDays(2);

    // Call Fleet Analytics Service to calculate driver performance
    List<DriverPerformanceHistoryResponse> performanceHistory =
        fleetAnalyticsAPIService.getDriverPerformanceHistory(
            esbVehicle.getByteData().getDriverId(), String.valueOf(startDate));

    String driverPerformance = null;
    if (!CollectionUtils.isEmpty(performanceHistory)) {
      // get latest data
      Collections.sort(
          performanceHistory, (o1, o2) -> o2.getCapturedDate().compareTo(o1.getCapturedDate()));
      DriverPerformanceHistoryResponse response = performanceHistory.get(0);
      driverPerformance = getDriverPerformance(response);
    } else {
      log.info(
          "No Performance data found for this driver {}", esbVehicle.getByteData().getDriverId());
    }

    // Map response message id to the aggregate root
    vehicleCommDomainService.setResponseMessageId(
        esbVehicle,
        Integer.parseInt(IvdMessageEnum.DRIVER_PERFORMANCE_RESPONSE_MESSAGE_ID.getId()));

    // Convert pojo to byte data
    GenericEventCommand genericEventCommand =
        beanToByteConverter.convertToByteDriverPerformance(driverPerformance, esbVehicle);

    // Map genericEventCommand to the aggregate root
    esbVehicleMapper.genericEventCommandToByteArrayData(esbVehicle, genericEventCommand);

    // Prepare message object and publish to kafka topic
    sendMessageToIvdResponse(esbVehicle);
  }

  /**
   * driverPerformance For byte conversion and StringBuilder
   *
   * <p>capturedDate as Date & Time totalOfferedJobs as Jobs Offered totalBidJobs and acceptanceRate
   * as Jobs Bid totalConfirmedJobs and confirmedRate as Jobs Confirmed
   *
   * <p>Date & Time:dd/mm/yyyy 00:00 - 23:59 Jobs Offered:xxxx Jobs Bid:xxxx (xx%) Jobs
   * Confirmed:xxx (xx%) Jobs Completed:xxx (xx%)
   *
   * @param response response
   * @return String
   */
  private String getDriverPerformance(DriverPerformanceHistoryResponse response) {
    String jobDt = String.valueOf(response.getCapturedDate());
    int jobOfferedCount = response.getTotalOfferedJobs();
    int jobBidCount = response.getTotalBidJobs();
    Double acceptanceRate = response.getAcceptanceRate();
    Integer totalConfirmedJobs = response.getTotalConfirmedJobs();
    Double confirmedRate = response.getConfirmedRate();
    StringBuilder dailyPerformanceBldr = new StringBuilder();
    dailyPerformanceBldr
        .append("Date & Time:")
        .append(jobDt)
        .append(" 00:00 - 23:59")
        .append(VehicleCommAppConstant.NEW_LINE);
    dailyPerformanceBldr
        .append("Jobs Offered:")
        .append(String.valueOf(jobOfferedCount))
        .append(VehicleCommAppConstant.NEW_LINE);
    dailyPerformanceBldr
        .append("Jobs Bid:")
        .append(String.valueOf(jobBidCount))
        .append(" (")
        .append(String.valueOf(acceptanceRate))
        .append("%)")
        .append(VehicleCommAppConstant.NEW_LINE);
    dailyPerformanceBldr
        .append("Jobs Confirmed:")
        .append(String.valueOf(totalConfirmedJobs))
        .append(" (")
        .append(String.valueOf(confirmedRate))
        .append("%)")
        .append(VehicleCommAppConstant.NEW_LINE);
    return dailyPerformanceBldr.toString();
  }

  protected void sendMessageToIvdResponse(EsbVehicle esbVehicle) {
    IvdResponseData ivdResponse =
        IvdResponseData.builder()
            .eventIdentifier(esbVehicle.getResponseMessageId().toString())
            .message(esbVehicle.getByteArrayData().getByteArrayMessage())
            .ivdNo(esbVehicle.getByteData().getIvdNo())
            .eventDate(LocalDateTime.now())
            .build();
    kafkaProducer.sendToIVDResponseEvent(ivdResponse);
  }

  @Override
  public IvdVehicleEventEnum vehicleEventType() {
    return IvdVehicleEventEnum.DRIVER_PERFORMANCE_REQUEST;
  }
}
