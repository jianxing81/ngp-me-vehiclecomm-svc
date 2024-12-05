package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.jobevent;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RedundantMessageKeyData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RedundantMessageRequestHolder;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.AbstractJobEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.EsbJobMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbJob;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.DriverAction;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdMessageEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.service.VehicleCommDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageAcknowledge extends AbstractJobEvent {

  private final EsbJobMapper esbJobMapper;
  private final VehicleCommDomainService vehicleCommDomainService;
  private final VehicleCommCacheService vehicleCommCacheService;

  @Override
  public void handleJobEvent(EsbJob esbJob, String ivdJobEventTopic) {

    // Convert byte data to a pojo
    var byteDataRepresentation =
        byteToBeanConverter.messageAcknowledgeConverter(esbJob.getIvdMessageRequest().getMessage());

    // Map the pojo to the aggregate root
    esbJobMapper.byteDataRepresentationToIvdInboundEvent(esbJob, byteDataRepresentation);

    // Validate the pojo
    vehicleCommDomainService.validateByteData(esbJob);

    // Perform logical conversions on the fields of the pojo
    vehicleCommDomainService.parseGeoLocations(esbJob);
    vehicleCommDomainService.parseMoneyValues(esbJob);

    // Get vehicle ID by IVD number from MDT service
    var vehicleDetails = getVehicleDetailsByIvdNo(esbJob.getByteData().getIvdNo());

    // Map MDT service response to the aggregate root
    esbJobMapper.vehicleDetailsResponseToIvdInboundEvent(esbJob, vehicleDetails);

    // Validate the MDT service response
    vehicleCommDomainService.validateVehicleDetails(esbJob);

    boolean validateCacheRequiredFields =
        vehicleCommDomainService.validateCacheRequiredFields(esbJob);

    if (validateCacheRequiredFields) {
      log.info(
          "storeAndForward cache key for message acknowledgement is {} ",
          esbJob.getByteData().getReceivedMessageId()
              + esbJob.getByteData().getIvdNo()
              + esbJob.getByteData().getReceivedMessageSn());
      // Retrieve jobNumber from cache
      var jobNumber =
          vehicleCommCacheService.getJobNumberFromCache(
              esbJob.getByteData().getReceivedMessageId(),
              esbJob.getByteData().getIvdNo(),
              esbJob.getByteData().getReceivedMessageSn());

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
      // Map job number to aggregate root
      esbJobMapper.jobNumberToIvdInboundEvent(esbJob, jobNumber);

      // Add entry to redundantMessage cache
      vehicleCommCacheService.isRedundantMessage(messageId, uniqueCacheIdentifier);
    }

    boolean isJobNumberAvailable = null != esbJob.getByteData().getJobNo();
    log.info("isJobNumberAvailable in bytearray: {}", isJobNumberAvailable);
    if (isJobNumberAvailable) {
      // Remove job number from cache
      vehicleCommCacheService.removeJobNumberFromCache(
          esbJob.getByteData().getReceivedMessageId(),
          esbJob.getByteData().getIvdNo(),
          esbJob.getByteData().getReceivedMessageSn());
      // Call Job Dispatch service to process the event
      callJobDispatchSvc(esbJob, DriverAction.JOB_SYNC_ACK);
    }
  }

  @Override
  public IvdMessageEnum jobEventType() {
    return IvdMessageEnum.MESSAGE_ACKNOWLEDGE;
  }
}
