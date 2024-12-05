package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.UpdateMdtRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.UpdateVehicleStateRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VehicleDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.internal.BeanToByteConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.internal.ByteToBeanConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.messaging.VehicleCommChannelProducer;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.MdtAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.VehicleAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbVehicle;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdMessageEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdResponseData;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/** This abstract class provides some common methods for all strategies of VehicleEventStrategy */
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractVehicleEvent
    implements VehicleEventStrategy, ApplicationContextAware {

  protected VehicleCommChannelProducer kafkaProducer;
  protected MdtAPIService mdtAPIService;
  protected VehicleAPIService vehicleAPIService;
  protected BeanToByteConverter beanToByteConverter;
  protected ByteToBeanConverter byteToBeanConverter;

  protected void sendAcknowledgementToMdt(EsbVehicle esbVehicle) {
    IvdResponseData ivdResponse =
        IvdResponseData.builder()
            .eventIdentifier(IvdMessageEnum.MESSAGE_AKNOWLEDGE.getId())
            .message(generateAcknowledgeResponse(esbVehicle))
            .ivdNo(esbVehicle.getByteData().getIvdNo())
            .eventDate(esbVehicle.getIvdVehicleEventMessageRequest().getEventDate())
            .build();
    kafkaProducer.sendToIVDResponseEvent(ivdResponse);
  }

  protected void sendMsgToIvdResponse(EsbVehicle esbVehicle) {
    IvdResponseData ivdResponse =
        IvdResponseData.builder()
            .eventIdentifier(esbVehicle.getIvdVehicleEventMessageRequest().getEventIdentifier())
            .message(esbVehicle.getByteArrayData().getByteArrayMessage())
            .ivdNo(esbVehicle.getByteData().getIvdNo())
            .eventDate(LocalDateTime.now())
            .build();
    kafkaProducer.sendToIVDResponseEvent(ivdResponse);
  }

  private String generateAcknowledgeResponse(EsbVehicle esbVehicle) {

    Integer ivdNo = esbVehicle.getByteData().getIvdNo();
    String serialNo = String.valueOf(esbVehicle.getByteData().getSerialNumber());
    String msgId = esbVehicle.getIvdVehicleEventMessageRequest().getEventIdentifier();
    String ipAddress =
        byteToBeanConverter.extractMdtIpAddress(
            esbVehicle.getIvdVehicleEventMessageRequest().getMessage());
    return beanToByteConverter.sendMessageAcknowledgement(ivdNo, serialNo, msgId, ipAddress);
  }

  /**
   * Retrieves vehicle details by IVD number from MDT service.
   *
   * @param ivdNo The IVD number.
   * @param isRegularReport regularReport
   * @return The {@link VehicleDetailsResponse} if available, else {@code null}.
   */
  protected VehicleDetailsResponse getVehicleDetailsByIvdNo(
      Integer ivdNo, boolean isRegularReport) {
    Optional<VehicleDetailsResponse> vehicleDetailsResponse =
        mdtAPIService.vehicleDetails(ivdNo, isRegularReport);
    return vehicleDetailsResponse.orElse(null);
  }

  /**
   * Call Vehicle service to update the vehicle-state
   *
   * @param esbVehicle The {@link EsbVehicle} representing the esbVehicle event.
   */
  protected void callVehicleSvcToUpdateVehicleState(EsbVehicle esbVehicle) {
    UpdateVehicleStateRequest updateVehicleStateRequest =
        UpdateVehicleStateRequest.builder()
            .jobNo(esbVehicle.getByteData().getJobNo())
            .driverId(esbVehicle.getByteData().getDriverId())
            .deviceType(esbVehicle.getDeviceType())
            .eventTime(esbVehicle.getIvdVehicleEventMessageRequest().getEventDate())
            .latitude(esbVehicle.getVehicleLatitude().getValue())
            .longitude(esbVehicle.getVehicleLongitude().getValue())
            .speed(esbVehicle.getByteData().getSpeed())
            .heading(esbVehicle.getHeading())
            .build();
    if (ObjectUtils.isNotEmpty(esbVehicle.getByteData().getMessageId())
        && esbVehicle
            .getByteData()
            .getMessageId()
            .equals(VehicleCommAppConstant.REGULAR_REPORT_EVENT_ID)) {

      vehicleAPIService.updateIVDVehicleState(
          esbVehicle.getVehicleDetails().getId(),
          esbVehicle.getByteData().getIvdEventStatus(),
          updateVehicleStateRequest);
    } else {
      vehicleAPIService.updateVehicleState(
          esbVehicle.getVehicleDetails().getId(),
          esbVehicle.getVehicleEventType(),
          updateVehicleStateRequest);
    }
  }

  /**
   * Method to update vehicle mdt
   *
   * @param esbVehicle esbVehicle
   * @param zoneDate zoneDate
   * @param shiftDestination shiftDestination
   * @param destinationCode destinationCode
   */
  protected void callVehicleSvcToUpdateVehicleMdt(
      EsbVehicle esbVehicle,
      LocalDateTime zoneDate,
      String shiftDestination,
      String destinationCode) {

    UpdateMdtRequest mdtRequest =
        UpdateMdtRequest.builder()
            .eventType(esbVehicle.getVehicleMdtEventType().getValue())
            .zoneDate(zoneDate)
            .shiftDestination(shiftDestination)
            .destinationCode(destinationCode)
            .build();
    vehicleAPIService.updateVehicleMdt(esbVehicle.getVehicleDetails().getId(), mdtRequest);
  }

  protected boolean processedOrRedundantMessage(
      EsbVehicle esbVehicle, boolean messageAlreadyProcessed) {
    if (messageAlreadyProcessed) {
      log.info("Message ID {} is already processed", esbVehicle.getByteData().getMessageId());
      return true;
    }
    return false;
  }

  @Override
  public void setApplicationContext(final ApplicationContext applicationContext)
      throws BeansException {
    kafkaProducer = applicationContext.getBean(VehicleCommChannelProducer.class);
    mdtAPIService = applicationContext.getBean(MdtAPIService.class);
    vehicleAPIService = applicationContext.getBean(VehicleAPIService.class);
    beanToByteConverter = applicationContext.getBean(BeanToByteConverter.class);
    byteToBeanConverter = applicationContext.getBean(ByteToBeanConverter.class);
  }
}
