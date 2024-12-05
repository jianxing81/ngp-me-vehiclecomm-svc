package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.jobevent;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RedundantMessageKeyData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RedundantMessageRequestHolder;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.AbstractJobEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.JobEventStrategy;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.EsbJobMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbJob;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DriverAction;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdMessageEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.service.VehicleCommDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * JobReject is responsible for handling job rejection events. It implements the {@link
 * JobEventStrategy} interface. Jira ME-1062
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobReject extends AbstractJobEvent {

  private final EsbJobMapper esbJobMapper;
  private final VehicleCommDomainService vehicleCommDomainService;
  private final VehicleCommCacheService vehicleCommCacheService;

  /**
   * Handles the job rejection event.
   *
   * @param esbJob The {@link EsbJob} representing the job event.
   * @param ivdJobEventTopic The kafka topic.
   */
  @Override
  public void handleJobEvent(EsbJob esbJob, String ivdJobEventTopic) {
    // Convert byte data to a pojo
    var byteDataRepresentation =
        byteToBeanConverter.jobRejectConverter(esbJob.getIvdMessageRequest().getMessage());

    // Map the pojo to the aggregate root
    esbJobMapper.byteDataRepresentationToIvdInboundEvent(esbJob, byteDataRepresentation);

    // Validate the pojo
    vehicleCommDomainService.validateByteData(esbJob);

    Integer messageId = esbJob.getByteData().getMessageId();
    String uniqueKey =
        esbJob.getByteData().getJobNo()
            + VehicleCommAppConstant.DASH
            + esbJob.getByteData().getIvdNo();

    // Add cacheKey details to access cache to discard duplicate message
    RedundantMessageKeyData msgHolder =
        new RedundantMessageKeyData(
            esbJob.getIvdMessageRequest().getEventId(), messageId, uniqueKey);

    RedundantMessageRequestHolder.set(msgHolder);

    // Perform logical conversions on the fields of the pojo
    vehicleCommDomainService.parseGeoLocations(esbJob);
    vehicleCommDomainService.parseMoneyValues(esbJob);

    // Get vehicle ID by IVD number from MDT service
    var vehicleDetails = getVehicleDetailsByIvdNo(esbJob.getByteData().getIvdNo());

    // Map MDT service response to the aggregate root
    esbJobMapper.vehicleDetailsResponseToIvdInboundEvent(esbJob, vehicleDetails);

    // Validate the MDT service response
    vehicleCommDomainService.validateVehicleDetails(esbJob);

    // Get Driver Action based on reasonCode
    DriverAction driverAction =
        vehicleCommDomainService.findDriverActionForJobReject(
            esbJob, esbJob.getByteData().getReasonCode());

    // Set Driver action
    vehicleCommDomainService.setDriverAction(esbJob, driverAction);

    // Call Job Dispatch service to process the event
    callJobDispatchSvc(esbJob, esbJob.getDriverAction());

    // Add entry to redundantMessage cache
    vehicleCommCacheService.isRedundantMessage(messageId, uniqueKey);
  }

  /**
   * Returns the type of job event handled by this class, which is {@link
   * IvdMessageEnum#JOB_REJECT}. This will help to initialize the map of handlers for Strategy
   *
   * @return The type of job event.
   */
  @Override
  public IvdMessageEnum jobEventType() {
    return IvdMessageEnum.JOB_REJECT;
  }
}
