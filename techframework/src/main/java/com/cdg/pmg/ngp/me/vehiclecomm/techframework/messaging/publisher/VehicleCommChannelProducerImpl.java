package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.publisher;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobOffersResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.messaging.VehicleCommChannelProducer;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.entities.IvdMessageRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.*;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.NotificationChannel;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.NotificationTypeEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.mapper.MessageDataMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ParentRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.esbjobevent.EsbJobEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ivdresponse.IvdResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.jobdispatchevent.JobDispatchEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.producercsaevent.ProduceRcsaEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.rcsamessageevent.RcsaMessageEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.tripupload.UploadTripEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.model.NotificationMessageEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.model.NotificationMetadata;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.model.NotificationTemplateData;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.JsonHelper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** The type vehicle comm producer. */
@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleCommChannelProducerImpl implements VehicleCommChannelProducer {

  private final KafkaEventPublisher<String, ParentRequest> kafkaEventPublisher;

  private final MessageDataMapper messageDataMapper;

  private final JsonHelper jsonHelper;

  @Override
  public void sendFailedEvent(VehicleCommFailedRequest vehicleCommFailedRequest) {
    com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.vehiclecommfailedrequest
            .VehicleCommFailedRequest
        failedRequest = messageDataMapper.vehicleCommFailedToTechMapper(vehicleCommFailedRequest);
    log.info("[sendFailedEvent] Failed event message {}", jsonHelper.pojoToJson(failedRequest));
    kafkaEventPublisher.send(failedRequest);
  }

  @Override
  public void sendToIVDResponseEvent(IvdResponseData ivdResponseTopic) {
    IvdResponse ivdResponse = messageDataMapper.ivdResponseTechMapper(ivdResponseTopic);
    log.info(
        "[sendToIVDResponseEvent] Ivd response event message {}",
        jsonHelper.pojoToJson(ivdResponse));
    kafkaEventPublisher.send(ivdResponse);
  }

  @Override
  public void sendTripUploadEvent(TripInfo tripInfo) {
    UploadTripEvent uploadTripEvent = messageDataMapper.tripInfoToUploadTrip(tripInfo);
    log.info(
        "[sendTripUploadEvent] Upload trip event message {}",
        jsonHelper.pojoToJson(uploadTripEvent));
    kafkaEventPublisher.send(uploadTripEvent);
  }

  @Override
  public void sendNotificationMessageEvent(String userId, NotificationMessage notificationMessage) {
    NotificationMessageEvent<Object> driverAppNotificationMessageEvent =
        NotificationMessageEvent.builder()
            .userId(userId)
            .userType(VehicleCommAppConstant.DRIVER)
            .targetApp(VehicleCommAppConstant.DRIVER)
            .type(NotificationTypeEnum.TRANSACTIONAL)
            .channel(List.of(NotificationChannel.PUSH))
            .template(
                NotificationTemplateData.builder()
                    .eventType(notificationMessage.getNotificationType())
                    .lang(VehicleCommAppConstant.LANG_EN)
                    .jobType(VehicleCommAppConstant.DEFAULTS)
                    .customerType(VehicleCommAppConstant.DEFAULTS)
                    .productType(VehicleCommAppConstant.DRIVER.toUpperCase())
                    .build())
            .metadata(
                NotificationMetadata.builder()
                    .placeHolder(notificationMessage.getPlaceHolder())
                    .build())
            .build();
    log.info(
        "[sendNotificationMessageEvent] Notification message event message {}",
        jsonHelper.pojoToJson(driverAppNotificationMessageEvent));
    kafkaEventPublisher.send(driverAppNotificationMessageEvent);
  }

  @Override
  public void sendToRcsaEvent(RcsaEventProducerData rcsaEventProducerData) {
    ProduceRcsaEvent produceRcsaEvent =
        messageDataMapper.rcsaResponseTechMapper(rcsaEventProducerData);
    log.info("[sendToRcsaEvent] RCSA event message {}", jsonHelper.pojoToJson(produceRcsaEvent));
    kafkaEventPublisher.send(produceRcsaEvent);
  }

  @Override
  public void sendJobDispatchEvent(
      JobOffersResponse jobDispatchEventRequest, String ivdNo, String ipAddress) {
    JobDispatchEvent jobDispatchEvent =
        messageDataMapper.joboffersResponseToJobDispatchEvent(jobDispatchEventRequest);
    jobDispatchEvent.setEventName(JobDispatchEvent.JobEventTypeEnum.JOB_OFFER);
    jobDispatchEvent.setEventDate(LocalDateTime.now());
    jobDispatchEvent.setOfferableDevice(VehicleCommAppConstant.MDT);
    jobDispatchEvent.setIvdNo(ivdNo);
    jobDispatchEvent.setIpAddress(ipAddress);
    log.info(
        "[sendJobDispatchEvent] Job dispatch event message {}",
        jsonHelper.pojoToJson(jobDispatchEvent));
    kafkaEventPublisher.send(jobDispatchEvent);
  }

  @Override
  public void sendToRcsaMessageEvent(RcsaMessageEventProducerData rcsaMessageEventProducerData) {
    RcsaMessageEvent rcsaMessageEvent =
        messageDataMapper.cancelMessageToRcsaMessage(rcsaMessageEventProducerData);
    log.info(
        "[sendToRcsaMessageEvent] RCSA message event message {}",
        jsonHelper.pojoToJson(rcsaMessageEvent));
    kafkaEventPublisher.send(rcsaMessageEvent);
  }

  @Override
  public void sendToIvdJobEvent(IvdMessageRequest ivdMessageRequest) {
    EsbJobEvent esbJobEvent = messageDataMapper.ivdMessageRequestToEsbJobEvent(ivdMessageRequest);
    log.info("[sendToIvdJobEvent] Ivd Job Event message {}", jsonHelper.pojoToJson(esbJobEvent));
    kafkaEventPublisher.send(esbJobEvent);
  }
}
