package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.vehicleevent;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.BookingProductData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.PaymentMethodData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RedundantMessageKeyData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.RedundantMessageRequestHolder;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.AbstractJobEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.EsbJobMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.TripInfoMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbJob;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.IvdMessageEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.service.VehicleCommDomainService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.ByteData;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportTripInformation extends AbstractJobEvent {

  private final VehicleCommCacheService vehicleCommCacheService;
  private final EsbJobMapper esbJobMapper;
  private final TripInfoMapper tripInfoMapper;
  private final VehicleCommDomainService vehicleCommDomainService;

  @Override
  public void handleJobEvent(EsbJob esbJob, String ivdJobEventTopic) {

    // Convert byte data to a pojo
    var byteDataRepresentation =
        byteToBeanConverter.reportTripInfoNormalConverter(
            esbJob.getIvdMessageRequest().getMessage());

    // Map the pojo to the aggregate root
    esbJobMapper.byteDataRepresentationToIvdInboundEvent(esbJob, byteDataRepresentation);

    // Validate the pojo
    vehicleCommDomainService.validateByteData(esbJob);

    // Check if acknowledgement required
    var ackRequired = vehicleCommDomainService.isAckRequired(esbJob);

    // Send acknowledgement back to MDT if ack required
    if (ackRequired) {
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

    // Perform logical conversions on the money and coordinate fields of the pojo
    vehicleCommDomainService.parseGeoLocations(esbJob);
    vehicleCommDomainService.parseMoneyValues(esbJob);

    // Get vehicle ID by IVD number from MDT service
    var vehicleDetails = getVehicleDetailsByIvdNo(esbJob.getByteData().getIvdNo());

    // Map MDT service response to the aggregate root
    esbJobMapper.vehicleDetailsResponseToIvdInboundEvent(esbJob, vehicleDetails);

    // Validate the MDT service response
    vehicleCommDomainService.validateVehicleDetails(esbJob);

    // Validate comfortProtectPremium and cpFreeInsurance
    vehicleCommDomainService.validateComfortProtect(esbJob);

    // Validate platform fee applicability
    vehicleCommDomainService.validatePlatformFeeApplicability(esbJob);

    // Get productId from cache based on inVehicleDeviceCode
    var productId =
        Optional.ofNullable(esbJob.getByteData())
            .map(ByteData::getProductId)
            .filter(productIdVal -> !productIdVal.isEmpty())
            .map(vehicleCommCacheService::getBookingInVehicleDeviceCodeFromCache)
            .map(BookingProductData::getProductId)
            .orElseGet(
                () -> {
                  log.info("ProductId is not found,appending default value as STD001");
                  return VehicleCommAppConstant.PRODUCT_ID_STD001;
                });

    // Map productId to the aggregate root
    esbJobMapper.productIdToIvdInboundEvent(esbJob, productId);

    // Fetching PaymentMethod and setting to Esb PaymentMethod.
    PaymentMethodData paymentData =
        vehicleCommCacheService.getPaymentMethodFromCacheAsId(
            Integer.valueOf(byteDataRepresentation.getPaymentMethod()));
    esbJob.getByteData().setPaymentMethod(paymentData.getCode());

    // Prepare TripInfo and publish it to the kafka topic
    buildAndPublishKafkaMessage(esbJob);

    // Add entry to redundantMessage cache
    vehicleCommCacheService.isRedundantMessage(messageId, uniqueKey);
  }

  private void buildAndPublishKafkaMessage(EsbJob esbJob) {
    var tripInfo = tripInfoMapper.ivdInbountEventToTripInfo(esbJob);
    kafkaProducer.sendTripUploadEvent(tripInfo);
  }

  @Override
  public IvdMessageEnum jobEventType() {
    return IvdMessageEnum.REPORT_TRIP_INFO_NORMAL;
  }
}
