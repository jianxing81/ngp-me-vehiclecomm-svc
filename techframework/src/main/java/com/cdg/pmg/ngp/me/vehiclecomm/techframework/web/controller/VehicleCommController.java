package com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.controller;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtResponseCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.port.VehicleCommApplicationService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.GenericByteToBean;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VehicleTrack;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.EmergencyClose;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdPingMessageRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.NotificationMessage;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleTrackCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.BytesUtil;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.IVDMessageType;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.mappers.SendNotificationRequestMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.mappers.SendPingMessageRequestMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.mappers.VehicleCommRestMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.apis.VehicleCommApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.ByteToBeanRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.EmergencyActionEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.EmergencyCloseRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.IvdPingMessage;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.MDTActionEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.MdtRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.MdtResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.NotificationRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.VehicleTrackEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.VehicleTrackRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.VoiceEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.VoiceStreamEnum;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class VehicleCommController implements VehicleCommApi {
  private final VehicleCommApplicationService vehicleCommApplicationService;
  private final VehicleCommRestMapper vehicleCommRestMapper;
  private final SendNotificationRequestMapper sendNotificationRequestMapper;
  private final SendPingMessageRequestMapper sendPingMessageRequestMapper;

  /**
   * Endpoint to send notification to driver app. Note that constraints (@Size, @NotNull etc) are
   * inherited from the overridden method.
   *
   * @param userId userId (required)
   * @param notificationRequest JSON object of vehicle comm (required)
   */
  @Override
  public ResponseEntity<Void> sendNotification(
      String userId, NotificationRequest notificationRequest) {
    // note that we do not have any try/catch blocks here since exceptions are handled by
    // a GlobalExceptionHandler
    NotificationMessage notificationMessage =
        sendNotificationRequestMapper.map(notificationRequest);
    vehicleCommApplicationService.sendNotificationToDriverApp(userId, notificationMessage);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * @param event (required)
   * @param vehicleTrackRequest JSON object of vehicle tracking (required)
   * @return vehicleTrackResponse
   */
  @Override
  public ResponseEntity<Void> sendVehicleTrackingByEvent(
      VehicleTrackEnum event, VehicleTrackRequest vehicleTrackRequest) {
    log.info("[sendVehicleTrackingByEvent] Request Event - {} : {}", event, vehicleTrackRequest);
    VehicleTrack vehicleTrackEvent = vehicleCommRestMapper.maptToVehicleTrack(event);
    VehicleTrackCommand vehicleTrackCommand =
        vehicleCommRestMapper.mapToVehicleTrackCommand(vehicleTrackRequest);
    vehicleCommApplicationService.sendVehicleTrackingByEvent(
        vehicleTrackEvent, vehicleTrackCommand);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Endpoint to receive emergency close request. The endpoint basically extracts the required info
   * from the request body and convert to byte array
   *
   * @param emergencyCloseRequest JSON object of a emergency close api (required)
   * @return String
   */
  @Override
  public ResponseEntity<Void> sendEmergencyClose(
      EmergencyActionEnum emergencyActionEnum, EmergencyCloseRequest emergencyCloseRequest) {
    EmergencyClose emergencyClose =
        vehicleCommRestMapper.mapEmergencyCloseRequest(emergencyCloseRequest);
    var emergencyAction = vehicleCommRestMapper.mapEmergencyAction(emergencyActionEnum);
    vehicleCommApplicationService.sendEmergencyCloseRequest(emergencyAction, emergencyClose);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * @param voiceEventRequest JSON object of Voice event request(required)
   * @param event path parameter(required)
   * @return ResponseEntity<Void> voiceStreamingResponse
   */
  @Override
  public ResponseEntity<Void> sendVoiceStreaming(
      VoiceStreamEnum event, VoiceEventRequest voiceEventRequest) {
    vehicleCommApplicationService.sendVoiceStreaming(
        vehicleCommRestMapper.mapToVoiceStreamEvent(event),
        vehicleCommRestMapper.mapVoiceEventRequestToCommand(voiceEventRequest));
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * Endpoint to send ping message. Note that constraints (@Size, @NotNull etc) are inherited from
   * the overridden method.
   *
   * @param ivdPingMessage JSON object of vehicle comm (required).
   * @return SendIvdPingMessageResponse
   */
  @Override
  public ResponseEntity<Void> sendPingMessage(IvdPingMessage ivdPingMessage) {
    /* note that we do not have any try/catch blocks here since exceptions are handled by
    a GlobalExceptionHandler */
    Integer messageId = IVDMessageType.IVD_PING.getId();
    IvdPingMessageRequest ivdPingMessageRequest =
        sendPingMessageRequestMapper.mapToIvdPingMessageRequest(ivdPingMessage);
    vehicleCommApplicationService.sendPingMessage(messageId, ivdPingMessageRequest);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  /**
   * This Method is to handle MDT action
   *
   * @param mdtAction Mdt action (required)
   * @param mdtRequest (required)
   * @return MdtResponse mdtResponse
   */
  @Override
  public ResponseEntity<MdtResponse> mdtAction(MDTActionEnum mdtAction, MdtRequest mdtRequest) {
    var mdtRequestCommand = vehicleCommRestMapper.mapToMDTRequestCommand(mdtRequest);
    byte[] requestBytes = BytesUtil.toBytes(mdtRequest.getByteString());
    byte[] ipAddArr =
        ArrayUtils.subarray(requestBytes, requestBytes.length - 15, requestBytes.length);
    MdtResponseCommand mdtResponseCommand =
        vehicleCommApplicationService.mdtAction(
            vehicleCommRestMapper.mapToMDTAction(mdtAction), mdtRequestCommand, ipAddArr);
    MdtResponse mdtResponse = vehicleCommRestMapper.mapToMDTResponse(mdtResponseCommand);
    return new ResponseEntity<>(mdtResponse, HttpStatus.OK);
  }

  /**
   * Internal API to convert byte to bean
   *
   * @param eventId Event ID (required)
   * @param byteToBeanRequest (required)
   * @return ResponseEntity<Map<String, Object>>
   */
  @Override
  public ResponseEntity<Map<String, Object>> byteToBeanConverter(
      String eventId, ByteToBeanRequest byteToBeanRequest) {
    var genericByteToBean =
        GenericByteToBean.builder()
            .eventId(eventId)
            .hexString(byteToBeanRequest.getByteString())
            .build();
    vehicleCommApplicationService.convertByteToBean(genericByteToBean);
    return ResponseEntity.ok(genericByteToBean.getResponseMap());
  }
}
