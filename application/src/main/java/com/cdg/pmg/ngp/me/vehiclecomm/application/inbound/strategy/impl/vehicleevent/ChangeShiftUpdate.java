package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.vehicleevent;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ZoneInfoDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.AbstractVehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.EsbVehicleMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbVehicle;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdVehicleEventEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VehicleMdtEventType;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.service.VehicleCommDomainService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * For VehicleComm requirement "4.2.1.15.14 Change Shift Update Event Use Case Design Document"
 *
 * @see <a
 *     href="https://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1261929179/4.2.1.15.14+Change+Shift+Update+Event+Use+Case+Design+Document">4.2.1.15.14
 *     Change Shift Update Event Use Case Design Document</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChangeShiftUpdate extends AbstractVehicleEvent {

  private final EsbVehicleMapper esbVehicleMapper;
  private final VehicleCommDomainService vehicleCommDomainService;
  private final VehicleCommCacheService vehicleCommCacheService;

  @Override
  public void handleVehicleEvent(EsbVehicle esbVehicle, String ivdVehicleEventTopic) {
    // Convert byte data to a pojo
    var byteDataRepresentation =
        byteToBeanConverter.changeShiftUpdateConverter(
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

    // Get vehicle ID by IVD number from MDT service
    var vehicleDetails =
        getVehicleDetailsByIvdNo(esbVehicle.getByteData().getIvdNo(), Boolean.FALSE);

    // Map MDT service response to the aggregate root
    esbVehicleMapper.vehicleDetailsResponseToVehicleInboundEvent(esbVehicle, vehicleDetails);

    // Validate the MDT service response
    vehicleCommDomainService.validateVehicleDetails(esbVehicle);

    // Get zoneInfoDetails by zoneId from MDT service
    var zoneInfoDetails =
        getZoneInfoDetailsByZoneId(Integer.parseInt(esbVehicle.getByteData().getZoneId()));

    // Map MDT service response to the aggregate root
    esbVehicleMapper.zoneInfoDetailsResponseToVehicleInboundEvent(esbVehicle, zoneInfoDetails);

    // Validate the MDT service response
    vehicleCommDomainService.validateZoneInfoDetails(esbVehicle);

    // set vehicleMdtEventType in aggregate root
    vehicleCommDomainService.setVehicleMdtEventType(
        esbVehicle, VehicleMdtEventType.CHANGE_SHIFT_UPDATE);

    // Call Vehicle service to update vehicle mdt
    callVehicleSvcToUpdateVehicleMdt(
        esbVehicle, null, esbVehicle.getZoneInfoDetails().getId(), null);
  }

  /**
   * Retrieves zone info details by ZoneId from MDT service.
   *
   * @param zoneId zoneId from ESB request Byte array
   * @return The {@link ZoneInfoDetailsResponse} if available, else {@code null}.
   */
  protected ZoneInfoDetailsResponse getZoneInfoDetailsByZoneId(Integer zoneId) {
    Optional<ZoneInfoDetailsResponse> zoneInfoDetailsResponse =
        mdtAPIService.zoneInfoDetails(zoneId);
    return zoneInfoDetailsResponse.orElse(null);
  }

  @Override
  public IvdVehicleEventEnum vehicleEventType() {
    return IvdVehicleEventEnum.CHANGE_SHIFT_UPDATE;
  }
}
