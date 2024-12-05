package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.jobevent;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.FareTariffInfo;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.FareTariffRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.FareTariffResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VehicleDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.AbstractJobEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.EsbJobMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.FareTariffMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.FareAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbJob;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdMessageEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.ByteDataRepresentation;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.FareTariff;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.GenericEventCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.service.VehicleCommDomainService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.ByteData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * This class is used to handle {@link IvdMessageEnum#FARE_CALCULATION_REQUEST} Event for
 * ivdJobEventTopic
 *
 * @see <a href="https://comfortdelgrotaxi.atlassian.net/wiki/x/soPSSw">4.2.1.15.31 Fare Calculation
 *     Request Event Use Case Design Document</a>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FareCalculationRequest extends AbstractJobEvent {

  private final EsbJobMapper esbJobMapper;
  private final FareTariffMapper fareTariffMapper;
  private final VehicleCommDomainService vehicleCommDomainService;
  private final VehicleCommCacheService vehicleCommCacheService;
  private final FareAPIService fareAPIService;

  @Override
  public void handleJobEvent(final EsbJob esbJob, final String ivdJobEventTopic) {
    // Convert byte data to a pojo
    ByteDataRepresentation byteDataRepresentation =
        byteToBeanConverter.fareCalculationRequestConverter(
            esbJob.getIvdMessageRequest().getMessage());

    // Map the pojo to the aggregate root
    esbJobMapper.byteDataRepresentationToIvdInboundEvent(esbJob, byteDataRepresentation);

    // Validate the pojo
    vehicleCommDomainService.validateByteData(esbJob);

    // Check if acknowledgement required
    boolean isAckRequired = vehicleCommDomainService.isAckRequired(esbJob);

    // Send acknowledgement back to MDT if ack required
    if (isAckRequired) {
      sendAcknowledgementToMdt(esbJob);
    }

    // Check for redundantMessage
    var redundantMessage =
        !VehicleCommAppConstant.FARE_CALCULATION_REQUEST_STREET_JOB_NO.equals(
                esbJob.getByteData().getJobNo())
            && vehicleCommCacheService.isRedundantMessage(
                esbJob.getByteData().getMessageId(), esbJob.getByteData().getJobNo());

    if (processedOrRedundantMessage(esbJob, redundantMessage)) return;

    // Check store and forward cache if message already processed
    var messageAlreadyProcessed =
        vehicleCommCacheService.isKeyPresentInStoreForwardCache(
            esbJob.getByteData().getMessageId(),
            esbJob.getByteData().getIvdNo(),
            esbJob.getByteData().getSerialNumber());

    // End the process if message is already processed
    if (processedOrRedundantMessage(esbJob, messageAlreadyProcessed)) return;

    // Perform logical conversions on the fields of the pojo
    vehicleCommDomainService.parseGeoLocations(esbJob);
    vehicleCommDomainService.parseMoneyValues(esbJob);

    // Get vehicle ID by IVD number from MDT service
    VehicleDetailsResponse vehicleDetails =
        getVehicleDetailsByIvdNo(esbJob.getByteData().getIvdNo());

    // Map MDT service response to the aggregate root
    esbJobMapper.vehicleDetailsResponseToIvdInboundEvent(esbJob, vehicleDetails);

    // Validate the MDT service response
    vehicleCommDomainService.validateVehicleDetails(esbJob);

    // Call Fare service to process the event
    callFareSvcToFareTariffAndSendMsgToIvdResponse(esbJob);
  }

  private void callFareSvcToFareTariffAndSendMsgToIvdResponse(final EsbJob esbJob) {
    ByteData byteData = esbJob.getByteData();
    List<FareTariffInfo> tariffList =
        Optional.ofNullable(byteData.getTariffInfo()).orElseGet(ArrayList::new).stream()
            .map(
                item ->
                    FareTariffInfo.builder()
                        .tariffTypeCode(item.getTariffTypeCode())
                        .quantity(item.getTariffUnit())
                        .build())
            .toList();

    FareTariffRequest fareTariffRequest =
        FareTariffRequest.builder()
            .jobNo(byteData.getJobNo())
            .vehicleId(esbJob.getVehicleDetails().getId())
            .ivdCode(byteData.getProductId())
            .corpCardNo(byteData.getCorpCardNo())
            .payment(
                vehicleCommCacheService
                    .getPaymentMethodFromCacheAsId(Integer.valueOf(byteData.getPaymentMethod()))
                    .getCode())
            .tarifflist(tariffList)
            .accountId(byteData.getAccountNumber())
            .build();
    FareTariffResponse fareTariffResponse = fareAPIService.fareTariff(fareTariffRequest);
    FareTariff fareTariff = fareTariffMapper.fareTariffResponseToFareTariff(fareTariffResponse);
    if (vehicleCommDomainService.isFareCalculationResponseNotRequired(fareTariff)) {
      log.info(
          "[callFareSvcToFareTariffAndSendMsgToIvdResponse] Fare calculation response is not required as booking Fee is 0");
      return;
    }
    GenericEventCommand genericEventCommand =
        beanToByteConverter.convertToByteFareCalculation(
            byteData, esbJob.getVehicleDetails().getIpAddress(), fareTariffResponse);
    // Map response message id to the aggregate root
    vehicleCommDomainService.setResponseMessageId(
        esbJob, Integer.parseInt(IvdMessageEnum.FARE_CALCULATION_RESPONSE.getId()));
    // Map genericEventCommand to the aggregate root
    esbJobMapper.genericEventCommandToByteArrayData(esbJob, genericEventCommand);
    sendAcknowledgementToIvdResponse(esbJob);
  }

  @Override
  public IvdMessageEnum jobEventType() {
    return IvdMessageEnum.FARE_CALCULATION_REQUEST;
  }
}
