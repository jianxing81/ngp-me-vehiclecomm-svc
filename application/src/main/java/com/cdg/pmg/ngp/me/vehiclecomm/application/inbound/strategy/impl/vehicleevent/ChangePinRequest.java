package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.vehicleevent;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ChangePinResult;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.AbstractVehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.EsbVehicleMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbVehicle;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdMessageEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdVehicleEventEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.GenericEventCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.service.VehicleCommDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * For VehicleComm requirement "4.2.1.15.27 Change Pin Request Event Use Case Design Document"
 *
 * @see <a
 *     href="https://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1285751033/4.2.1.15.27+Change+Pin+Request+Event+Use+Case+Design+Document">4.2.1.15.27
 *     Change Pin Request Event Use Case Design Document</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChangePinRequest extends AbstractVehicleEvent {

  private final EsbVehicleMapper esbVehicleMapper;
  private final VehicleCommDomainService vehicleCommDomainService;
  private final VehicleCommCacheService vehicleCommCacheService;

  @Override
  public void handleVehicleEvent(EsbVehicle esbVehicle, String ivdVehicleEventTopic) {
    // Convert byte data to a pojo
    var byteDataRepresentation =
        byteToBeanConverter.changePinRequestConverter(
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

    // Call MDT service API to update the PIN.
    ChangePinResult changePinResult = callMdtServiceToChangePin(esbVehicle);

    // Convert pojo to byte data
    GenericEventCommand genericEventCommand =
        beanToByteConverter.convertToByteChangePin(
            changePinResult,
            Integer.parseInt(IvdMessageEnum.TO_IVD_CHANGEPASSWORD_CONFIRMATION.getId()),
            esbVehicle);

    // Map genericEventCommand to the aggregate root
    esbVehicleMapper.genericEventCommandToByteArrayData(esbVehicle, genericEventCommand);

    // Prepare message object and publish to kafka topic
    sendMsgToIvdResponse(esbVehicle);
  }

  private ChangePinResult callMdtServiceToChangePin(EsbVehicle esbVehicle) {
    com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ChangePinRequest changePinRequest =
        com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ChangePinRequest.builder()
            .mobileId(esbVehicle.getByteData().getIvdNo())
            .driverId(esbVehicle.getByteData().getDriverId())
            .oldPin(esbVehicle.getByteData().getOldPin())
            .newPin(esbVehicle.getByteData().getNewPin())
            .messageId(esbVehicle.getByteData().getMessageId())
            .build();
    return mdtAPIService.changePin(changePinRequest);
  }

  @Override
  public IvdVehicleEventEnum vehicleEventType() {
    return IvdVehicleEventEnum.CHANGE_PIN_REQUEST;
  }
}
