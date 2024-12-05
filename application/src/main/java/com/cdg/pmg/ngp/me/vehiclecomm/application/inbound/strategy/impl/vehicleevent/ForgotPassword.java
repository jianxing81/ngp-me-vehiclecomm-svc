package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.vehicleevent;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ForgotPasswordRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.ForgotPasswordResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.AbstractVehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.EsbVehicleMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbVehicle;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdVehicleEventEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.ByteDataRepresentation;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.GenericEventCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.service.VehicleCommDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * For VehicleComm requirement "4.2.1.15.40 Forgot Password request Event Use Case Design Document"
 *
 * @see <a
 *     href="https://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1285260526/4.2.1.15.40+Forgot+Password+request+Event+Use+Case+Design+Document">4.2.1.15.40
 *     Forgot Password request Event Use Case Design Document</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ForgotPassword extends AbstractVehicleEvent {

  private final VehicleCommCacheService vehicleCommCacheService;
  private final EsbVehicleMapper esbVehicleMapper;
  private final VehicleCommDomainService vehicleCommDomainService;

  @Override
  public void handleVehicleEvent(EsbVehicle esbVehicle, String ivdVehicleEventTopic) {

    // Convert byte data to a pojo
    ByteDataRepresentation byteDataRepresentation =
        byteToBeanConverter.forgotPasswordRequestConverter(
            esbVehicle.getIvdVehicleEventMessageRequest().getMessage());

    // Map the pojo to the aggregate root
    esbVehicleMapper.byteDataRepresentationToIvdInboundEvent(esbVehicle, byteDataRepresentation);

    // validate the ivd no
    vehicleCommDomainService.validateIvdNo(esbVehicle);

    // Validate the Mobile Number
    vehicleCommDomainService.validateMobileNumber(esbVehicle);

    // Check if acknowledgement required
    boolean isAckRequired = vehicleCommDomainService.isAckRequired(esbVehicle);

    // Send acknowledgement back to MDT if ack required
    if (isAckRequired) {
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

    // Call the forgotten password interface of the MDT service and obtain the response result
    ForgotPasswordResponse forgotPasswordResponse = callMdtServiceToForgotPassword(esbVehicle);

    // converting to bean to byte
    GenericEventCommand genericEventCommand =
        beanToByteConverter.convertToByteForgotPassword(
            forgotPasswordResponse, esbVehicle.getByteData().getIvdNo());

    // Map genericEventCommand to the aggregate root
    esbVehicleMapper.genericEventCommandToByteArrayData(esbVehicle, genericEventCommand);

    // Prepare message object and publish to kafka topic
    sendMsgToIvdResponse(esbVehicle);
  }

  /**
   * Call the forgotten password interface of the MDT service and obtain the response result
   *
   * @param esbVehicle esbVehicle
   * @return MDT service API response
   */
  private ForgotPasswordResponse callMdtServiceToForgotPassword(EsbVehicle esbVehicle) {
    ForgotPasswordRequest forgotPasswordRequest =
        ForgotPasswordRequest.builder()
            .mobileNo(esbVehicle.getByteData().getMobileNumber())
            .ivdNo(esbVehicle.getByteData().getIvdNo())
            .vehiclePlateNum(esbVehicle.getByteData().getVehiclePlateNumber())
            .messageId(esbVehicle.getByteData().getMessageId().toString())
            .build();
    return mdtAPIService.forgotPassword(forgotPasswordRequest);
  }

  @Override
  public IvdVehicleEventEnum vehicleEventType() {
    return IvdVehicleEventEnum.FORGOT_PASSWORD_REQUEST;
  }
}
