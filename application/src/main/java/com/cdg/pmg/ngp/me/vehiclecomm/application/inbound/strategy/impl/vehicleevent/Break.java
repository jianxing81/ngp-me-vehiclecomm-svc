package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.vehicleevent;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VehicleDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.AbstractVehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.EsbVehicleMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbVehicle;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DeviceType;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdVehicleEventEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.ByteDataRepresentation;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.service.VehicleCommDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * For VehicleComm requirement "4.2.1.15.62 Vehicle Comm Break event Use Case Design Document."
 *
 * @see <a
 *     href="https://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1352532788/4.2.1.15.62+Vehicle+Comm+Break+event+Use+Case+Design+Document">4.2.1.15.62
 *     Vehicle Comm Break event Use Case Design Document.</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Break extends AbstractVehicleEvent {
  private final EsbVehicleMapper esbVehicleMapper;
  private final VehicleCommDomainService vehicleCommDomainService;
  private final VehicleCommCacheService vehicleCommCacheService;

  @Override
  public void handleVehicleEvent(EsbVehicle esbVehicle, String ivdVehicleEventTopic) {
    // Convert byte data to a pojo
    ByteDataRepresentation byteDataRepresentation =
        byteToBeanConverter.breakConverter(
            esbVehicle.getIvdVehicleEventMessageRequest().getMessage());

    // Map the pojo to the aggregate root
    esbVehicleMapper.byteDataRepresentationToIvdInboundEvent(esbVehicle, byteDataRepresentation);

    // validate the ivd no
    vehicleCommDomainService.validateIvdNo(esbVehicle);

    // Check if acknowledgement required
    boolean ackRequired = vehicleCommDomainService.isAckRequired(esbVehicle);

    // Send acknowledgement back to MDT if ack required
    if (ackRequired) {
      sendAcknowledgementToMdt(esbVehicle);
    }

    // Check store and forward cache if message already processed
    boolean messageAlreadyProcessed =
        vehicleCommCacheService.isKeyPresentInStoreForwardCache(
            esbVehicle.getByteData().getMessageId(),
            esbVehicle.getByteData().getIvdNo(),
            esbVehicle.getByteData().getSerialNumber());

    // End the process if message is already processed
    if (processedOrRedundantMessage(esbVehicle, messageAlreadyProcessed)) return;

    // Perform logical conversions on  coordinate fields of the pojo
    vehicleCommDomainService.parseGeoLocations(esbVehicle);

    // Get vehicle ID by IVD number from MDT service
    VehicleDetailsResponse vehicleDetails =
        getVehicleDetailsByIvdNo(esbVehicle.getByteData().getIvdNo(), Boolean.FALSE);

    // Map MDT service response to the aggregate root
    esbVehicleMapper.vehicleDetailsResponseToVehicleInboundEvent(esbVehicle, vehicleDetails);

    // Validate the MDT service response
    vehicleCommDomainService.validateVehicleDetails(esbVehicle);

    // Set Driver action
    vehicleCommDomainService.setDeviceType(esbVehicle, DeviceType.MDT);

    // set vehicleEventType in aggregater root
    vehicleCommDomainService.setVehicleEventType(esbVehicle, VehicleEvent.BREAK);

    // call vehicle service to update vehicle state
    callVehicleSvcToUpdateVehicleState(esbVehicle);
  }

  @Override
  public IvdVehicleEventEnum vehicleEventType() {
    return IvdVehicleEventEnum.BREAK;
  }
}
