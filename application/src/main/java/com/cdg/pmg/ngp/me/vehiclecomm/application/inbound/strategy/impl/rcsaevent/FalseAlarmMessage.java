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
 * Class to handle Rcsa false Alarm Event
 *
 * @see <a
 *     href="https://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1288897316/4.2.1.15.37+False+Alarm+Event+Use+Case+Design+Document"</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FalseAlarmMessage extends AbstractRcsaEvent {

  private final RcsaEventsMapper rcsaEventsMapper;
  private final VehicleCommDomainService vehicleCommDomainService;
  private final VehicleCommCacheService vehicleCommCacheService;

  /**
   * Method to process rcsa false alarm event
   *
   * @param rcsaEvent event deatils
   * @param rcsaEventTopic topic name
   */
  @Override
  public void handleRcsaEvent(Rcsa rcsaEvent, String rcsaEventTopic) {
    log.debug("handle Rcsa False Alarm Event");
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
    if (rcsaEvent.getByteData() != null) {
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
    }

    // Get vehicle ID by IVD number from MDT service
    var vehicleDetails = getVehicleDetailsByIvdNo(rcsaEvent.getByteData().getIvdNo());

    // Map MDT service response to the aggregate root
    rcsaEventsMapper.vehicleDetailsResponseToRcsaInboundEvent(rcsaEvent, vehicleDetails);

    // Validate the MDT service response
    vehicleCommDomainService.validateVehicleDetails(rcsaEvent);

    // Set Driver action
    vehicleCommDomainService.setDeviceType(rcsaEvent, DeviceType.MDT);

    // set vehicleEventType in aggregater root
    vehicleCommDomainService.setVehicleEventType(rcsaEvent, VehicleEvent.FALSE_ALARM);

    callVehicleSvcToUpdateVehicleState(rcsaEvent);

    // set rcsaEventType in aggregater root
    vehicleCommDomainService.setRcsaEventType(rcsaEvent, RcsaEvents.FALSE_ALARM);
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
    return IvdMessageEnum.FALSE_ALARM;
  }
}
