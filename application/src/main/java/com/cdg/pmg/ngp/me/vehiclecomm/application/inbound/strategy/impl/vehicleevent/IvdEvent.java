package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.vehicleevent;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.AbstractVehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.EsbVehicleMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbVehicle;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DeviceType;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdVehicleEventEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.BadRequestException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.service.VehicleCommDomainService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * For VehicleComm requirement "4.2.1.15.38 IVD Event Use Case Design Document"
 *
 * @see <a
 *     href="https://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1272218730/4.2.1.15.38+IVD+Event+Use+Case+Design+Document">4.2.1.15.38
 *     IVD Event Use Case Design Document</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IvdEvent extends AbstractVehicleEvent {

  private final EsbVehicleMapper esbVehicleMapper;
  private final VehicleCommDomainService vehicleCommDomainService;
  private final VehicleCommCacheService vehicleCommCacheService;

  @Override
  public void handleVehicleEvent(EsbVehicle esbVehicle, String ivdVehicleEventTopic) {
    // Convert byte data to a pojo
    var byteDataRepresentation =
        byteToBeanConverter.ivdEventConverter(
            esbVehicle.getIvdVehicleEventMessageRequest().getMessage());

    // Map the pojo to the aggregate root
    esbVehicleMapper.byteDataRepresentationToIvdInboundEvent(esbVehicle, byteDataRepresentation);

    // validate the ivd no
    vehicleCommDomainService.validateIvdNo(esbVehicle);

    // Check if acknowledgement required
    var ackRequired = vehicleCommDomainService.isAckRequired(esbVehicle);

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
    var vehicleDetails =
        getVehicleDetailsByIvdNo(esbVehicle.getByteData().getIvdNo(), Boolean.FALSE);

    // Map MDT service response to the aggregate root
    esbVehicleMapper.vehicleDetailsResponseToVehicleInboundEvent(esbVehicle, vehicleDetails);

    // Validate the MDT service response
    vehicleCommDomainService.validateVehicleDetails(esbVehicle);

    // Set Driver action
    vehicleCommDomainService.setDeviceType(esbVehicle, DeviceType.MDT);

    // Check if the event id is 2001,only if 2001 proceed
    if (esbVehicle.getByteData().getEventId() != null
        && Objects.equals(
            esbVehicle.getByteData().getEventId(), VehicleCommAppConstant.AUTOBID_EVENT_ID)) {
      // set vehicleEventType in aggregater root
      vehicleCommDomainService.setVehicleEventType(
          esbVehicle, getEventType(esbVehicle.getByteData().getEventContent()));

      // call vehicle service to update vehicle state
      callVehicleSvcToUpdateVehicleState(esbVehicle);
    }
  }

  private VehicleEvent getEventType(String eventContent) {
    log.debug("getEventType {}", eventContent);
    return switch (eventContent) {
      case "0" -> VehicleEvent.DISABLE_AUTO_BID;
      case "1" -> VehicleEvent.ENABLE_AUTO_BID;
      case "2" -> VehicleEvent.ENABLE_AUTO_ACCEPT;
      default -> throw new BadRequestException(ErrorCode.INVALID_VEHICLE_EVENT.getCode());
    };
  }

  @Override
  public IvdVehicleEventEnum vehicleEventType() {
    return IvdVehicleEventEnum.IVD_EVENT;
  }
}
