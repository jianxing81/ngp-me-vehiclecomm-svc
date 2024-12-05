package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.jobevent;

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
 * For VehicleComm requirement "4.2.1.15.17 Acknowledge Convert Street Hail Event Use Case Design
 * Document"
 *
 * @see <a
 *     href="https://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1261568545/4.2.1.15.17+Acknowledge+Convert+Street+Hail+Event+Use+Case+Design+Document">4.2.1.15.17
 *     Acknowledge Convert Street Hail Event Use Case Design Document</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AcknowledgeConvertStreethail extends AbstractJobEvent {

  private final EsbJobMapper esbJobMapper;
  private final VehicleCommDomainService vehicleCommDomainService;
  private final VehicleCommCacheService vehicleCommCacheService;

  @Override
  public void handleJobEvent(EsbJob esbJob, String ivdJobEventTopic) {

    // Convert byte data to a pojo
    var byteDataRepresentation =
        byteToBeanConverter.acknowledgeConvertStreetHailEvent(
            esbJob.getIvdMessageRequest().getMessage());

    // Map the pojo to the aggregate root
    esbJobMapper.byteDataRepresentationToIvdInboundEvent(esbJob, byteDataRepresentation);

    // Validate the pojo
    vehicleCommDomainService.validateByteData(esbJob);

    // Check if acknowledgement required
    var ackRequired = vehicleCommDomainService.isAckRequired(esbJob);

    // Send acknowledgement back to MDT if ack required
    if (ackRequired) {
      sendAcknowledgementToMdt(esbJob);
    }

    Integer messageId = esbJob.getByteData().getMessageId();
    String uniqueKey = esbJob.getByteData().getJobNo();

    // Add cacheKey details to access cache to discard duplicate message
    RedundantMessageKeyData msgHolder =
        new RedundantMessageKeyData(
            esbJob.getIvdMessageRequest().getEventId(), messageId, uniqueKey);

    RedundantMessageRequestHolder.set(msgHolder);

    // Check store and forward cache if message already processed
    var messageAlreadyProcessed =
        vehicleCommCacheService.isKeyPresentInStoreForwardCache(
            esbJob.getByteData().getMessageId(),
            esbJob.getByteData().getIvdNo(),
            esbJob.getByteData().getSerialNumber());

    // End the process if message is already processed
    if (processedOrRedundantMessage(esbJob, messageAlreadyProcessed)) return;

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
    callJobDispatchSvc(esbJob, DriverAction.STREET_CONVERT_ACK);

    // Add entry to redundantMessage cache
    vehicleCommCacheService.isRedundantMessage(messageId, uniqueKey);
  }

  @Override
  public IvdMessageEnum jobEventType() {
    return IvdMessageEnum.ACKNOWLEDGE_CONVERT_STREETHAIL;
  }
}
