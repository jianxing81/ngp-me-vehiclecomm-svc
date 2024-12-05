package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.rcsaevent;

import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.AbstractRcsaEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.RcsaEventsMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.Rcsa;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DeviceType;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdMessageEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.RcsaEvents;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.service.VehicleCommDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * For VehicleComm requirement "4.2.1.15.35 Emergency Report Event Use Case Design Document"
 *
 * @see <a
 *     href="https://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1288799522/4.2.1.15.35+Emergency+Report+Event+Use+Case+Design+Document">4.2.1.15.35
 *     Emergency Report Event Use Case Design Document</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmergencyReport extends AbstractRcsaEvent {

  private final RcsaEventsMapper rcsaEventsMapper;
  private final VehicleCommDomainService vehicleCommDomainService;
  private final VehicleCommCacheService vehicleCommCacheService;

  @Override
  public void handleRcsaEvent(Rcsa rcsaEvent, String rcsaEventTopic) {
    // Convert byte data to a pojo
    var byteDataRepresentation =
        byteToBeanConverter.emergencyReportConverter(
            rcsaEvent.getRcsaMessageRequest().getMessage());

    // Map the pojo to the aggregate root
    rcsaEventsMapper.byteDataRepresentationToRcsaInboundEvent(rcsaEvent, byteDataRepresentation);

    // validate the ivd no
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

    // Perform logical conversions on  coordinate fields of the pojo
    vehicleCommDomainService.parseGeoLocations(rcsaEvent);

    // Get vehicle ID by IVD number from MDT service
    var vehicleDetails = getVehicleDetailsByIvdNo(rcsaEvent.getByteData().getIvdNo());

    // Map MDT service response to the aggregate root
    rcsaEventsMapper.vehicleDetailsResponseToRcsaInboundEvent(rcsaEvent, vehicleDetails);

    // Validate the MDT service response
    vehicleCommDomainService.validateVehicleDetails(rcsaEvent);

    // Set Driver action
    vehicleCommDomainService.setDeviceType(rcsaEvent, DeviceType.ANDROID);

    // set vehicleEventType in aggregater root
    vehicleCommDomainService.setVehicleEventType(rcsaEvent, VehicleEvent.EMERGENCY_REPORT);

    // call vehicle service to update vehicle state
    callVehicleSvcToUpdateVehicleState(rcsaEvent);

    // set rcsaEventType in aggregater root
    vehicleCommDomainService.setRcsaEventType(rcsaEvent, RcsaEvents.EMERGENCY_REPORT);

    // publish message to ngp.me.rcsa.event
    publishMessageToRcsaEvent(rcsaEvent);
  }

  @Override
  public IvdMessageEnum ivdMessageEnum() {
    return IvdMessageEnum.EMERGENCY_REPORT;
  }
}
