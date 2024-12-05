package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.vehicleevent;

import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.AbstractVehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.EsbVehicleMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbVehicle;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdVehicleEventEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.PingMessageEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdPingMessageData;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdPingUpdateRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.service.VehicleCommDomainService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * For VehicleComm requirement "4.2.1.15.28 IVD Ping Response Event Use Case Design Document"
 *
 * @see <a
 *     href="https://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1285327702/4.2.1.15.28+IVD+Ping+Response+Event+Use+Case+Design+Document">4.2.1.15.28
 *     IVD Ping Response Event Use Case Design Document</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IvdPingResponse extends AbstractVehicleEvent {

  private final EsbVehicleMapper esbVehicleMapper;
  private final VehicleCommDomainService vehicleCommDomainService;
  private final VehicleCommCacheService vehicleCommCacheService;

  @Override
  public void handleVehicleEvent(EsbVehicle esbVehicle, String ivdVehicleEventTopic) {
    // Convert byte data to a pojo
    var byteDataRepresentation =
        byteToBeanConverter.ivdPingResponseConverter(
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

    // Call MDT service to update ping event
    updateIvdPingEventToMdt(esbVehicle);
  }

  /**
   * This method is to call the api end point ivd-ping-response for updating ivd ping
   *
   * @param esbVehicle esbVehicle
   */
  private void updateIvdPingEventToMdt(EsbVehicle esbVehicle) {
    IvdPingMessageData appResvEvent =
        IvdPingMessageData.builder()
            .pingMessageEvent(PingMessageEvent.APP_RESV)
            .timestamp(LocalDateTime.now())
            .build();
    IvdPingMessageData esbIvdResvEvent =
        IvdPingMessageData.builder()
            .pingMessageEvent(PingMessageEvent.ESB_IVD_RESV)
            .timestamp(null)
            .build();
    IvdPingMessageData esbAppSendEvent =
        IvdPingMessageData.builder()
            .pingMessageEvent(PingMessageEvent.ESB_APP_SEND)
            .timestamp(null)
            .build();
    List<IvdPingMessageData> pingMessageList = new ArrayList<>();
    pingMessageList.add(appResvEvent);
    pingMessageList.add(esbIvdResvEvent);
    pingMessageList.add(esbAppSendEvent);
    IvdPingUpdateRequest ivdPingUpdateRequest =
        IvdPingUpdateRequest.builder()
            .requestNumber(String.valueOf(esbVehicle.getByteData().getRefNo()))
            .sequenceNumber(String.valueOf(esbVehicle.getByteData().getSeqNo()))
            .messageId(String.valueOf(esbVehicle.getByteData().getMessageId()))
            .ivdNo(esbVehicle.getByteData().getIvdNo())
            .ivdPingData(pingMessageList)
            .build();
    mdtAPIService.updateIvdPing(ivdPingUpdateRequest);
  }

  @Override
  public IvdVehicleEventEnum vehicleEventType() {
    return IvdVehicleEventEnum.IVD_PING_RESPONSE;
  }
}
