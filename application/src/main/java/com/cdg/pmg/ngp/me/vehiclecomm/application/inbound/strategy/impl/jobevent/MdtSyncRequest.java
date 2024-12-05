package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.jobevent;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobOffersResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.JobOffersResponseData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VehicleDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.AbstractJobEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.EsbJobMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbJob;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdMessageEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.ByteDataRepresentation;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.service.VehicleCommDomainService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 4.2.1.15.15 MDT Sync Request Event
 *
 * @see <a
 *     href="https://comfortdelgrotaxi.atlassian.net/wiki/spaces/NGP/pages/1261537747/4.2.1.15.15+MDT+Sync+Request+Event+Use+Case+Design+Document">4.2.1.15.15
 *     MDT Sync Request Event</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MdtSyncRequest extends AbstractJobEvent {

  private final EsbJobMapper esbJobMapper;
  private final VehicleCommDomainService vehicleCommDomainService;
  private final VehicleCommCacheService vehicleCommCacheService;

  /**
   * 1.Consume MDT Sync Request event 2.Send acknowledgement to MDT 3.Call JobDispatchService 4.Send
   * Job Offer message
   *
   * @param esbJob The {@link EsbJob} representing the job event.
   * @param ivdJobEventTopic The kafka topic.
   */
  @Override
  public void handleJobEvent(EsbJob esbJob, String ivdJobEventTopic) {

    // Convert byte data to a pojo
    ByteDataRepresentation byteDataRepresentation =
        byteToBeanConverter.mdtSyncRequestConverter(esbJob.getIvdMessageRequest().getMessage());

    // Map the pojo to the aggregate root
    esbJobMapper.byteDataRepresentationToIvdInboundEvent(esbJob, byteDataRepresentation);

    // Validate the pojo
    vehicleCommDomainService.validateByteData(esbJob);

    // Send acknowledgement back to MDT if ack required
    if (vehicleCommDomainService.isAckRequired(esbJob)) {
      sendAcknowledgementToMdt(esbJob);
    }

    // Retrieve ivdNo for further processes
    Integer ivdNo = esbJob.getByteData().getIvdNo();

    // Check store and forward cache if message already processed
    var messageAlreadyProcessed =
        vehicleCommCacheService.isKeyPresentInStoreForwardCache(
            esbJob.getByteData().getMessageId(), ivdNo, esbJob.getByteData().getSerialNumber());

    // End the process if message is already processed
    if (processedOrRedundantMessage(esbJob, messageAlreadyProcessed)) return;

    // Perform logical conversions on the fields of the pojo
    vehicleCommDomainService.parseGeoLocations(esbJob);
    vehicleCommDomainService.parseMoneyValues(esbJob);

    // Get vehicle ID by IVD number from MDT service
    VehicleDetailsResponse vehicleDetails = getVehicleDetailsByIvdNo(ivdNo);

    // Map MDT service response to the aggregate root
    esbJobMapper.vehicleDetailsResponseToIvdInboundEvent(esbJob, vehicleDetails);

    // Validate the MDT service response

    vehicleCommDomainService.validateVehicleDetails(esbJob);

    // Call Job Dispatch service
    JobOffersResponseData jobOffersResponse =
        callJobDispatchJobOffers(vehicleDetails.getVehicleId(), vehicleDetails.getDriverId());

    // send offerMessage to job_event
    sendToJobDispatchEvent(
        jobOffersResponse, String.valueOf(ivdNo), esbJob.getVehicleDetails().getIpAddress());
  }

  /**
   * we are getting a list of data. so we need to iterate each object and send it to job dispatch
   * event
   *
   * @param data data
   * @param ivdNo ivdNo
   * @param ipAddress
   */
  private void sendToJobDispatchEvent(JobOffersResponseData data, String ivdNo, String ipAddress) {
    List<JobOffersResponse> jobOffersResponse = data.getData();
    for (JobOffersResponse jobOffer : jobOffersResponse) {
      kafkaProducer.sendJobDispatchEvent(jobOffer, ivdNo, ipAddress);
    }
  }

  private JobOffersResponseData callJobDispatchJobOffers(String vehicleId, String driverId) {
    return jobDispatchAPIService.getJobOffersResponse(vehicleId, driverId);
  }

  @Override
  public IvdMessageEnum jobEventType() {
    return IvdMessageEnum.MDT_SYNC_REQUEST_EVENT_ID;
  }
}
