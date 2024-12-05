package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.vehicleevent;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VerifyOtpRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VerifyOtpResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.AbstractVehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.EsbVehicleMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbVehicle;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdMessageEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdVehicleEventEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.GenericEventCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdResponseData;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.service.VehicleCommDomainService;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * For VehicleComm requirement "4.2.1.15.41 Verify OTP Use Case Design"
 *
 * @see <a
 *     href="https://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1285031522/4.2.1.15.41+Verify+OTP+Use+Case+Design">4.2.1.15.41
 *     Verify OTP Use Case Design</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VerifyOtp extends AbstractVehicleEvent {

  private final EsbVehicleMapper esbVehicleMapper;
  private final VehicleCommDomainService vehicleCommDomainService;
  private final VehicleCommCacheService vehicleCommCacheService;

  @Override
  public void handleVehicleEvent(EsbVehicle esbVehicle, String ivdVehicleEventTopic) {
    // Convert byte data to a pojo
    var byteDataRepresentation =
        byteToBeanConverter.verifyOtpConverter(
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

    // Call MDT service, fetch approval from response
    callMdtServiceToVerifyOtp(esbVehicle)
        .ifPresent(
            verifyOtp -> {
              // Update the response message ID in aggregate root
              vehicleCommDomainService.setResponseMessageId(
                  esbVehicle,
                  Integer.parseInt(IvdMessageEnum.VERIFY_OTP_RESPONSE_MESSAGE_ID.getId()));

              GenericEventCommand genericEventCommand =
                  beanToByteConverter.convertToByteVerifyOtp(verifyOtp, esbVehicle);

              // Map genericEventCommand to the aggregate root
              esbVehicleMapper.genericEventCommandToByteArrayData(esbVehicle, genericEventCommand);

              // Prepare message object and publish to kafka topic
              sendResponseMsgToIvdResponse(esbVehicle);
            });
  }

  private Optional<VerifyOtpResponse> callMdtServiceToVerifyOtp(EsbVehicle esbVehicle) {
    VerifyOtpRequest verifyOtpRequest =
        VerifyOtpRequest.builder()
            .code(esbVehicle.getByteData().getCode())
            .mobileNo(esbVehicle.getByteData().getMobileNumber())
            .ivdNo(esbVehicle.getByteData().getIvdNo())
            .messageId(esbVehicle.getByteData().getMessageId())
            .build();
    return mdtAPIService.verifyOtpDetails(verifyOtpRequest);
  }

  protected void sendResponseMsgToIvdResponse(EsbVehicle esbVehicle) {
    IvdResponseData ivdResponse =
        IvdResponseData.builder()
            .eventIdentifier(esbVehicle.getResponseMessageId().toString())
            .message(esbVehicle.getByteArrayData().getByteArrayMessage())
            .ivdNo(esbVehicle.getByteData().getIvdNo())
            .eventDate(LocalDateTime.now())
            .build();
    kafkaProducer.sendToIVDResponseEvent(ivdResponse);
  }

  @Override
  public IvdVehicleEventEnum vehicleEventType() {
    return IvdVehicleEventEnum.VERIFY_OTP;
  }
}
