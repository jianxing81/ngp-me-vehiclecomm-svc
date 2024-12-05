package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.rcsaevent;

import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.AbstractRcsaEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.RcsaEventsMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.Rcsa;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdMessageEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.RcsaEvents;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.service.VehicleCommDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * This class is used for handling RCSA Emergency initiate event Initiated
 *
 * @see <a
 *     href="https://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1287096329/4.2.1.15.36+Emergency+Initiated+Use+Case+Design+Document"</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmergencyInitiateMessage extends AbstractRcsaEvent {

  private final RcsaEventsMapper rcsaEventsMapper;
  private final VehicleCommDomainService vehicleCommDomainService;
  private final VehicleCommCacheService vehicleCommCacheService;

  /**
   * Method to process RCSA Emergency Initiate event
   *
   * @param rcsaEvent event details
   * @param rcsaEventTopic kafka topic
   */
  @Override
  public void handleRcsaEvent(Rcsa rcsaEvent, String rcsaEventTopic) {
    log.debug("handle Rcsa Event for emergency initiate ");
    // Convert byte data to a pojo
    var byteDataRepresentation =
        byteToBeanConverter.emergencyRequestConverter(
            rcsaEvent.getRcsaMessageRequest().getMessage());

    // Map the pojo to the aggregate root
    rcsaEventsMapper.byteDataRepresentationToRcsaInboundEvent(rcsaEvent, byteDataRepresentation);

    // Validate the pojo
    vehicleCommDomainService.validateIvdNo(rcsaEvent);

    vehicleCommDomainService.parseGeoLocations(rcsaEvent);

    // validate emergency id
    vehicleCommDomainService.validateEmergencyId(rcsaEvent);
    // Check if acknowledgement required
    var ackRequired = vehicleCommDomainService.isAckRequired(rcsaEvent);

    // Send acknowledgement back to MDT if ack required
    if (ackRequired) {
      sendAcknowledgementToMdt(rcsaEvent);
    }

    // Check store and forward cache if message already processed
    if (rcsaEvent.getByteData() != null) {
      var messageAlreadyProcessed =
          vehicleCommCacheService.isKeyPresentInStoreForwardCache(
              rcsaEvent.getByteData().getMessageId(),
              rcsaEvent.getByteData().getIvdNo(),
              rcsaEvent.getByteData().getSerialNumber());

      // End the process if message is already processed
      if (messageAlreadyProcessed) {
        log.info("Message ID {} is already processed", rcsaEvent.getByteData().getMessageId());
        return;
      }
    }
    // Get vehicle ID by IVD number from MDT service
    var vehicleDetails = getVehicleDetailsByIvdNo(rcsaEvent.getByteData().getIvdNo());

    // Map MDT service response to the aggregate root
    rcsaEventsMapper.vehicleDetailsResponseToRcsaInboundEvent(rcsaEvent, vehicleDetails);

    // Validate the MDT service response
    vehicleCommDomainService.validateVehicleDetails(rcsaEvent);

    // set rcsaEventType in aggregater root
    vehicleCommDomainService.setRcsaEventType(rcsaEvent, RcsaEvents.EMERGENCY_INITIATED);
    // publish message to ngp.me.rcsa.event
    produceMessageToRcsaEvent(rcsaEvent);
  }

  /**
   * Method to return ivd message enum value
   *
   * @return IvdMessageEnum
   */
  @Override
  public IvdMessageEnum ivdMessageEnum() {
    return IvdMessageEnum.EMERGENCY_INITIATED;
  }
}
