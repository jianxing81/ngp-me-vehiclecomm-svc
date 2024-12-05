package com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.client.api.service.impl;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.FareTariffRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.FareTariffResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.FareAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.DomainException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.NetworkException;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.JsonHelper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.VehicleCommFrameworkConstants;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fare.client.apis.BookingPaymentsApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fare.client.models.IvdFareResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mappers.FareMapper;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/** Internal class for wrapper API calls to Fare Service */
@RequiredArgsConstructor
@ServiceComponent
@Slf4j
public class FareAPIServiceImpl implements FareAPIService {
  private final BookingPaymentsApi bookingPaymentsApi;
  private final FareMapper fareMapper;
  private final JsonHelper jsonHelper;

  @Override
  public FareTariffResponse fareTariff(final FareTariffRequest fareTariffRequest) {
    try {
      log.info("[fareTariff] Request - {}", jsonHelper.pojoToJson(fareTariffRequest));
      IvdFareResponse response =
          bookingPaymentsApi
              .getTariffCalculation(fareMapper.mapToIvdFareRequest(fareTariffRequest))
              .block();
      log.info("[fareTariff] Response - {}", jsonHelper.pojoToJson(response));
      return Optional.ofNullable(fareMapper.mapToFareTariffResponse(response))
          .filter(v -> Objects.nonNull(v.getData()))
          .orElseThrow(() -> new DomainException(ErrorCode.FARE_SERVICE_NETWORK_ERROR.getCode()));

    } catch (WebClientResponseException exception) {
      log.error(
          VehicleCommFrameworkConstants.LOG_BRACKETS_BRACKETS,
          ErrorCode.FARE_SERVICE_NETWORK_ERROR.getCode(),
          exception.getMessage());
      throw new NetworkException(
          exception.getResponseBodyAsString(), ErrorCode.FARE_SERVICE_NETWORK_ERROR.getCode());
    }
  }
}
