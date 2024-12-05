package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.impl;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.CmsConfiguration;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.Coordinate;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtApiResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtIvdDeviceConfigApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtLogOffApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtLogOnApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtPowerUpApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtRequestCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtResponseCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.PaymentMethodData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.UpdateVehicleStateRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VehicleDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.handlers.GenericByteToBeanHandler;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.port.VehicleCommApplicationService;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.JobEventStrategy;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.RcsaEventStrategy;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.VehicleEventStrategy;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.EsbVehicleMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.internal.BeanToByteConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.internal.ByteToBeanConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.messaging.VehicleCommChannelProducer;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.persistence.SchedulerRepositoryOutboundPort;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.MdtAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.VehicleAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.web.ConfigurationService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.Driver;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbJob;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbVehicle;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.GenericByteToBean;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.Rcsa;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.RcsaMessage;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.constants.VehicleCommDomainConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.entities.DriverEventVehicleRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DeviceType;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DriverNotificationTypeEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.EmergencyAction;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.Event;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdMessageEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdVehicleEventEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.JobEventTypeEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.MdtAction;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.MessageTypeEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.PingMessageEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.RequestServiceTypeEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VehicleTrack;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VoiceStream;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.ApplicationException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.BadRequestException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.ConstraintException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.DomainException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.ContentRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.DriverAppNotificationRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.EmergencyClose;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.GenericEventCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdPingMessageData;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdPingMessageRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdPingUpdateRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdResponseData;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.JobDispachNotificationRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.JobDispatchEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.NotificationMessage;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.RcsaMessageEventProducerData;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.SendAppStatusSyncRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.SendAutoBidButtonSyncRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.SendSyncronizeAutoBidToDriverAppRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommFailedRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleTrackCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VoiceEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.service.VehicleCommDomainService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/** This class is an implementation of Common Service class */
@RequiredArgsConstructor
@Slf4j
@ServiceComponent
public class VehicleCommApplicationServiceImpl implements VehicleCommApplicationService {

  private final Validator validator;
  private final BeanToByteConverter beanToByteConverter;
  private final VehicleCommChannelProducer producer;
  private final VehicleCommCacheService vehicleCommCacheService;
  private final ConfigurationService configurationService;
  private final MdtAPIService mdtAPIService;
  private final VehicleCommDomainService vehicleCommDomainService;
  private final ByteToBeanConverter byteToBeanConverter;
  private final EsbVehicleMapper esbVehicleMapper;
  private final VehicleAPIService vehicleAPIService;
  private final GenericByteToBeanHandler genericByteToBeanHandler;
  private final SchedulerRepositoryOutboundPort schedulerRepositoryOutboundPort;

  // strategy maps
  private final Map<IvdMessageEnum, JobEventStrategy> jobEventStrategiesMap;
  private final Map<IvdMessageEnum, RcsaEventStrategy> rcsaEventStrategiesMap;
  private final Map<IvdVehicleEventEnum, VehicleEventStrategy> vehicelEventStrategiesMap;

  /**
   * Process Job Events coming from ESB Comm
   *
   * @param esbJob esbJob
   * @param ivdJobEventTopic ivdJobEventTopic
   */
  @Override
  public void processJobEvent(EsbJob esbJob, String ivdJobEventTopic) {
    try {
      // Validate the request
      validateMessage(esbJob.getIvdMessageRequest());

      // Retrieves ivd message enum based on event id
      IvdMessageEnum ivdMessageEnum =
          IvdMessageEnum.getEventTypeByEventId(esbJob.getIvdMessageRequest().getEventIdentifier());

      // Selects the strategy to be executed based on the ivd message enum
      JobEventStrategy jobEventStrategy = jobEventStrategiesMap.getOrDefault(ivdMessageEnum, null);
      if (jobEventStrategy == null) {
        throw new IllegalArgumentException("Invalid event ID");
      }

      // Executes the strategy
      jobEventStrategy.handleJobEvent(esbJob, ivdJobEventTopic);

      // If retry is success, update the status as success in the table
      if (Objects.nonNull(esbJob.getIvdMessageRequest().getRetryId())) {
        schedulerRepositoryOutboundPort.updateEntityStatusToSuccess(
            esbJob.getIvdMessageRequest().getRetryId());
      }

    } catch (DomainException domainException) {

      log.error(
          "[processJobEvent] Domain exception while attempting to handle job event from esb-comm",
          domainException);
      throw new ApplicationException(
          domainException,
          esbJob.getIvdMessageRequest(),
          VehicleCommFailedRequest.class,
          ivdJobEventTopic);

    } catch (Exception e) {

      log.error(
          "[processJobEvent] Exception while attempting to handle job event from esb-comm", e);
      throw new ApplicationException(
          new DomainException(
              Arrays.toString(e.getStackTrace()), ErrorCode.INTERNAL_SERVER_ERROR.getCode()),
          esbJob.getIvdMessageRequest(),
          VehicleCommFailedRequest.class,
          ivdJobEventTopic);
    }
  }

  /**
   * Process Vehicle Events coming from ESB Comm
   *
   * @param esbVehicle esbVehicle
   * @param ivdVehicleEventTopic ivdVehicleEventTopic
   */
  @Override
  public void processVehicleEvent(
      EsbVehicle esbVehicle, String ivdVehicleEventTopic, boolean isRegular) {
    try {
      // Validate the request
      validateMessage(esbVehicle.getIvdVehicleEventMessageRequest());

      // Retrieves ivd vehicle event enum based on event id
      IvdVehicleEventEnum ivdVehicleEventEnum =
          getVehicleEventTypeUsingEventID(
              esbVehicle.getIvdVehicleEventMessageRequest().getEventIdentifier());

      // Selects the strategy to be executed based on the ivd vehicle event enum
      VehicleEventStrategy vehicleEventStrategy =
          vehicelEventStrategiesMap.getOrDefault(ivdVehicleEventEnum, null);

      if (vehicleEventStrategy == null) {
        throw new IllegalArgumentException(VehicleCommAppConstant.INVALID_EVENT_ID);
      }

      if ((!isRegular
              && ivdVehicleEventEnum
                  .getId()
                  .equalsIgnoreCase(VehicleCommAppConstant.REGULAR_REPORT_EVENT_ID.toString()))
          || (isRegular
              && !ivdVehicleEventEnum
                  .getId()
                  .equalsIgnoreCase(VehicleCommAppConstant.REGULAR_REPORT_EVENT_ID.toString()))) {
        // Throw exception if Regular report event received from other topics
        throw new IllegalArgumentException(VehicleCommAppConstant.INVALID_EVENT_ID);
      }

      // Executes the strategy
      vehicleEventStrategy.handleVehicleEvent(esbVehicle, ivdVehicleEventTopic);

    } catch (DomainException domainException) {

      log.error(
          "[processVehicleEvent] Domain exception while sending the vehicle event",
          domainException);
      throw new ApplicationException(
          domainException,
          esbVehicle.getIvdVehicleEventMessageRequest(),
          VehicleCommFailedRequest.class,
          ivdVehicleEventTopic);

    } catch (Exception e) {

      log.error("[processVehicleEvent] Exception while sending the vehicle event", e);
      throw new ApplicationException(
          new DomainException(
              Arrays.toString(e.getStackTrace()), ErrorCode.INTERNAL_SERVER_ERROR.getCode()),
          esbVehicle.getIvdVehicleEventMessageRequest(),
          VehicleCommFailedRequest.class,
          ivdVehicleEventTopic);
    }
  }

  /**
   * Process Rcsa Events
   *
   * @param rcsaEvent rcsaEvent
   * @param rcsaEventTopic rcsaEventTopic
   */
  @Override
  public void processRcsaEvents(Rcsa rcsaEvent, String rcsaEventTopic) {
    try {
      // Validate the request
      validateMessage(rcsaEvent.getRcsaMessageRequest());

      // Retrieves ivd message enum based on event id
      IvdMessageEnum ivdMessageEnum =
          getRcsaEventTypeUsingEventID(rcsaEvent.getRcsaMessageRequest().getEventIdentifier());

      // Selects the strategy to be executed based on the ivd message enum
      RcsaEventStrategy rcsaEventStrategy =
          rcsaEventStrategiesMap.getOrDefault(ivdMessageEnum, null);
      if (rcsaEventStrategy == null) {
        throw new IllegalArgumentException("Invalid event ID");
      }

      // Executes the strategy
      rcsaEventStrategy.handleRcsaEvent(rcsaEvent, rcsaEventTopic);
    } catch (DomainException domainException) {

      log.error(
          "[processRcsaEvents] Domain exception while sending the rcsa event", domainException);
      throw new ApplicationException(
          domainException,
          rcsaEvent.getRcsaMessageRequest(),
          VehicleCommFailedRequest.class,
          rcsaEventTopic);

    } catch (Exception e) {

      log.error("[processRcsaEvents] Exception while sending the rcsa event", e);
      throw new ApplicationException(
          new DomainException(
              Arrays.toString(e.getStackTrace()), ErrorCode.INTERNAL_SERVER_ERROR.getCode()),
          rcsaEvent.getRcsaMessageRequest(),
          VehicleCommFailedRequest.class,
          rcsaEventTopic);
    }
  }

  private IvdVehicleEventEnum getVehicleEventTypeUsingEventID(String eventIdentifier) {
    return Arrays.stream(IvdVehicleEventEnum.values())
        .filter(e -> e.getId().equals(eventIdentifier))
        .findFirst()
        .orElse(null);
  }

  /* NOTE -The below written code will be migrated to strategy pattern
     and will be removed once that is done
  */

  @Override
  public void sendVehicleTrackingByEvent(
      VehicleTrack event, VehicleTrackCommand vehicleTrackCommand) {
    GenericEventCommand message = convertVehicleTrackBeanToByteArray(event, vehicleTrackCommand);
    sendMsgToIvdResponse(
        message, vehicleTrackCommand.getId().toString(), vehicleTrackCommand.getIvdNo());
  }

  private GenericEventCommand convertVehicleTrackBeanToByteArray(
      VehicleTrack event, VehicleTrackCommand vehicleTrackCommand) {
    boolean isStart = Boolean.FALSE;
    if (event.equals(VehicleTrack.START)) isStart = Boolean.TRUE;
    return beanToByteConverter.convertToByteVehicleTrack(vehicleTrackCommand, isStart);
  }

  /**
   * Method for Voice Streaming Event
   *
   * @param event event
   * @param voiceEventRequest voiceEventRequest
   */
  @Override
  public void sendVoiceStreaming(VoiceStream event, VoiceEventRequest voiceEventRequest) {
    GenericEventCommand message = convertVoiceStreamingBeanToByteArray(event, voiceEventRequest);
    sendMsgToIvdResponse(
        message, voiceEventRequest.getId().toString(), voiceEventRequest.getIvdNo());
  }

  private GenericEventCommand convertVoiceStreamingBeanToByteArray(
      VoiceStream event, VoiceEventRequest voiceEventRequest) {
    boolean isStart = Boolean.FALSE;
    if (event.equals(VoiceStream.START)) isStart = Boolean.TRUE;
    return beanToByteConverter.convertToByteVoiceStreaming(voiceEventRequest, isStart);
  }

  @Override
  public void sendJobDispatchEvent(
      JobDispatchEventRequest jobDispatchEventRequest, String jobDispatchEventTopic) {
    try {
      validateEventRequest(jobDispatchEventRequest);
      validateJobStatus(jobDispatchEventRequest);
      JobEventTypeEnum eventName = jobDispatchEventRequest.getEventName();
      CmsConfiguration cmsConfiguration = configurationService.getCmsConfiguration();
      switch (eventName) {
        case JOB_MODIFY, CALLOUT_RESULT -> sendMessageFromJobDispatchEvent(
            jobDispatchEventRequest, eventName);
        case MDT_ALIVE -> {
          ContentRequest contentRequest = getContentRequest(jobDispatchEventRequest);
          contentRequest.setType(DriverNotificationTypeEnum.MDT_ALIVE_TYPE.getValue());
          contentRequest.setAdvanceBooking(
              jobDispatchEventRequest
                  .getJobType()
                  .equalsIgnoreCase(VehicleCommAppConstant.ADVANCE));
          PaymentMethodData paymentMethodData =
              vehicleCommCacheService.getPaymentMethodFromCacheAsName(
                  jobDispatchEventRequest.getPaymentMethod());
          contentRequest.setPaymentMode(paymentMethodData.getCodeDesc());
          contentRequest.setPaymentModeID(paymentMethodData.getPaymentMode());
          NotificationMessage notificationMessage =
              NotificationMessage.builder()
                  .placeHolder(contentRequest)
                  .notificationType(JobEventTypeEnum.MDT_ALIVE.getValue())
                  .build();
          sendNotificationToDriverApp(contentRequest.getDriverID(), notificationMessage);
        }
        case LEVY_UPDATE -> {
          GenericEventCommand genericEventCommand =
              beanToByteConverter.convertToByteLevyUpdate(jobDispatchEventRequest);
          sendMsgToIvdResponse(
              genericEventCommand,
              eventName.getValue(),
              Integer.parseInt(jobDispatchEventRequest.getIvdNo()));
        }
        case STREET_JOB -> {
          GenericEventCommand genericEventCommand =
              beanToByteConverter.convertToStreetHail(jobDispatchEventRequest);
          sendMsgToIvdResponse(
              genericEventCommand,
              eventName.getValue(),
              Integer.parseInt(jobDispatchEventRequest.getIvdNo()));
        }
        case JOB_CANCEL -> {
          if (VehicleCommAppConstant.MDT.equals(jobDispatchEventRequest.getOfferableDevice())) {
            validateIvdNo(jobDispatchEventRequest.getIvdNo());
            GenericEventCommand genericEventCommand =
                beanToByteConverter.convertToJobCancelInIvdResponse(jobDispatchEventRequest);
            GenericEventCommand[] sendingObjects =
                sendMultipleObjectsToIvdResponse(
                    genericEventCommand, cmsConfiguration.getJobCancelTime());
            // Publish all the elements of the new array to the ivdResponse
            sendArrayElementsToIvdResponse(
                sendingObjects,
                eventName.getValue(),
                Integer.parseInt(jobDispatchEventRequest.getIvdNo()));
          } else {
            var notificationPlaceholderData =
                esbVehicleMapper.mapJobDispachForNotification(jobDispatchEventRequest);
            notificationPlaceholderData.setType(
                DriverNotificationTypeEnum.JOB_CANCELLED.getValue());
            NotificationMessage notificationMessage =
                NotificationMessage.builder()
                    .notificationType(eventName.getValue())
                    .placeHolder(notificationPlaceholderData)
                    .build();
            sendNotificationToDriverApp(jobDispatchEventRequest.getDriverId(), notificationMessage);
          }
        }
        case JOB_CONFIRM -> {
          if (VehicleCommAppConstant.MDT.equals(jobDispatchEventRequest.getOfferableDevice())) {

            validateIvdNo(jobDispatchEventRequest.getIvdNo());

            GenericEventCommand genericEventCommand =
                beanToByteConverter.convertToJobConfirmation(jobDispatchEventRequest);

            GenericEventCommand[] sendingObjects = new GenericEventCommand[] {genericEventCommand};
            if ((jobDispatchEventRequest.getAutoAcceptFlag()
                != VehicleCommAppConstant.JOB_CONFIRM_AUTO_ACCEPT_FLAG)) {
              sendingObjects =
                  sendMultipleObjectsToIvdResponse(
                      genericEventCommand, cmsConfiguration.getJobConfirmTime());
            }
            sendArrayElementsToIvdResponse(
                sendingObjects,
                eventName.getValue(),
                Integer.parseInt(jobDispatchEventRequest.getIvdNo()));
          } else {
            PaymentMethodData paymentMethodData =
                vehicleCommCacheService.getPaymentMethodFromCacheAsName(
                    jobDispatchEventRequest.getPaymentMethod());
            var notificationPlaceholderData =
                esbVehicleMapper.mapJobDispachForNotification(jobDispatchEventRequest);
            notificationPlaceholderData.setType(
                DriverNotificationTypeEnum.JOB_CONFIRMED.getValue());
            notificationPlaceholderData.setPaymentMode(paymentMethodData.getCodeDesc());
            notificationPlaceholderData.setPaymentModeID(paymentMethodData.getPaymentMode());
            NotificationMessage notificationMessage =
                NotificationMessage.builder()
                    .notificationType(eventName.getValue())
                    .placeHolder(notificationPlaceholderData)
                    .build();
            sendNotificationToDriverApp(jobDispatchEventRequest.getDriverId(), notificationMessage);
          }
        }
        case JOB_OFFER -> {
          if (VehicleCommAppConstant.MDT.equals(jobDispatchEventRequest.getOfferableDevice())) {
            validateIvdNo(jobDispatchEventRequest.getIvdNo());
            jobOfferAckFlagCheck(jobDispatchEventRequest);
            GenericEventCommand genericEventCommand =
                beanToByteConverter.convertToJobOffer(jobDispatchEventRequest);
            sendMsgToIvdResponse(
                genericEventCommand,
                eventName.getValue(),
                Integer.parseInt(jobDispatchEventRequest.getIvdNo()));
          } else {
            PaymentMethodData paymentMethodData =
                vehicleCommCacheService.getPaymentMethodFromCacheAsName(
                    jobDispatchEventRequest.getPaymentMethod());
            var notificationPlaceholderData =
                esbVehicleMapper.mapJobDispachForNotification(jobDispatchEventRequest);
            notificationPlaceholderData.setType(
                DriverNotificationTypeEnum.AVAILABLE_JOB.getValue());
            notificationPlaceholderData.setPaymentMode(paymentMethodData.getCodeDesc());
            notificationPlaceholderData.setPaymentModeID(paymentMethodData.getPaymentMode());
            setAdvanceBookingField(jobDispatchEventRequest, notificationPlaceholderData);
            NotificationMessage notificationMessage =
                NotificationMessage.builder()
                    .notificationType(eventName.getValue())
                    .placeHolder(notificationPlaceholderData)
                    .build();
            sendNotificationToDriverApp(jobDispatchEventRequest.getDriverId(), notificationMessage);
          }
        }
        default -> log.info("[sendJobDispatchEvent] Event name {} is invalid", eventName);
      }
    } catch (DomainException domainException) {
      log.error(
          "[sendJobDispatchEvent] Domain exception while sending the job dispatch event",
          domainException);
      throw new ApplicationException(
          domainException,
          jobDispatchEventRequest,
          VehicleCommFailedRequest.class,
          jobDispatchEventTopic);
    } catch (Exception e) {
      log.error("[sendJobDispatchEvent] Exception while sending the job dispatch event", e);
      throw new ApplicationException(
          new DomainException(
              Arrays.toString(e.getStackTrace()), ErrorCode.INTERNAL_SERVER_ERROR.getCode()),
          jobDispatchEventRequest,
          VehicleCommFailedRequest.class,
          jobDispatchEventTopic);
    }
  }

  /**
   * This method is used to check if the job status coming in the request is valid or not. If the ID
   * corresponding to the status is 0, then the status is not valid to be used here and we reject
   * that request
   *
   * @param jobDispatchEventRequest jobDispatchEventRequest
   */
  private void validateJobStatus(JobDispatchEventRequest jobDispatchEventRequest) {
    if (Objects.nonNull(jobDispatchEventRequest.getJobStatus())
        && jobDispatchEventRequest.getJobStatus().getId() == 0) {
      throw new BadRequestException(ErrorCode.INVALID_JOB_STATUS.getCode());
    }
  }

  private void setAdvanceBookingField(
      JobDispatchEventRequest jobDispatchEventRequest,
      JobDispachNotificationRequest notificationPlaceholderData) {
    notificationPlaceholderData.setAdvanceBooking(
        jobDispatchEventRequest.getJobType().equalsIgnoreCase(VehicleCommAppConstant.ADVANCE));
  }

  /**
   * if the offerable device is IVD, then will publish to ivdResponse else notification subscribe
   *
   * @param jobDispatchEventRequest jobDispatchEventRequest
   * @param eventName eventName
   */
  private void sendMessageFromJobDispatchEvent(
      JobDispatchEventRequest jobDispatchEventRequest, JobEventTypeEnum eventName) {

    if (VehicleCommAppConstant.MDT.equals(jobDispatchEventRequest.getOfferableDevice())) {
      new GenericEventCommand();
      validateIvdNo(jobDispatchEventRequest.getIvdNo());
      GenericEventCommand genericEventCommand;
      if (eventName.equals(JobEventTypeEnum.JOB_MODIFY)) {
        genericEventCommand = beanToByteConverter.convertToJobModification(jobDispatchEventRequest);
      } else {
        genericEventCommand = beanToByteConverter.convertToByteCallOut(jobDispatchEventRequest);
      }
      sendMsgToIvdResponse(
          genericEventCommand,
          eventName.getValue(),
          Integer.parseInt(jobDispatchEventRequest.getIvdNo()));

    } else {
      PaymentMethodData paymentMethodData =
          vehicleCommCacheService.getPaymentMethodFromCacheAsName(
              jobDispatchEventRequest.getPaymentMethod());

      var notificationPlaceholderData =
          esbVehicleMapper.mapJobDispachForNotification(jobDispatchEventRequest);
      notificationPlaceholderData.setType(DriverNotificationTypeEnum.JOB_MODIFICATION.getValue());
      notificationPlaceholderData.setPaymentMode(paymentMethodData.getCodeDesc());
      notificationPlaceholderData.setPaymentModeID(paymentMethodData.getPaymentMode());
      setAdvanceBookingField(jobDispatchEventRequest, notificationPlaceholderData);
      NotificationMessage notificationMessage =
          NotificationMessage.builder()
              .notificationType(eventName.getValue())
              .placeHolder(notificationPlaceholderData)
              .build();
      log.info(
          "[sendMessageFromJobDispatchEvent] Notification placeholder request for event {} - {}",
          eventName,
          notificationMessage.getNotificationType());
      sendNotificationToDriverApp(jobDispatchEventRequest.getDriverId(), notificationMessage);
    }
  }

  // Added  code for Vehicle event
  @Override
  public void sendVehicleMessageEvent(
      VehicleEventRequest vehicleEventRequest, String vehicleMessageEventTopic) {
    try {
      validateEventRequest(vehicleEventRequest);
      Event eventName = vehicleEventRequest.getEvent();
      if (Objects.requireNonNull(eventName) == Event.DISABLE_AUTO_BID
          || eventName == Event.ENABLE_AUTO_BID
          || eventName == Event.ENABLE_AUTO_ACCEPT) {
        sendMessage(vehicleEventRequest, eventName);
      } else if (Objects.requireNonNull(eventName) == Event.APP_STATUS_SYNC) {
        sendAppStatusSyncNotification(vehicleEventRequest);
      }
    } catch (DomainException domainException) {
      log.error(
          "[sendVehicleMessageEvent] Domain exception while sending the vehicle dispatch event",
          domainException);
      throw new ApplicationException(
          domainException,
          vehicleEventRequest,
          VehicleCommFailedRequest.class,
          vehicleMessageEventTopic);
    } catch (Exception e) {
      log.error("[sendVehicleMessageEvent] Exception while sending the vehicle dispatch event", e);
      throw new ApplicationException(
          new DomainException(
              Arrays.toString(e.getStackTrace()), ErrorCode.INTERNAL_SERVER_ERROR.getCode()),
          vehicleEventRequest,
          VehicleCommFailedRequest.class,
          vehicleMessageEventTopic);
    }
  }

  /**
   * This method processes the vehicle event request and sends data to driver application.
   *
   * @param vehicleEventRequest the vehicle event request containing details of the event.
   */
  private void sendAppStatusSyncNotification(VehicleEventRequest vehicleEventRequest) {
    NotificationMessage notificationMessage =
        NotificationMessage.builder()
            .notificationType(JobEventTypeEnum.MDT_COMPLETED.getValue())
            .placeHolder(generateAppStatusSyncRequest(vehicleEventRequest))
            .build();
    sendNotificationToDriverApp(vehicleEventRequest.getDriverId(), notificationMessage);
  }

  /**
   * Sends a message based on the vehicle event request and event name. This method processes the
   * vehicle event request and sends data to either the IVD or a driver application , depending on
   * the device type specified in the request.
   *
   * @param vehicleEventRequest the vehicle event request containing details of the event.
   * @param eventName the name of the event to be processed and sent.
   */
  private void sendMessage(VehicleEventRequest vehicleEventRequest, Event eventName) {
    if (VehicleCommAppConstant.MDT.equals(vehicleEventRequest.getDeviceType().getValue())) {
      validateIvdNo(vehicleEventRequest.getIvdNo());
      // Call MDT service
      var vehicleDetails = getVehicleDetailsByIvdNo(vehicleEventRequest.getIvdNo());
      // byteConversion Logic
      GenericEventCommand genericEventCommand =
          beanToByteConverter.convertToByteSyncronizedAutoBid(vehicleEventRequest, vehicleDetails);
      // Produce data to Ivd Response
      sendMsgToIvdResponse(
          genericEventCommand, eventName.toString(), vehicleEventRequest.getIvdNo());
    } else {
      NotificationMessage notificationMessage =
          NotificationMessage.builder()
              .notificationType(VehicleCommAppConstant.AUTOBID_STATUS_EVENT)
              .placeHolder(generateSyncronizedNotificationMessage(vehicleEventRequest))
              .build();
      sendNotificationToDriverApp(vehicleEventRequest.getDriverId(), notificationMessage);
    }
  }

  protected VehicleDetailsResponse getVehicleDetailsByIvdNo(Integer ivdNo) {
    Optional<VehicleDetailsResponse> vehicleDetailsResponse =
        mdtAPIService.vehicleDetails(ivdNo, Boolean.FALSE);
    return vehicleDetailsResponse.orElse(null);
  }

  /**
   * Sending the genericEventCommandArray to kafka topic ivdResponse
   *
   * @param sendingObjects array of GenericEventCommand
   * @param eventName eventName
   * @param ivdNo ivdNo
   */
  private void sendArrayElementsToIvdResponse(
      GenericEventCommand[] sendingObjects, String eventName, Integer ivdNo) {
    for (GenericEventCommand sendingObj : sendingObjects) {
      sendMsgToIvdResponse(sendingObj, eventName, ivdNo);
    }
  }

  /**
   * Iterate over the new array and set the 8th index of each array to byte value of (i + 1)
   *
   * @param genericEventCommand genericEventCommand
   * @param jobCancelTime jobCancelTime
   * @return GenericEventCommand[]
   */
  private GenericEventCommand[] sendMultipleObjectsToIvdResponse(
      GenericEventCommand genericEventCommand, Integer jobCancelTime) {
    if (jobCancelTime == 0) {
      return new GenericEventCommand[] {genericEventCommand};
    }
    GenericEventCommand[] msgArrays = new GenericEventCommand[jobCancelTime];

    /* For each message to send ,set the sequence number in the byte array
    Note: The sequence number position is at 8 in the byte array
    */
    IntStream.range(0, jobCancelTime)
        .forEach(
            i -> {
              genericEventCommand.getByteArray()[8] = (byte) (i + 1);
              msgArrays[i] = genericEventCommand;
            });
    return msgArrays;
  }

  private void jobOfferAckFlagCheck(JobDispatchEventRequest jobDispatchEventRequest) {
    if (Boolean.TRUE.equals(jobDispatchEventRequest.getAckFlag())) {
      vehicleCommCacheService.isCacheEntryInStoreAndForward(
          jobDispatchEventRequest.getMessageId(),
          Integer.valueOf(jobDispatchEventRequest.getIvdNo()),
          jobDispatchEventRequest.getJobNo());
    }
  }

  private <T> void validateEventRequest(T requestObject) {
    try {
      Set<ConstraintViolation<T>> violations = validator.validate(requestObject);
      if (!violations.isEmpty()) {
        throw new ConstraintViolationException(violations);
      }
    } catch (ConstraintViolationException violationException) {
      log.error("[validateEventRequest] Exception while validating event ", violationException);
      throw new ConstraintException(violationException);
    }
  }

  private void sendMsgToIvdResponse(
      GenericEventCommand genericEventCommand, String eventName, int ivdNo) {
    IvdResponseData ivdResponse =
        IvdResponseData.builder()
            .eventIdentifier(eventName)
            .message(genericEventCommand.getByteArrayMessage())
            .ivdNo(ivdNo)
            .eventDate(LocalDateTime.now())
            .build();
    producer.sendToIVDResponseEvent(ivdResponse);
  }

  private static ContentRequest getContentRequest(JobDispatchEventRequest jobDispatchEventRequest) {
    return ContentRequest.builder()
        .vehicleID(jobDispatchEventRequest.getVehicleId())
        .jobNo(jobDispatchEventRequest.getJobNo())
        .jobType(jobDispatchEventRequest.getJobType())
        .pickUpTime(jobDispatchEventRequest.getPickupTime())
        .pickupAddr(jobDispatchEventRequest.getPickupAddr())
        .pickupPt(jobDispatchEventRequest.getPickupPt())
        .pickupLat(jobDispatchEventRequest.getPickupLat())
        .pickupLng(jobDispatchEventRequest.getPickupLng())
        .destAddr(jobDispatchEventRequest.getDestAddr())
        .destPt(jobDispatchEventRequest.getDestPoint())
        .bookingFee(jobDispatchEventRequest.getBookingFee())
        .jobStatus(
            null != jobDispatchEventRequest.getJobStatus()
                ? jobDispatchEventRequest.getJobStatus().getId()
                : null)
        .destLat(jobDispatchEventRequest.getDestLat())
        .destLng(jobDispatchEventRequest.getDestLng())
        .notes(jobDispatchEventRequest.getNotes())
        .passengerName(jobDispatchEventRequest.getPassengerName())
        .bookingDate(jobDispatchEventRequest.getBookingDate())
        .eta(jobDispatchEventRequest.getEta())
        .promoCode(jobDispatchEventRequest.getPromoCode())
        .promoAmount(jobDispatchEventRequest.getPromoAmount())
        .loyaltyAmount(jobDispatchEventRequest.getLoyaltyAmount())
        .paymentMode(jobDispatchEventRequest.getPaymentMethod())
        .productType(jobDispatchEventRequest.getProductType())
        .productDesc(jobDispatchEventRequest.getProductDesc())
        .pdtID(jobDispatchEventRequest.getProductId())
        .ccNumber(jobDispatchEventRequest.getCcNumber())
        .ccExpiry(jobDispatchEventRequest.getCcExpiry())
        .autoAcceptFlag(jobDispatchEventRequest.getAutoAcceptFlag())
        .autoAssignFlag(jobDispatchEventRequest.getAutoAssignFlag())
        .offerTimeOut(jobDispatchEventRequest.getOfferTimeout())
        .jobSurgeSigns(jobDispatchEventRequest.getJobSurgeSigns())
        .privateField(jobDispatchEventRequest.getPrivateField())
        .cpFreeInsurance(jobDispatchEventRequest.getCpFreeInsurance())
        .comfortProtectPremium(jobDispatchEventRequest.getComfortProtectPremium())
        .intermediateAddr(jobDispatchEventRequest.getMultiStop().getIntermediateAddr())
        .intermediateLat(jobDispatchEventRequest.getMultiStop().getIntermediateLat())
        .intermediateLng(jobDispatchEventRequest.getMultiStop().getIntermediateLng())
        .intermediatePt(jobDispatchEventRequest.getMultiStop().getIntermediatePt())
        .intermediateZoneId(jobDispatchEventRequest.getMultiStop().getIntermediateZoneId())
        .driverID(jobDispatchEventRequest.getDriverId())
        .platformFeeItem(jobDispatchEventRequest.getPlatformFeeItem())
        .alert(jobDispatchEventRequest.getAlert())
        .build();
  }

  /**
   * Method to generate notification method for Suspend Message
   *
   * @param driverEventVehicleRequest driver event request
   * @return DriverAppNotificationRequest
   */
  private DriverAppNotificationRequest generateSuspendMessage(
      DriverEventVehicleRequest driverEventVehicleRequest) {
    String suspendMessage =
        configurationService.getCmsConfiguration().getAutoAcceptSuspendMessage();
    String broadCastMsgAutoAccept =
        suspendMessage
            + VehicleCommAppConstant.SPACE_SEPARATOR
            + driverEventVehicleRequest.getSuspendTimeInMinutes()
            + VehicleCommAppConstant.SPACE_SEPARATOR
            + VehicleCommAppConstant.MINUTES;

    return DriverAppNotificationRequest.builder()
        .vehicleId(driverEventVehicleRequest.getVehicleId())
        .driverId(driverEventVehicleRequest.getDriverId())
        .broadcastMessage("")
        .autoBidStatus(VehicleCommAppConstant.AUTO_BID_STATUS_SUSPEND)
        .autoBidBtnEnable(VehicleCommAppConstant.AUTO_BID_BTN_ENABLE_SUSPEND)
        .isLoginEvent(VehicleCommAppConstant.IS_LOGIN_EVENT)
        .isLogoutEvent(VehicleCommAppConstant.IS_LOGOUT_EVENT)
        .type(DriverNotificationTypeEnum.APP_AUTO_MSG.getValue())
        .broadcastMessage(broadCastMsgAutoAccept)
        .build();
  }

  /**
   * @param driverEventVehicleRequest driver event request
   * @return DriverAppNotificationRequest
   */
  private DriverAppNotificationRequest generateSuspendMessageAutoBid(
      DriverEventVehicleRequest driverEventVehicleRequest) {
    String autoBidSuspendMessage =
        configurationService.getCmsConfiguration().getAutoBidSuspendMessage();

    return DriverAppNotificationRequest.builder()
        .vehicleId(driverEventVehicleRequest.getVehicleId())
        .driverId(driverEventVehicleRequest.getDriverId())
        .broadcastMessage("")
        .autoBidStatus(VehicleCommAppConstant.AUTO_BID_STATUS_SUSPEND)
        .autoBidBtnEnable(VehicleCommAppConstant.AUTO_BID_BTN_ENABLE_SUSPEND)
        .isLoginEvent(VehicleCommAppConstant.IS_LOGIN_EVENT)
        .isLogoutEvent(VehicleCommAppConstant.IS_LOGOUT_EVENT)
        .type(DriverNotificationTypeEnum.APP_AUTO_MSG.getValue())
        .broadcastMessage(autoBidSuspendMessage)
        .build();
  }

  private SendAutoBidButtonSyncRequest generateAutoBidButtonSync(
      DriverEventVehicleRequest driverEventVehicleRequest, String commandVariable) {
    return SendAutoBidButtonSyncRequest.builder()
        .vehicleId(driverEventVehicleRequest.getVehicleId())
        .driverId(driverEventVehicleRequest.getDriverId())
        .autoBidStatus(commandVariable)
        .autoBidBtnEnable(commandVariable)
        .type(DriverNotificationTypeEnum.APP_AUTO_BID_STATUS.getValue())
        .build();
  }

  /**
   * Method to generate notification method for Auto Bid sync request
   *
   * @param driverEventVehicleRequest driver event request
   * @return SendAutoBidButtonSyncRequest
   */
  private SendAutoBidButtonSyncRequest generateAutoBidButtonSyncRequest(
      DriverEventVehicleRequest driverEventVehicleRequest, String commandVariable) {
    return SendAutoBidButtonSyncRequest.builder()
        .vehicleId(driverEventVehicleRequest.getVehicleId())
        .driverId(driverEventVehicleRequest.getDriverId())
        .autoBidStatus(commandVariable)
        .autoBidBtnEnable(commandVariable)
        .type(DriverNotificationTypeEnum.APP_AUTO_BID_BTN.getValue())
        .build();
  }

  /**
   * Method to generate notification request for App Status Sync
   *
   * @param vehicleEventRequest vehicle event request
   * @return SendAppStatusSyncRequest
   */
  private SendAppStatusSyncRequest generateAppStatusSyncRequest(
      VehicleEventRequest vehicleEventRequest) {
    return SendAppStatusSyncRequest.builder()
        .vehicleId(vehicleEventRequest.getVehicleId())
        .driverId(vehicleEventRequest.getDriverId())
        .appstatus(vehicleEventRequest.getStatus().toUpperCase())
        .type(DriverNotificationTypeEnum.APP_STATUS_SYNC.getValue())
        .build();
  }

  private SendSyncronizeAutoBidToDriverAppRequest generateSyncronizedNotificationMessage(
      VehicleEventRequest vehicleEventRequest) {
    return SendSyncronizeAutoBidToDriverAppRequest.builder()
        .vehicleId(vehicleEventRequest.getVehicleId())
        .driverId(vehicleEventRequest.getDriverId())
        .autoAcceptStatus(getAutoBidStatusType(vehicleEventRequest.getEvent().toString()))
        .type(DriverNotificationTypeEnum.APP_AUTO_BID_STATUS.getValue())
        .build();
  }

  private void sendPingMessageEventToMdt(
      Integer seqNo, Integer refNo, Integer messageId, Integer ivdNo) {
    IvdPingMessageData appResvEvent =
        IvdPingMessageData.builder()
            .pingMessageEvent(PingMessageEvent.APP_SEND)
            .timestamp(LocalDateTime.now())
            .build();
    List<IvdPingMessageData> pingMessageList = new ArrayList<>();
    pingMessageList.add(appResvEvent);

    IvdPingUpdateRequest ivdPingUpdateRequest =
        IvdPingUpdateRequest.builder()
            .requestNumber(String.valueOf(refNo))
            .sequenceNumber(String.valueOf(seqNo))
            .messageId(String.valueOf(messageId))
            .ivdPingData(pingMessageList)
            .ivdNo((ivdNo))
            .build();
    mdtAPIService.updateIvdPing(ivdPingUpdateRequest);
  }

  private Integer getAutoBidStatusType(String eventContent) {
    log.debug("getEventType {}", eventContent);
    return switch (eventContent) {
      case "DISABLE_AUTO_BID" -> 0;
      case "ENABLE_AUTO_BID" -> 1;
      case "ENABLE_AUTO_ACCEPT" -> 2;
      default -> throw new BadRequestException(ErrorCode.INVALID_VEHICLE_EVENT.getCode());
    };
  }

  private void validateIvdNo(Integer ivdNo) {
    if (Objects.isNull(ivdNo)) throw new BadRequestException(ErrorCode.IVD_NO_REQUIRED.getCode());
  }

  private void validateIvdNo(String ivdNo) {
    if (StringUtils.isBlank(ivdNo))
      throw new BadRequestException(ErrorCode.IVD_NO_REQUIRED.getCode());
  }

  /**
   * Method for sending notificationMessage to driver app.
   *
   * @param userId userId
   * @param notificationMessage notificationMessage
   */
  @Override
  public void sendNotificationToDriverApp(String userId, NotificationMessage notificationMessage) {
    log.info(
        "[sendNotificationToDriverApp] Notification to driver app for userId {} - {}",
        userId,
        notificationMessage);
    producer.sendNotificationMessageEvent(userId, notificationMessage);
  }

  /**
   * This method sends ping message
   *
   * @param messageId messageId
   * @param ivdPingMessageRequest ivdPingMessageRequest
   */
  @Override
  public void sendPingMessage(Integer messageId, IvdPingMessageRequest ivdPingMessageRequest) {
    try {
      GenericEventCommand genericEventCommand =
          beanToByteConverter.convertToBytePingMessage(messageId, ivdPingMessageRequest);
      IvdResponseData ivdResponse =
          IvdResponseData.builder()
              .message(genericEventCommand.getByteArrayMessage())
              .ivdNo(ivdPingMessageRequest.getIvdNo())
              .eventDate(LocalDateTime.now())
              .build();
      producer.sendToIVDResponseEvent(ivdResponse);
      sendPingMessageEventToMdt(
          ivdPingMessageRequest.getSeqNo(),
          ivdPingMessageRequest.getRefNo(),
          messageId,
          ivdPingMessageRequest.getIvdNo());
    } catch (DomainException domainException) {
      log.error("[sendPingMessage] Exception while sending Ping Message", domainException);
      throw new ApplicationException(
          domainException, ivdPingMessageRequest, VehicleCommFailedRequest.class, null);
    }
  }

  /**
   * This method sends rcsa message
   *
   * @param rcsaMessage rcsaMessage
   * @param rcsaMessageEventTopic event topic
   */
  @Override
  public void processRcsaMessageEvent(RcsaMessage rcsaMessage, String rcsaMessageEventTopic) {
    try {
      validateMessage(rcsaMessage.getRcsaMessageEventRequest());
      validateDeviceTypeAndMessageType(rcsaMessage);
      GenericEventCommand genericEventCommand =
          beanToByteConverter.convertToByteRcsaMessage(rcsaMessage);
      vehicleCommDomainService.setIvdByteArray(rcsaMessage, genericEventCommand);
      log.info(
          "[processRcsaMessageEvent]send rcsaMessage to esb-ms, rcsaMessage: {} ", rcsaMessage);
      sendMsgToIvdResponse(
          rcsaMessage.getGenericEventCommand(),
          rcsaMessageEventTopic,
          rcsaMessage.getRcsaMessageEventRequest().getIvdNo());
    } catch (DomainException domainException) {
      log.error(
          "[processRcsaMessageEvent] Domain exception while sending the Rcsa Message event",
          domainException);
      throw new ApplicationException(
          domainException,
          rcsaMessage.getRcsaMessageEventRequest(),
          VehicleCommFailedRequest.class,
          rcsaMessageEventTopic);
    } catch (Exception e) {
      log.error("[processRcsaMessageEvent] Exception while sending the Rcsa Message event", e);
      throw new ApplicationException(
          new DomainException(
              Arrays.toString(e.getStackTrace()), ErrorCode.INTERNAL_SERVER_ERROR.getCode()),
          rcsaMessage.getRcsaMessageEventRequest(),
          VehicleCommFailedRequest.class,
          rcsaMessageEventTopic);
    }
  }

  /**
   * validate deviceType and messageType
   *
   * @param rcsaMessage rcsa message
   */
  private void validateDeviceTypeAndMessageType(RcsaMessage rcsaMessage) {
    if (rcsaMessage.getRcsaMessageEventRequest().getDeviceType().equals(DeviceType.MDT)
        && StringUtils.isBlank(rcsaMessage.getRcsaMessageEventRequest().getMessageType())) {
      vehicleCommDomainService.setMessageType(rcsaMessage, MessageTypeEnum.COMMAND_TYPE_3);
    } else {
      MessageTypeEnum messageTypeEnum = validateMessageType(rcsaMessage);
      vehicleCommDomainService.setMessageType(rcsaMessage, messageTypeEnum);
    }
  }

  /**
   * validate messageType
   *
   * @param rcsaMessage rcsa message
   */
  private MessageTypeEnum validateMessageType(RcsaMessage rcsaMessage) {
    if (rcsaMessage.getRcsaMessageEventRequest().getMessageType() == null) {
      throw new BadRequestException(ErrorCode.INVALID_MESSAGE_TYPE.getCode());
    }
    MessageTypeEnum messageTypeEnum =
        MessageTypeEnum.fromString(rcsaMessage.getRcsaMessageEventRequest().getMessageType());
    if (messageTypeEnum != null) {
      return messageTypeEnum;
    } else {
      throw new BadRequestException(ErrorCode.INVALID_MESSAGE_TYPE.getCode());
    }
  }

  @Override
  public void processDriverSuspendEvent(Driver driverEvent, String driverMessageEventTopic) {
    try {
      if (Objects.equals(
          driverEvent.getDriverEventRequest().getEventIdentifier(),
          VehicleCommAppConstant.DRIVER_SUSPEND)) {
        validateMessage(driverEvent.getDriverEventRequest());
        for (DriverEventVehicleRequest driverEventVehicleRequest :
            driverEvent.getDriverEventRequest().getVehicleSuspends()) {
          processDriverSuspension(driverEvent, driverEventVehicleRequest);
        }
      } else {
        throw new BadRequestException(ErrorCode.INVALID_EVENT_IDENTIFIER.getCode());
      }
    } catch (DomainException domainException) {
      log.error(
          "[processDriverSuspendEvent] Domain exception while sending the driver event",
          domainException);
      throw new ApplicationException(
          domainException,
          driverEvent.getDriverEventRequest(),
          VehicleCommFailedRequest.class,
          driverMessageEventTopic);
    } catch (Exception e) {
      log.error("[processDriverSuspendEvent] Exception while sending the driver event", e);
      throw new ApplicationException(
          new DomainException(
              Arrays.toString(e.getStackTrace()), ErrorCode.INTERNAL_SERVER_ERROR.getCode()),
          driverEvent.getDriverEventRequest(),
          VehicleCommFailedRequest.class,
          driverMessageEventTopic);
    }
  }

  /**
   * This method converts hex string to a java bean
   *
   * @param genericByteToBean genericByteToBean
   */
  @Override
  public void convertByteToBean(GenericByteToBean genericByteToBean) {
    genericByteToBeanHandler.handle(genericByteToBean);
  }

  /**
   * Method to process driver suspension
   *
   * @param driverEvent driver event
   * @param driverEventVehicleRequest driver vehicle request
   */
  private void processDriverSuspension(
      Driver driverEvent, DriverEventVehicleRequest driverEventVehicleRequest) {
    if (DeviceType.MDT.equals(driverEventVehicleRequest.getDeviceType())) {
      driverSuspendMdt(driverEvent, driverEventVehicleRequest);
    } else if (DeviceType.ANDROID.equals(driverEventVehicleRequest.getDeviceType())
        || DeviceType.IPHONE.equals(driverEventVehicleRequest.getDeviceType())) {
      driverSuspendPhone(driverEvent, driverEventVehicleRequest);
    }
  }

  /**
   * process driver suspension for Iphone and Android
   *
   * @param driverEvent driver event
   * @param driverEventVehicleRequest driver event request
   */
  private void driverSuspendPhone(
      Driver driverEvent, DriverEventVehicleRequest driverEventVehicleRequest) {
    if (Boolean.TRUE.equals(driverEventVehicleRequest.getAutoAcceptFlag())) {
      if (Objects.equals(
          VehicleCommAppConstant.COMMAND_TYPE_FOUR,
          driverEvent.getDriverEventRequest().getCommandType())) {
        NotificationMessage notificationMessage =
            NotificationMessage.builder()
                .notificationType(VehicleCommAppConstant.DRIVER_SUSPEND_EVENT)
                .placeHolder(
                    generateAutoBidButtonSyncRequest(
                        driverEventVehicleRequest,
                        driverEvent.getDriverEventRequest().getCommandVariable()))
                .build();
        sendNotificationToDriverApp(driverEventVehicleRequest.getDriverId(), notificationMessage);
        NotificationMessage suspendMessage =
            NotificationMessage.builder()
                .notificationType(VehicleCommAppConstant.DRIVER_SUSPEND_EVENT)
                .placeHolder(generateSuspendMessage(driverEventVehicleRequest))
                .build();
        sendNotificationToDriverApp(driverEventVehicleRequest.getDriverId(), suspendMessage);
      }
      if (Objects.equals(
          VehicleCommAppConstant.COMMAND_TYPE_FIVE,
          driverEvent.getDriverEventRequest().getCommandType())) {
        NotificationMessage suspendMessage =
            NotificationMessage.builder()
                .notificationType(VehicleCommAppConstant.DRIVER_SUSPEND_EVENT)
                .placeHolder(
                    generateAutoBidButtonSync(
                        driverEventVehicleRequest,
                        driverEvent.getDriverEventRequest().getCommandVariable()))
                .build();
        sendNotificationToDriverApp(driverEventVehicleRequest.getDriverId(), suspendMessage);
      }
    }
    if (Boolean.TRUE.equals(driverEventVehicleRequest.getAutoBidFlag())) {
      NotificationMessage suspendMessageAutoBid =
          NotificationMessage.builder()
              .notificationType(VehicleCommAppConstant.DRIVER_SUSPEND_EVENT)
              .placeHolder(generateSuspendMessageAutoBid(driverEventVehicleRequest))
              .build();
      sendNotificationToDriverApp(driverEventVehicleRequest.getDriverId(), suspendMessageAutoBid);
    }
  }

  /**
   * process driver suspension for MDT
   *
   * @param driverEvent driver event
   * @param driverEventVehicleRequest driver event request
   */
  private void driverSuspendMdt(
      Driver driverEvent, DriverEventVehicleRequest driverEventVehicleRequest) {
    if (Objects.nonNull(driverEventVehicleRequest.getIvdNo())) {
      VehicleDetailsResponse vehicleDetailsResponse =
          getVehicleDetailsByIvdNo(driverEventVehicleRequest.getIvdNo());
      if (Boolean.TRUE.equals(driverEventVehicleRequest.getAutoAcceptFlag())) {
        RcsaMessageEventProducerData rcsaMessageEventProducerData =
            generateRcsaCommandMessage(
                driverEvent, driverEventVehicleRequest, vehicleDetailsResponse);
        sendRcsaMessage(rcsaMessageEventProducerData);

        String suspendMessage =
            configurationService.getCmsConfiguration().getAutoAcceptSuspendMessage();

        if (Objects.equals(
                driverEvent.getDriverEventRequest().getCommandType(),
                VehicleCommAppConstant.COMMAND_TYPE_FIVE)
            && Objects.nonNull(driverEventVehicleRequest.getSuspendTimeInMinutes())) {

          RcsaMessageEventProducerData rcsaSimpleMessageEventProducerData =
              generateRcsaSimpleMessage(
                  driverEventVehicleRequest, vehicleDetailsResponse, suspendMessage);
          sendRcsaMessage(rcsaSimpleMessageEventProducerData);
        }
      }
      String autoBidSuspendMessage =
          configurationService.getCmsConfiguration().getAutoBidSuspendMessage();
      if (Boolean.TRUE.equals(driverEventVehicleRequest.getAutoBidFlag())) {
        RcsaMessageEventProducerData rcsaSimpleMessageAutoBidProducer =
            generateRcsaSimpleMessageForAutobid(
                driverEventVehicleRequest, vehicleDetailsResponse, autoBidSuspendMessage);
        sendRcsaMessage(rcsaSimpleMessageAutoBidProducer);
      }
    } else {
      throw new BadRequestException(ErrorCode.INVALID_IVD_NUMBER.getCode());
    }
  }

  /**
   * @param driverEvent driver event
   * @param driverEventVehicleRequest driver event request
   * @param vehicleDetailsResponse vehicle details
   * @return RcsaMessageEventProducerData
   */
  private RcsaMessageEventProducerData generateRcsaCommandMessage(
      Driver driverEvent,
      DriverEventVehicleRequest driverEventVehicleRequest,
      VehicleDetailsResponse vehicleDetailsResponse) {
    return RcsaMessageEventProducerData.builder()
        .ivdNo(driverEventVehicleRequest.getIvdNo())
        .vehicleId(vehicleDetailsResponse.getVehicleId())
        .deviceType(driverEventVehicleRequest.getDeviceType())
        .ipAddr(vehicleDetailsResponse.getIpAddress())
        .msgId(VehicleCommAppConstant.COMMAND_MESSAGE)
        .messageType(driverEvent.getDriverEventRequest().getCommandType())
        .commandVariable(driverEvent.getDriverEventRequest().getCommandVariable())
        .build();
  }

  /**
   * generate Rcsa Simple Message
   *
   * @param driverEventVehicleRequest driver event request
   * @param vehicleDetailsResponse vehicle details response
   * @param suspendMessage message for driver suspension
   * @return RcsaMessageEventProducerData
   */
  private RcsaMessageEventProducerData generateRcsaSimpleMessage(
      DriverEventVehicleRequest driverEventVehicleRequest,
      VehicleDetailsResponse vehicleDetailsResponse,
      String suspendMessage) {
    String strDt = generateCanMessageId();
    return RcsaMessageEventProducerData.builder()
        .ivdNo(driverEventVehicleRequest.getIvdNo())
        .vehicleId(vehicleDetailsResponse.getVehicleId())
        .deviceType(driverEventVehicleRequest.getDeviceType())
        .ipAddr(vehicleDetailsResponse.getIpAddress())
        .msgId(VehicleCommAppConstant.SIMPLE_MESSAGE)
        .messageSerialNo(VehicleCommAppConstant.SIMPLE_MESSAGE_SERIAL_NUMBER)
        .requestServiceType(RequestServiceTypeEnum.NORMAL_STRUCTURE_MESSAGE)
        .msgContent(suspendMessage)
        .canMessageId(strDt + VehicleCommAppConstant.CAN_MESSAGE_VALUE_TWO)
        .messageType(String.valueOf(MessageTypeEnum.SIMPLE))
        .build();
  }

  /**
   * @param driverEventVehicleRequest driver event request
   * @param vehicleDetailsResponse vehicle details response
   * @param suspendMessage suspend message
   * @return RcsaMessageEventProducerData
   */
  private RcsaMessageEventProducerData generateRcsaSimpleMessageForAutobid(
      DriverEventVehicleRequest driverEventVehicleRequest,
      VehicleDetailsResponse vehicleDetailsResponse,
      String suspendMessage) {
    String strDt = generateCanMessageId();
    return RcsaMessageEventProducerData.builder()
        .ivdNo(driverEventVehicleRequest.getIvdNo())
        .vehicleId(vehicleDetailsResponse.getVehicleId())
        .deviceType(driverEventVehicleRequest.getDeviceType())
        .ipAddr(vehicleDetailsResponse.getIpAddress())
        .msgId(VehicleCommAppConstant.SIMPLE_MESSAGE)
        .messageSerialNo(VehicleCommAppConstant.SIMPLE_MESSAGE_SERIAL_NUMBER)
        .requestServiceType(RequestServiceTypeEnum.NORMAL_STRUCTURE_MESSAGE)
        .msgContent(suspendMessage)
        .canMessageId(strDt + VehicleCommAppConstant.CAN_MESSAGE_VALUE_ONE)
        .messageType(String.valueOf(MessageTypeEnum.SIMPLE))
        .build();
  }

  /**
   * method to generate Can message id
   *
   * @return cancel message id
   */
  private static String generateCanMessageId() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyhhmmssSSS");
    return LocalDateTime.now().format(formatter);
  }

  /**
   * method to send to Rcsa Message event
   *
   * @param rcsaMessageEventProducerData rcsa message producer data
   */
  private void sendRcsaMessage(RcsaMessageEventProducerData rcsaMessageEventProducerData) {
    producer.sendToRcsaMessageEvent(rcsaMessageEventProducerData);
  }

  /**
   * Method to Send Emergency Close Request
   *
   * @param emergencyAction emergencyAction
   * @param emergencyClose request object
   */
  @Override
  public void sendEmergencyCloseRequest(
      EmergencyAction emergencyAction, EmergencyClose emergencyClose) {
    GenericEventCommand genericEventCommand =
        convertEmergencyCloseRequestToByteArray(emergencyAction, emergencyClose);
    sendMsgToIvdResponse(
        genericEventCommand, emergencyClose.getId().toString(), emergencyClose.getIvdNo());
  }

  private IvdMessageEnum getRcsaEventTypeUsingEventID(String eventIdentifier) {
    return Arrays.stream(IvdMessageEnum.values())
        .filter(e -> e.getId().equals(eventIdentifier))
        .findFirst()
        .orElse(null);
  }

  /**
   * Converting to byte array from the pojo by checking the event.
   *
   * @param event event can be start or stop
   * @param emergencyClose emergencyClose
   */
  private GenericEventCommand convertEmergencyCloseRequestToByteArray(
      EmergencyAction event, EmergencyClose emergencyClose) {
    return beanToByteConverter.convertToByteEmergencyCloseRequest(event, emergencyClose);
  }

  private <T> void validateMessage(T request) {
    try {
      Set<ConstraintViolation<T>> violations = validator.validate(request);
      if (!violations.isEmpty()) {
        throw new ConstraintViolationException(violations);
      }
    } catch (ConstraintViolationException violationException) {
      log.error("[validateMessage] Exception during validating event ", violationException);
      throw new ConstraintException(violationException);
    }
  }

  /**
   * @param mdtActionType mdtActionType
   * @param mdtRequestCommand mdtRequestCommand
   * @return MdtResponseCommand
   */
  @Override
  public MdtResponseCommand mdtAction(
      MdtAction mdtActionType, MdtRequestCommand mdtRequestCommand, byte[] ipAddArr) {
    log.info(
        "[mdtAction] Byte string for MDT action {} - {}",
        mdtActionType,
        mdtRequestCommand.getByteString());
    MdtResponseCommand mdtResponseCommand;
    switch (mdtActionType) {
      case LOGON_REQUEST -> {
        MdtLogOnApiRequest mdtLogOnAPIRequest =
            byteToBeanConverter.convertLogOnByteToBeanConverter(
                mdtRequestCommand.getByteString(), ipAddArr);

        Coordinate coordinate =
            getOffsetCoordinate(
                mdtLogOnAPIRequest.getOffsetLatitude(), mdtLogOnAPIRequest.getOffsetLongitude());

        mdtLogOnAPIRequest.setOffsetLatitude(coordinate.getLatitude());
        mdtLogOnAPIRequest.setOffsetLongitude(coordinate.getLongitude());
        int ivdNo = mdtLogOnAPIRequest.getMobileId();
        log.info("[mdtAction] MDTLogOnAPIRequest {}", mdtLogOnAPIRequest);
        // Check for Acknowledgement Flag in order to send ivdResponse
        acknowledgmentCheck(
            mdtLogOnAPIRequest.getMessageId(),
            mdtLogOnAPIRequest.getMobileId(),
            mdtLogOnAPIRequest.getIsAckRequired(),
            mdtLogOnAPIRequest.getSerialNo(),
            mdtLogOnAPIRequest.getIpAddress(),
            mdtLogOnAPIRequest.getLogonTimeStamp());

        Optional<MdtApiResponse> mdtLogOnApiResponse =
            mdtAPIService.mdtLogOnAction(mdtLogOnAPIRequest);
        if (mdtLogOnApiResponse.isEmpty()) {
          throw new BadRequestException(ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode());
        }

        mdtResponseCommand =
            beanToByteConverter.convertMDTResponseToBytes(
                mdtLogOnApiResponse.get(), mdtActionType, ipAddArr, ivdNo);
      }

      case LOGOUT_REQUEST -> {
        MdtLogOffApiRequest mdtLogOffAPIRequest =
            byteToBeanConverter.convertLogOffByteToBeanConverter(
                mdtRequestCommand.getByteString(), ipAddArr);

        Coordinate coordinate =
            getOffsetCoordinate(
                mdtLogOffAPIRequest.getOffsetLatitude(), mdtLogOffAPIRequest.getOffsetLongitude());
        mdtLogOffAPIRequest.setOffsetLatitude(coordinate.getLatitude());
        mdtLogOffAPIRequest.setOffsetLongitude(coordinate.getLongitude());

        // Check for Acknowledgement Flag in order to send ivdResponse
        acknowledgmentCheck(
            mdtLogOffAPIRequest.getMessageId(),
            mdtLogOffAPIRequest.getIvdNo(),
            mdtLogOffAPIRequest.getIsAckRequired(),
            mdtLogOffAPIRequest.getSerialNo(),
            mdtLogOffAPIRequest.getIpAddress(),
            mdtLogOffAPIRequest.getTimeStamp());

        Optional<MdtApiResponse> mdtLogOffApiResponse =
            mdtAPIService.mdtLogOffAction(mdtLogOffAPIRequest);
        if (mdtLogOffApiResponse.isEmpty()) {
          throw new BadRequestException(ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode());
        }
        int ivdNo = mdtLogOffAPIRequest.getIvdNo();
        mdtLogOffApiResponse.get().setDriverId(mdtLogOffAPIRequest.getDriverId());
        mdtResponseCommand =
            beanToByteConverter.convertMDTResponseToBytes(
                mdtLogOffApiResponse.get(), mdtActionType, ipAddArr, ivdNo);
      }
      case IVD_HARDWARE_INFO -> {
        MdtIvdDeviceConfigApiRequest ivdDeviceConfigAPiRequest =
            byteToBeanConverter.convertIVDDeviceConfigByteToBeanConverter(
                mdtRequestCommand.getByteString(), ipAddArr);
        log.info("[mdtAction] MDTIvdDeviceConfigAPiRequest: {}", ivdDeviceConfigAPiRequest);

        // Check for Acknowledgement Flag in order to send ivdResponse
        acknowledgmentCheck(
            ivdDeviceConfigAPiRequest.getMessageId(),
            ivdDeviceConfigAPiRequest.getIvdNo(),
            ivdDeviceConfigAPiRequest.getIsAckRequired(),
            ivdDeviceConfigAPiRequest.getSerialNo(),
            ivdDeviceConfigAPiRequest.getIpAddress(),
            ivdDeviceConfigAPiRequest.getTimeStamp());

        Optional<MdtApiResponse> mdtIVDDeviceConfigAPIResponse =
            mdtAPIService.mdtIVDDeviceConfigAction(
                ivdDeviceConfigAPiRequest, ivdDeviceConfigAPiRequest.getIvdNo());

        int ivdNo = ivdDeviceConfigAPiRequest.getIvdNo();
        if (mdtIVDDeviceConfigAPIResponse.isPresent()) {
          mdtResponseCommand =
              beanToByteConverter.convertMDTResponseToBytes(
                  mdtIVDDeviceConfigAPIResponse.get(), mdtActionType, ipAddArr, ivdNo);
        } else {
          mdtResponseCommand = new MdtResponseCommand();
          mdtResponseCommand.setByteString(null);
          mdtResponseCommand.setEventDate(getInstantDate());
        }
      }

      case POWER_UP -> {
        MdtPowerUpApiRequest powerUpAPiRequest =
            byteToBeanConverter.convertPowerUpByteToBeanConverter(
                mdtRequestCommand.getByteString(), ipAddArr);

        log.info("[mdtAction] MDTPowerUpAPIRequest: {}", powerUpAPiRequest);

        // Check for Acknowledgement Flag in order to send ivdResponse
        acknowledgmentCheck(
            powerUpAPiRequest.getMessageId(),
            powerUpAPiRequest.getIvdNo(),
            powerUpAPiRequest.getIsAckRequired(),
            powerUpAPiRequest.getSerialNo(),
            powerUpAPiRequest.getIpAddr(),
            powerUpAPiRequest.getTimeStamp());

        Optional<MdtApiResponse> mdtPowerUpAPIResponseOptional =
            mdtAPIService.mdtPowerUpAction(powerUpAPiRequest);
        if (mdtPowerUpAPIResponseOptional.isEmpty()) {
          throw new BadRequestException(ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode());
        }
        MdtApiResponse mdtPowerUpAPIResponse = mdtPowerUpAPIResponseOptional.get();
        int ivdNo = mdtPowerUpAPIResponse.getIvdNo();
        mdtPowerUpAPIResponse.setImsi(powerUpAPiRequest.getImsi());
        mdtPowerUpAPIResponse.setVehiclePlateNum(powerUpAPiRequest.getVehiclePlateNum());

        /* The reason code helps determine whether the MDT power-up API call was successful.
        If the API responds successfully, the reason code will be empty.
        If the API throws an exception, the reason code will contain a value.
        We will only proceed to call the vehicle service to update the vehicle state if the MDT power-up API call is successful. */
        if (StringUtils.isBlank(mdtPowerUpAPIResponse.getReasonCode())) {
          UpdateVehicleStateRequest updateVehicleStateRequest =
              validateOffsetMultiplier(powerUpAPiRequest, mdtPowerUpAPIResponse);

          vehicleAPIService.updateVehicleStateForPowerUp(
              powerUpAPiRequest.getVehiclePlateNum().replaceAll("\\s+", ""),
              VehicleEvent.POWER_UP,
              updateVehicleStateRequest);
        }
        mdtResponseCommand =
            beanToByteConverter.convertMDTResponseToBytes(
                mdtPowerUpAPIResponse, mdtActionType, ipAddArr, ivdNo);
      }

      default -> throw new BadRequestException(ErrorCode.INVALID_MDT_ACTION.getCode());
    }
    return mdtResponseCommand;
  }

  /**
   * This method is used to validate offsetMultiplier and frames the update vehicle state request.
   *
   * @param powerUpAPiRequest powerUpAPiRequest
   * @param mdtPowerUpAPIResponse mdtPowerUpAPIResponse
   * @return UpdateVehicleStateRequest
   */
  private UpdateVehicleStateRequest validateOffsetMultiplier(
      MdtPowerUpApiRequest powerUpAPiRequest, MdtApiResponse mdtPowerUpAPIResponse) {
    Coordinate offsetCoordinateValue =
        getOffsetCoordinate(
            powerUpAPiRequest.getOffsetLatitude(), powerUpAPiRequest.getOffsetLongitude());
    return UpdateVehicleStateRequest.builder()
        .deviceType(DeviceType.MDT)
        .eventTime(mdtPowerUpAPIResponse.getPowerUpDt())
        .latitude(offsetCoordinateValue.getLatitude())
        .longitude(offsetCoordinateValue.getLongitude())
        .speed(powerUpAPiRequest.getSpeed())
        .heading(
            null != powerUpAPiRequest.getHeading()
                ? (int)
                    (powerUpAPiRequest.getHeading()
                        * VehicleCommDomainConstant.DIRECTION_MULTIPLIER)
                : 0)
        .build();
  }

  /**
   * Method to adjust offset value
   *
   * @param coordinate coordinate value
   * @param origin origin value
   * @param offsetMultiplier offset
   * @return Double
   */
  private Double adjustOffsetWithOriginAndMultiplier(
      Double coordinate, double origin, long offsetMultiplier) {
    if (coordinate == null || offsetMultiplier == 0) {
      return null;
    }
    return (coordinate / offsetMultiplier + origin);
  }

  public String getInstantDate() {
    ZonedDateTime instant = ZonedDateTime.now(ZoneId.of("UTC"));
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    return formatter.format(instant);
  }

  /**
   * @param offsetLatitude offsetLatitude
   * @param offsetLongitude offsetLongitude
   * @return Coordinate
   */
  private Coordinate getOffsetCoordinate(Double offsetLatitude, Double offsetLongitude) {
    CmsConfiguration cmsConfiguration = configurationService.getCmsConfiguration();
    Double latitudeOrigin = cmsConfiguration.getLatitudeOrigin();
    Double longitudeOrigin = cmsConfiguration.getLongitudeOrigin();
    Long offsetMultiplier = cmsConfiguration.getOffsetMultiplier();

    if (offsetMultiplier == null || offsetMultiplier == VehicleCommDomainConstant.OFFSET_VALUE) {
      log.error(
          "[getOffsetCoordinate] Exception occurred while validating offsetMultiplier {}",
          offsetMultiplier);
      throw new BadRequestException(ErrorCode.INVALID_OFFSET_MULTIPLIER.getCode());
    }

    Double latitude =
        adjustOffsetWithOriginAndMultiplier(offsetLongitude, latitudeOrigin, offsetMultiplier);
    Double longitude =
        adjustOffsetWithOriginAndMultiplier(offsetLatitude, longitudeOrigin, offsetMultiplier);

    return Coordinate.builder().latitude(latitude).longitude(longitude).build();
  }

  /**
   * This method is to produce IVDResponse topic based on the Ack Flag.
   *
   * @param messageId messageId
   * @param mobileId mobileId
   * @param isAckRequired isAckRequired
   * @param serialNo serialNo
   * @param ipAddress ipAddress
   * @param timeStamp timeStamp
   */
  private void acknowledgmentCheck(
      String messageId,
      Integer mobileId,
      Boolean isAckRequired,
      String serialNo,
      String ipAddress,
      LocalDateTime timeStamp) {
    boolean acknowledgementFlag =
        List.of(configurationService.getCmsConfiguration().getStoreForwardEvents().split(","))
            .contains(messageId);
    if (Boolean.TRUE.equals(isAckRequired) || Boolean.TRUE.equals(acknowledgementFlag)) {
      IvdResponseData ivdResponse =
          IvdResponseData.builder()
              .eventIdentifier(IvdMessageEnum.MESSAGE_AKNOWLEDGE.getId())
              .message(
                  beanToByteConverter.sendMessageAcknowledgement(
                      mobileId, serialNo, messageId, ipAddress))
              .ivdNo(mobileId)
              .eventDate(timeStamp)
              .build();
      producer.sendToIVDResponseEvent(ivdResponse);
    }
  }
}
