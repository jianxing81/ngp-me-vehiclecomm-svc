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
 * For VehicleComm requirement "4.2.1.15.29 Respond to Structure Message Event Use Case Design
 * Document"
 *
 * @see <a
 *     href="https://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1288798899/4.2.1.15.43+Respond+to+Simple+Message+Use+Case+Design+Documenthttps://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1288798899/4.2.1.15.43+Respond+to+Simple+Message+Use+Case+Design+Documenthttps://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1288798899/4.2.1.15.43+Respond+to+Simple+Message+Use+Case+Design+Documenthttps://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1288798899/4.2.1.15.43+Respond+to+Simple+Message+Use+Case+Design+Documenthttps://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1288798899/4.2.1.15.43+Respond+to+Simple+Message+Use+Case+Design+Documenthttps://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1288798899/4.2.1.15.43+Respond+to+Simple+Message+Use+Case+Design+Documenthttps://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1288798899/4.2.1.15.43+Respond+to+Simple+Message+Use+Case+Design+Documenthttps://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1288798899/4.2.1.15.43+Respond+to+Simple+Message+Use+Case+Design+Document
 *     Respond to Simple Message Event Use Case Design Document</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RespondToSimpleMessage extends AbstractRcsaEvent {

  private final RcsaEventsMapper rcsaEventsMapper;
  private final VehicleCommDomainService vehicleCommDomainService;
  private final VehicleCommCacheService vehicleCommCacheService;

  @Override
  public void handleRcsaEvent(Rcsa rcsaEvent, String rcsaEventTopic) {
    log.debug("handle Rcsa Event for Respond To Simple Message: ");
    // Convert byte data to a pojo
    var byteDataRepresentation =
        byteToBeanConverter.responseToSimpleMessageConverter(
            rcsaEvent.getRcsaMessageRequest().getMessage());

    // Map the pojo to the aggregate root
    rcsaEventsMapper.byteDataRepresentationToRcsaInboundEvent(rcsaEvent, byteDataRepresentation);

    vehicleCommDomainService.validateIvdNo(rcsaEvent);

    // Check if acknowledgement required
    var ackRequired = vehicleCommDomainService.isAckRequired(rcsaEvent);

    // Send acknowledgement back to MDT if ack required
    if (ackRequired) {
      sendAcknowledgementToMdt(rcsaEvent);
    }

    // Check store and forward cache if message already processed
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

    // Get vehicle ID by IVD number from MDT service
    var vehicleDetails = getVehicleDetailsByIvdNo(rcsaEvent.getByteData().getIvdNo());

    // Map MDT service response to the aggregate root
    rcsaEventsMapper.vehicleDetailsResponseToRcsaInboundEvent(rcsaEvent, vehicleDetails);

    // Validate the MDT service response
    vehicleCommDomainService.validateVehicleDetails(rcsaEvent);

    // set rcsaEventType in aggregater root
    vehicleCommDomainService.setRcsaEventType(rcsaEvent, RcsaEvents.RESPOND_TO_SIMPLE_MESSAGE);
    // publish message to ngp.me.rcsa.event
    produceMessageToResponseToSimpleMessageRcsaEvent(rcsaEvent);
  }

  @Override
  public IvdMessageEnum ivdMessageEnum() {
    return IvdMessageEnum.RESPOND_TO_SIMPLE_MESSAGE;
  }
}
