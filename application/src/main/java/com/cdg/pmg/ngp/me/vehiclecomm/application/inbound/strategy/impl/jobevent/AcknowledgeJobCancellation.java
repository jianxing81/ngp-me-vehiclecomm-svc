package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.jobevent;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RedundantMessageKeyData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RedundantMessageRequestHolder;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.AbstractJobEvent;
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
 * For VehicleComm requirement "4.2.1.15.4 Acknowledge Job Cancellation Event Use Case Design
 * Document"
 *
 * @see <a
 *     href="https://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1260717203/4.2.1.15.4+Acknowledge+Job+Cancellation+Event+Use+Case+Design+Document">4.2.1.15.4
 *     AAcknowledge Job Cancellation Event Use Case Design Document</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AcknowledgeJobCancellation extends AbstractJobEvent {

  private final EsbJobMapper esbJobMapper;
  private final VehicleCommDomainService vehicleCommDomainService;
  private final VehicleCommCacheService vehicleCommCacheService;

  @Override
  public void handleJobEvent(EsbJob esbJob, String ivdJobEventTopic) {

    // Convert byte data to a pojo
    var byteDataRepresentation =
        byteToBeanConverter.acknowledgeJobCancellationConverter(
            esbJob.getIvdMessageRequest().getMessage());

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

    // Call Job Dispatch service to process the event
    callJobDispatchSvc(esbJob, DriverAction.CANCEL_ACK);

    // Add entry to redundantMessage cache
    vehicleCommCacheService.isRedundantMessage(messageId, uniqueKey);
  }

  @Override
  public IvdMessageEnum jobEventType() {
    return IvdMessageEnum.ACKNOWLEDGE_JOB_CANCELLATION;
  }
}
