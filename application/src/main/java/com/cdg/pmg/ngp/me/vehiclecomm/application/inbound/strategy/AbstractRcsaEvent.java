package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.UpdateVehicleStateRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VehicleDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.internal.BeanToByteConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.internal.ByteToBeanConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.messaging.VehicleCommChannelProducer;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.JobDispatchAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.VehicleAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.Rcsa;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdMessageEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdResponseData;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.RcsaEventMessageData;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.RcsaEventProducerData;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/** This abstract class provides some common methods for all strategies of RcsaEventStrategy */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractRcsaEvent implements RcsaEventStrategy, ApplicationContextAware {
  protected com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.MdtAPIService
      mdtAPIService;

  protected VehicleCommChannelProducer kafkaProducer;
  protected JobDispatchAPIService jobDispatchAPIService;
  protected VehicleAPIService vehicleAPIService;
  protected BeanToByteConverter beanToByteConverter;
  protected ByteToBeanConverter byteToBeanConverter;

  /**
   * Retrieves vehicle details by IVD number from MDT service.
   *
   * @param ivdNo The IVD number.
   * @return The {@link VehicleDetailsResponse} if available, else {@code null}.
   */
  protected VehicleDetailsResponse getVehicleDetailsByIvdNo(Integer ivdNo) {
    Optional<VehicleDetailsResponse> vehicleDetailsResponse =
        mdtAPIService.vehicleDetails(ivdNo, Boolean.FALSE);
    return vehicleDetailsResponse.orElse(null);
  }

  protected void sendAcknowledgementToMdt(Rcsa rcsaEvent) {
    IvdResponseData ivdResponse =
        IvdResponseData.builder()
            .eventIdentifier(IvdMessageEnum.MESSAGE_AKNOWLEDGE.getId())
            .message(generateAcknowledgeResponse(rcsaEvent))
            .ivdNo(rcsaEvent.getByteData().getIvdNo())
            .eventDate(rcsaEvent.getRcsaMessageRequest().getEventDate())
            .build();
    kafkaProducer.sendToIVDResponseEvent(ivdResponse);
  }

  private String generateAcknowledgeResponse(Rcsa rcsaEvent) {

    Integer ivdNo = rcsaEvent.getByteData().getIvdNo();
    String serialNo = String.valueOf(rcsaEvent.getByteData().getSerialNumber());
    String msgId = rcsaEvent.getRcsaMessageRequest().getEventIdentifier();
    String ipAddress =
        byteToBeanConverter.extractMdtIpAddress(rcsaEvent.getRcsaMessageRequest().getMessage());
    return beanToByteConverter.sendMessageAcknowledgement(ivdNo, serialNo, msgId, ipAddress);
  }

  /**
   * Produce message to kafka topic ngp.me.rcsa.event
   *
   * @param rcsaEvent event details
   */
  protected void produceMessageToRcsaEvent(Rcsa rcsaEvent) {
    RcsaEventMessageData messageData =
        RcsaEventMessageData.builder()
            .msgContent(rcsaEvent.getByteData().getMessageContent())
            .build();
    RcsaEventProducerData rcsaEventProducerData =
        RcsaEventProducerData.builder()
            .ivdNo(rcsaEvent.getByteData().getIvdNo())
            .vehicleId(rcsaEvent.getVehicleDetails().getId())
            .voiceStream(rcsaEvent.getByteData().getVoiceStream())
            .emergencyId(rcsaEvent.getByteData().getEmergencyId())
            .rcsaEvent(rcsaEvent.getRcsaEventsType())
            .driverId(rcsaEvent.getDriverId())
            .message(messageData)
            .build();
    kafkaProducer.sendToRcsaEvent(rcsaEventProducerData);
  }

  /**
   * Produce message to kafka topic ngp.me.rcsa.event
   *
   * @param rcsaEvent event details of Respond to structure event
   */
  protected void produceMsgToRcsaEvent(Rcsa rcsaEvent) {
    RcsaEventProducerData rcsaEventProducerData =
        RcsaEventProducerData.builder()
            .vehicleId(rcsaEvent.getVehicleDetails().getId())
            .message(
                RcsaEventMessageData.builder()
                    .uniqueMsgId(rcsaEvent.getByteData().getUniqueMsgId())
                    .selection(rcsaEvent.getByteData().getSelection())
                    .build())
            .rcsaEvent(rcsaEvent.getRcsaEventsType())
            .driverId(rcsaEvent.getDriverId())
            .build();
    kafkaProducer.sendToRcsaEvent(rcsaEventProducerData);
  }

  /**
   * Produce message to kafka topic ngp.me.rcsa.event
   *
   * @param rcsaEvent event details of Respond to Simple event
   */
  protected void produceMessageToResponseToSimpleMessageRcsaEvent(Rcsa rcsaEvent) {
    RcsaEventProducerData rcsaEventProducerData =
        RcsaEventProducerData.builder()
            .vehicleId(rcsaEvent.getVehicleDetails().getId())
            .message(
                RcsaEventMessageData.builder()
                    .uniqueMsgId(rcsaEvent.getByteData().getUniqueMsgId())
                    .build())
            .rcsaEvent(rcsaEvent.getRcsaEventsType())
            .driverId(rcsaEvent.getDriverId())
            .build();
    kafkaProducer.sendToRcsaEvent(rcsaEventProducerData);
  }

  /**
   * sends message to rcsa event
   *
   * @param rcsaEvent The {@link Rcsa} representing the rcsa event.
   */
  protected void publishMessageToRcsaEvent(Rcsa rcsaEvent) {
    RcsaEventProducerData rcsaEventProducerData =
        RcsaEventProducerData.builder()
            .emergencyId(rcsaEvent.getByteData().getEmergencyId())
            .vehicleId(rcsaEvent.getVehicleDetails().getId())
            .rcsaEvent(rcsaEvent.getRcsaEventsType())
            .driverId(rcsaEvent.getDriverId())
            .build();
    log.info("[publishMessageToRcsaEvent]rcsaEventProducerData: {}", rcsaEventProducerData);
    kafkaProducer.sendToRcsaEvent(rcsaEventProducerData);
  }

  /**
   * Call Vehicle service to update the vehicle-state
   *
   * @param rcsa The {@link Rcsa} representing the rcsa event.
   */
  protected void callVehicleSvcToUpdateVehicleState(Rcsa rcsa) {
    UpdateVehicleStateRequest updateVehicleStateRequest =
        UpdateVehicleStateRequest.builder()
            .jobNo(rcsa.getByteData().getJobNo())
            .driverId(rcsa.getDriverId())
            .deviceType(rcsa.getDeviceType())
            .eventTime(rcsa.getRcsaMessageRequest().getEventDate())
            .latitude(rcsa.getVehicleLatitude().getValue())
            .longitude(rcsa.getVehicleLongitude().getValue())
            .speed(rcsa.getByteData().getSpeed())
            .heading(rcsa.getHeading())
            .build();
    vehicleAPIService.updateVehicleState(
        rcsa.getVehicleDetails().getId(), rcsa.getVehicleEventType(), updateVehicleStateRequest);
  }

  @Override
  public void setApplicationContext(final ApplicationContext applicationContext)
      throws BeansException {
    mdtAPIService =
        applicationContext.getBean(
            com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.MdtAPIService.class);
    kafkaProducer = applicationContext.getBean(VehicleCommChannelProducer.class);
    jobDispatchAPIService = applicationContext.getBean(JobDispatchAPIService.class);
    vehicleAPIService = applicationContext.getBean(VehicleAPIService.class);
    beanToByteConverter = applicationContext.getBean(BeanToByteConverter.class);
    byteToBeanConverter = applicationContext.getBean(ByteToBeanConverter.class);
  }
}
