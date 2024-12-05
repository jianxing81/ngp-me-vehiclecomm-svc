package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.jobevent;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobDispatchPostEventsRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RedundantMessageKeyData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RedundantMessageRequestHolder;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VehicleDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.AbstractJobEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.EsbJobMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbJob;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdMessageEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.JobEventLogEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.OriginatorEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.ByteDataRepresentation;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.service.VehicleCommDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * For VehicleComm requirement "4.2.1.15.11 Advance Job Remind Event Use Case Design Document"
 *
 * @see <a
 *     href="https://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1260751453/4.2.1.15.11+Advance+Job+Remind+Event+Use+Case+Design+Document">4.2.1.15.11
 *     Advance Job Remind Event Use Case Design Document</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdvanceJobRemindEvent extends AbstractJobEvent {

  /** Not acknowledged reminder status value {@value} */
  private static final Integer REMINDER_NOT_ACKNOWLEDGED_VALUE = 0;

  /** Not acknowledged reminder status eventMessage {@value} */
  private static final String ADVJOB_REMINDER_NOT_ACKNOWLEDGED =
      "AdvJob Reminder Not Acknowledged,vehicle id=%s";

  /** Acknowledged reminder status eventMessage {@value} */
  private static final String ADVJOB_REMINDER_ACKNOWLEDGED =
      "AdvJob Reminder Acknowledged,vehicle id=%s";

  private final VehicleCommCacheService vehicleCommCacheService;
  private final EsbJobMapper esbJobMapper;
  private final VehicleCommDomainService vehicleCommDomainService;

  @Override
  public void handleJobEvent(EsbJob esbJob, String ivdJobEventTopic) {

    // Convert byte data to a pojo
    ByteDataRepresentation byteDataRepresentation =
        byteToBeanConverter.advanceJobRemindEventConverter(
            esbJob.getIvdMessageRequest().getMessage());

    // Map the pojo to the aggregate root
    esbJobMapper.byteDataRepresentationToIvdInboundEvent(esbJob, byteDataRepresentation);

    // Validate the pojo
    vehicleCommDomainService.validateByteData(esbJob);

    // Perform logical conversions on the fields of the pojo
    vehicleCommDomainService.parseGeoLocations(esbJob);

    // Check if acknowledgement required
    boolean isAckRequired = vehicleCommDomainService.isAckRequired(esbJob);

    // Send acknowledgement back to MDT if ack required
    if (isAckRequired) {
      sendAcknowledgementToMdt(esbJob);
    }

    Integer messageId = esbJob.getByteData().getMessageId();
    String uniqueKey = esbJob.getByteData().getJobNo();

    // Add cacheKey details to access cache to discard duplicate message
    RedundantMessageKeyData msgHolder =
        new RedundantMessageKeyData(
            esbJob.getIvdMessageRequest().getEventId(), messageId, uniqueKey);

    RedundantMessageRequestHolder.set(msgHolder);

    // Check store and forward cache if message already processed
    var messageAlreadyProcessed =
        vehicleCommCacheService.isKeyPresentInStoreForwardCache(
            esbJob.getByteData().getMessageId(),
            esbJob.getByteData().getIvdNo(),
            esbJob.getByteData().getSerialNumber());

    // End the process if message is already processed
    if (processedOrRedundantMessage(esbJob, messageAlreadyProcessed)) return;

    // Get vehicle ID by IVD number from MDT service
    VehicleDetailsResponse vehicleDetails =
        getVehicleDetailsByIvdNo(esbJob.getByteData().getIvdNo());

    // Map MDT service response to the aggregate root
    esbJobMapper.vehicleDetailsResponseToIvdInboundEvent(esbJob, vehicleDetails);

    // Validate the MDT service response
    vehicleCommDomainService.validateVehicleDetails(esbJob);

    // Call Job Dispatch service to process the event
    callMdtServiceToAdvanceJobRemindEvent(esbJob);

    // Add entry to redundantMessage cache
    vehicleCommCacheService.isRedundantMessage(messageId, uniqueKey);
  }

  /**
   * Call Job Dispatch service to process the event
   *
   * @param esbJob esbJob
   */
  private void callMdtServiceToAdvanceJobRemindEvent(EsbJob esbJob) {
    boolean isNotAcknowledgedValue =
        REMINDER_NOT_ACKNOWLEDGED_VALUE.equals(esbJob.getByteData().getAcknowledgement());
    JobDispatchPostEventsRequest jobDispatchPostEventsRequest =
        JobDispatchPostEventsRequest.builder()
            .jobNo(esbJob.getByteData().getJobNo())
            .vehicleId(esbJob.getVehicleDetails().getId())
            .driverId(esbJob.getDriverId())
            .event(
                isNotAcknowledgedValue
                    ? JobEventLogEvent.EVT_AJ_REMINDER_NOT_ACK
                    : JobEventLogEvent.EVT_AJ_REMINDER_ACK)
            .eventMessage(
                isNotAcknowledgedValue
                    ? String.format(
                        ADVJOB_REMINDER_NOT_ACKNOWLEDGED, esbJob.getVehicleDetails().getId())
                    : String.format(
                        ADVJOB_REMINDER_ACKNOWLEDGED, esbJob.getVehicleDetails().getId()))
            .eventTime(esbJob.getIvdMessageRequest().getEventDate())
            .originator(OriginatorEnum.MDT)
            .build();
    jobDispatchAPIService.postJobEvents(jobDispatchPostEventsRequest);
  }

  @Override
  public IvdMessageEnum jobEventType() {
    return IvdMessageEnum.ADVANCED_JOB_REMIND;
  }
}
