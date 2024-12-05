package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.port;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtRequestCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtResponseCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.Driver;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbJob;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbVehicle;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.GenericByteToBean;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.Rcsa;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.RcsaMessage;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.EmergencyAction;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.MdtAction;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VehicleTrack;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VoiceStream;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.EmergencyClose;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdPingMessageRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.JobDispatchEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.NotificationMessage;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleTrackCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VoiceEventRequest;

/** The interface Common service. */
public interface VehicleCommApplicationService {

  /**
   * This method will process the job events coming from esb comm
   *
   * @param esbJob esbJob
   * @param ivdJobEventTopic ivdJobEventTopic
   */
  void processJobEvent(EsbJob esbJob, String ivdJobEventTopic);

  /**
   * Method to handle vehicle track by event
   *
   * @param event event
   * @param vehicleTrackCommand vehicleTrackCommand
   */
  void sendVehicleTrackingByEvent(VehicleTrack event, VehicleTrackCommand vehicleTrackCommand);

  /**
   * Method for voice streaming event
   *
   * @param voiceEventRequest voiceEventRequest
   * @param event event with value START/STOP
   */
  void sendVoiceStreaming(VoiceStream event, VoiceEventRequest voiceEventRequest);

  /**
   * Method for job dispatch event
   *
   * @param jobDispatchEventRequest jobDispatchEventRequest
   * @param jobDispatchEventTopic jobDispatchEventTopic
   */
  void sendJobDispatchEvent(
      JobDispatchEventRequest jobDispatchEventRequest, String jobDispatchEventTopic);

  /**
   * Method for vehicle message event
   *
   * @param vehicleEventRequest vehicleEventRequest
   * @param vehicleMessageEventTopic vehicleMessageEventTopic
   */
  void sendVehicleMessageEvent(
      VehicleEventRequest vehicleEventRequest, String vehicleMessageEventTopic);

  /**
   * Method for sending notificationMessage to driver app.
   *
   * @param userId userId
   * @param notificationMessage notificationMessage
   */
  void sendNotificationToDriverApp(String userId, NotificationMessage notificationMessage);

  /**
   * This method will process the rcsa events
   *
   * @param rcsaInboundEvent rcsaInboundEvent
   * @param rcsaEventTopic topic
   */
  void processRcsaEvents(Rcsa rcsaInboundEvent, String rcsaEventTopic);

  void sendEmergencyCloseRequest(EmergencyAction emergencyAction, EmergencyClose emergencyClose);

  /**
   * Method for send Ping message
   *
   * @param messageId messageId
   * @param ivdPingMessageRequest ivdPingMessageRequest
   */
  void sendPingMessage(Integer messageId, IvdPingMessageRequest ivdPingMessageRequest);

  void processRcsaMessageEvent(RcsaMessage rcsaMessageInboundEvent, String rcsaMessageEventTopic);

  /**
   * @param mdtActionType mdtActionType
   * @param mdtRequestCommand mdtActionType
   * @return mdtResponseCommand mdtResponseCommand
   */
  MdtResponseCommand mdtAction(
      MdtAction mdtActionType, MdtRequestCommand mdtRequestCommand, byte[] ipAddr);

  /**
   * This method will process the ivd vehicle events coming from esb comm
   *
   * @param esbVehicle esbVehicle
   * @param ivdVehicleEventTopic ivdVehicleEventTopic
   * @param isRegular isRegular
   */
  void processVehicleEvent(EsbVehicle esbVehicle, String ivdVehicleEventTopic, boolean isRegular);

  /**
   * Method for driver suspend
   *
   * @param driverSuspendInboundEvent inbound event request
   * @param driverMessageEventTopic event topic
   */
  void processDriverSuspendEvent(Driver driverSuspendInboundEvent, String driverMessageEventTopic);

  /**
   * This method converts hex string to a java bean
   *
   * @param genericByteToBean genericByteToBean
   */
  void convertByteToBean(GenericByteToBean genericByteToBean);
}
