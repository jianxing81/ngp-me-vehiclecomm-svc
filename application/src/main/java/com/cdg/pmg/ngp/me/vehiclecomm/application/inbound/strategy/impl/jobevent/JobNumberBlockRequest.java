package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.jobevent;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobNoBlockRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobNoBlockResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RedundantMessageKeyData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RedundantMessageRequestHolder;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.AbstractJobEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.EsbJobMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbJob;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdMessageEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.GenericEventCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdResponseData;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.service.VehicleCommDomainService;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobNumberBlockRequest extends AbstractJobEvent {

  private final EsbJobMapper esbJobMapper;
  private final VehicleCommDomainService vehicleCommDomainService;
  private final VehicleCommCacheService vehicleCommCacheService;

  @Override
  public void handleJobEvent(EsbJob esbJob, String ivdJobEventTopic) {

    // Convert byte data to a pojo
    var byteDataRepresentation =
        byteToBeanConverter.jobNumberBlockRequestEventConverter(
            esbJob.getIvdMessageRequest().getMessage());

    // Map the pojo to the aggregate root
    esbJobMapper.byteDataRepresentationToIvdInboundEvent(esbJob, byteDataRepresentation);

    // Validate the pojo
    vehicleCommDomainService.validateByteData(esbJob);

    // Validate ivd number
    vehicleCommDomainService.validateIvdNo(esbJob);

    // Check if acknowledgement required
    var ackRequired = vehicleCommDomainService.isAckRequired(esbJob);

    // Send acknowledgement back to MDT if ack required
    if (ackRequired) {
      sendAcknowledgementToMdt(esbJob);
    }

    String uniqueCacheIdentifier =
        StringUtils.isNotBlank(esbJob.getByteData().getJobNo())
            ? esbJob.getByteData().getJobNo()
            : String.valueOf(esbJob.getByteData().getIvdNo());

    Integer messageId = esbJob.getByteData().getMessageId();

    // Add cacheKey details to access cache to discard duplicate message
    RedundantMessageKeyData msgHolder =
        new RedundantMessageKeyData(
            esbJob.getIvdMessageRequest().getEventId(), messageId, uniqueCacheIdentifier);

    RedundantMessageRequestHolder.set(msgHolder);

    // Check store and forward cache if message already processed
    var messageAlreadyProcessed =
        vehicleCommCacheService.isKeyPresentInStoreForwardCache(
            esbJob.getByteData().getMessageId(),
            esbJob.getByteData().getIvdNo(),
            esbJob.getByteData().getSerialNumber());

    // End the process if message is already processed
    if (processedOrRedundantMessage(esbJob, messageAlreadyProcessed)) return;

    // Perform logical conversions on  coordinate fields of the pojo
    vehicleCommDomainService.parseGeoLocations(esbJob);

    // Get vehicle ID by IVD number from MDT service
    var vehicleDetails = getVehicleDetailsByIvdNo(esbJob.getByteData().getIvdNo());

    // Map MDT service response to the aggregate root
    esbJobMapper.vehicleDetailsResponseToIvdInboundEvent(esbJob, vehicleDetails);

    // Validate the MDT service response
    vehicleCommDomainService.validateVehicleDetails(esbJob);

    // call MDT service to get the job number block
    callMdtSvcToGetJobNumberBlock(esbJob);

    // Add entry to redundantMessage cache
    vehicleCommCacheService.isRedundantMessage(messageId, uniqueCacheIdentifier);
  }

  private void callMdtSvcToGetJobNumberBlock(EsbJob esbJob) {
    JobNoBlockRequest jobNoBlockRequest =
        JobNoBlockRequest.builder()
            .vehicleId(esbJob.getVehicleDetails().getId())
            .jobNoBlockStart(VehicleCommAppConstant.JOB_NUMBER_BLOCK_REQUEST_ZERO_VALUE)
            .jobNoBlockEnd(VehicleCommAppConstant.JOB_NUMBER_BLOCK_REQUEST_ZERO_VALUE)
            .build();
    Optional<JobNoBlockResponse> jobNoBlockResponse =
        mdtAPIService.getJobNoBlock(jobNoBlockRequest);
    jobNoBlockResponse.ifPresent(
        noBlockResponse -> {
          // Map response message id to the aggregate root
          vehicleCommDomainService.setResponseMessageId(
              esbJob,
              Integer.parseInt(IvdMessageEnum.JOB_NUMBER_BLOCK_RESPONSE_MESSAGE_ID.getId()));

          GenericEventCommand genericEventCommand =
              beanToByteConverter.convertToJobNumberBlock(esbJob, noBlockResponse);

          // Map genericEventCommand to the aggregate root
          esbJobMapper.genericEventCommandToByteArrayData(esbJob, genericEventCommand);
          sendAcknowledgementMessageToIvdResponse(esbJob);
        });
  }

  /**
   * Publish business message to ivd response topic
   *
   * @param esbJob The {@link EsbJob} representing the job event.
   */
  protected void sendAcknowledgementMessageToIvdResponse(EsbJob esbJob) {
    IvdResponseData ivdResponse =
        IvdResponseData.builder()
            .eventIdentifier(esbJob.getResponseMessageId().toString())
            .message(esbJob.getByteArrayData().getByteArrayMessage())
            .ivdNo(esbJob.getByteData().getIvdNo())
            .eventDate(LocalDateTime.now())
            .build();
    kafkaProducer.sendToIVDResponseEvent(ivdResponse);
  }

  @Override
  public IvdMessageEnum jobEventType() {
    return IvdMessageEnum.JOB_NUMBER_BLOCK_REQUEST;
  }
}
