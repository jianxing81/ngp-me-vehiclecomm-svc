package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobDispatchDetailsRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.UpdateVehicleStateRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VehicleDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.internal.BeanToByteConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.internal.ByteToBeanConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.messaging.VehicleCommChannelProducer;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.JobDispatchAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.MdtAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.VehicleAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbJob;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DeviceType;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DriverAction;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdMessageEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.VehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdResponseData;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/** This abstract class provides some common methods for all strategies of JobEventStrategy */
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractJobEvent implements JobEventStrategy, ApplicationContextAware {

  protected MdtAPIService mdtAPIService;

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

  /**
   * Send acknowledgement to MDT
   *
   * @param esbJob The {@link EsbJob} representing the job event.
   */
  protected void sendAcknowledgementToMdt(EsbJob esbJob) {
    IvdResponseData ivdResponse =
        IvdResponseData.builder()
            .eventIdentifier(IvdMessageEnum.MESSAGE_AKNOWLEDGE.getId())
            .message(generateAcknowledgeResponse(esbJob))
            .ivdNo(esbJob.getByteData().getIvdNo())
            .eventDate(LocalDateTime.now())
            .build();
    kafkaProducer.sendToIVDResponseEvent(ivdResponse);
  }

  private String generateAcknowledgeResponse(EsbJob esbJob) {

    Integer ivdNo = esbJob.getByteData().getIvdNo();
    String serialNo = String.valueOf(esbJob.getByteData().getSerialNumber());
    String msgId = esbJob.getIvdMessageRequest().getEventIdentifier();
    String ipAddress =
        byteToBeanConverter.extractMdtIpAddress(esbJob.getIvdMessageRequest().getMessage());
    return beanToByteConverter.sendMessageAcknowledgement(ivdNo, serialNo, msgId, ipAddress);
  }

  /**
   * Publish business message to ivd response topic
   *
   * @param esbJob The {@link EsbJob} representing the job event.
   */
  protected void sendAcknowledgementToIvdResponse(EsbJob esbJob) {
    IvdResponseData ivdResponse =
        IvdResponseData.builder()
            .eventIdentifier(esbJob.getResponseMessageId().toString())
            .message(esbJob.getByteArrayData().getByteArrayMessage())
            .ivdNo(esbJob.getByteData().getIvdNo())
            .eventDate(LocalDateTime.now())
            .build();
    kafkaProducer.sendToIVDResponseEvent(ivdResponse);
  }

  /**
   * Calls the Job Dispatch service to process the event.
   *
   * @param esbJob The {@link EsbJob} representing the job event.
   * @param driverAction The {@link DriverAction}
   */
  protected void callJobDispatchSvc(EsbJob esbJob, DriverAction driverAction) {
    JobDispatchDetailsRequest jobDispatchDetailsRequest =
        JobDispatchDetailsRequest.builder()
            .vehicleId(esbJob.getVehicleDetails().getId())
            .latitude(esbJob.getVehicleLatitude().getValue())
            .longitude(esbJob.getVehicleLongitude().getValue())
            .driverId(esbJob.getDriverId())
            .eta(esbJob.getByteData().getEta())
            .driverAction(driverAction)
            .eventTime(esbJob.getIvdMessageRequest().getEventDate())
            .deviceType(DeviceType.MDT)
            .build();
    jobDispatchAPIService.getDriverResponse(
        esbJob.getByteData().getJobNo(), jobDispatchDetailsRequest);
  }

  /**
   * Call Vehicle service to update the vehicle-state
   *
   * @param esbJob The {@link EsbJob} representing the job event.
   * @param event The {@link VehicleEvent}
   */
  protected void callVehicleSvcToUpdateVehicleState(EsbJob esbJob, VehicleEvent event) {
    UpdateVehicleStateRequest updateVehicleStateRequest =
        UpdateVehicleStateRequest.builder()
            .jobNo(esbJob.getByteData().getJobNo())
            .driverId(esbJob.getDriverId())
            .deviceType(DeviceType.MDT)
            .eventTime(esbJob.getIvdMessageRequest().getEventDate())
            .latitude(esbJob.getVehicleLatitude().getValue())
            .longitude(esbJob.getVehicleLongitude().getValue())
            .speed(esbJob.getByteData().getSpeed())
            .heading(esbJob.getHeading())
            .noShowEventType(esbJob.getByteData().getNoShowEventType())
            .build();
    vehicleAPIService.updateVehicleState(
        esbJob.getVehicleDetails().getId(), event, updateVehicleStateRequest);
  }

  /**
   * Method to check if it is a duplicate requests
   *
   * @param esbJob - esbJob
   * @param message - message
   * @return boolean
   */
  protected boolean processedOrRedundantMessage(EsbJob esbJob, boolean message) {
    if (message) {
      log.info(
          VehicleCommAppConstant.LOG_MESSAGE_ID_ALREADY_PROCESS,
          esbJob.getByteData().getMessageId());
      return true;
    }
    return false;
  }

  @Override
  public void setApplicationContext(final ApplicationContext applicationContext)
      throws BeansException {
    mdtAPIService = applicationContext.getBean(MdtAPIService.class);
    kafkaProducer = applicationContext.getBean(VehicleCommChannelProducer.class);
    jobDispatchAPIService = applicationContext.getBean(JobDispatchAPIService.class);
    vehicleAPIService = applicationContext.getBean(VehicleAPIService.class);
    beanToByteConverter = applicationContext.getBean(BeanToByteConverter.class);
    byteToBeanConverter = applicationContext.getBean(ByteToBeanConverter.class);
  }
}
