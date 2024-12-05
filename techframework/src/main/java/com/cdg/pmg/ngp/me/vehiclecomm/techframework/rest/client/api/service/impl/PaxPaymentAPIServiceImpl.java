package com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.client.api.service.impl;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.PaymentMethodData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.PaxPaymentAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.NetworkException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.NotFoundException;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.JsonHelper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mappers.PaxPaymentMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.paxpaymentservice.client.apis.PaymentMethodApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.paxpaymentservice.client.models.PaymentMethodResponse;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.Cacheable;

/** Internal class for wrapper API calls to Pax payment Service */
@RequiredArgsConstructor
@ServiceComponent
@Slf4j
public class PaxPaymentAPIServiceImpl implements PaxPaymentAPIService {

  private final PaymentMethodApi paymentMethodApi;
  private final PaxPaymentMapper paxPaymentMapper;
  private final JsonHelper jsonHelper;

  /**
   * Method to return response for payment methods API
   *
   * @return list of payment method details
   */
  @Override
  public Optional<List<PaymentMethodData>> getPaymentMethodList() {
    try {
      PaymentMethodResponse paymentMethodResponse = paymentMethodApi.getPaymentMethodList().block();
      log.info(
          "[getPaymentMethodList] Response - {}", jsonHelper.pojoToJson(paymentMethodResponse));
      if (Objects.nonNull(paymentMethodResponse)
          && ObjectUtils.isNotEmpty(paymentMethodResponse.getData())) {
        return Optional.of(
            paxPaymentMapper.paymentMethodResponseMapper(paymentMethodResponse.getData()));
      }
    } catch (Exception e) {
      log.error(VehicleCommAppConstant.PAYMENT_METHOD_API_LOG_EXCEPTION, e);
    }
    return Optional.empty();
  }

  @Override
  @Cacheable(value = VehicleCommAppConstant.VEHICLE_COMM_PAYMENT_METHOD, key = "#paymentMethod")
  public PaymentMethodData getPaymentMethodData(String paymentMethod) {
    try {
      PaymentMethodResponse paymentMethodResponse = paymentMethodApi.getPaymentMethodList().block();
      log.info(
          "[getPaymentMethodData by paymentMethod] Response - {}",
          jsonHelper.pojoToJson(paymentMethodResponse));
      return paxPaymentMapper.paymentMethodResponseToPaymentMethodData(
          Objects.requireNonNull(Objects.requireNonNull(paymentMethodResponse).getData()).stream()
              .filter(paymentsMethodData -> paymentMethod.equals(paymentsMethodData.getCode()))
              .findAny()
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          paymentMethod, ErrorCode.PAYMENT_METHOD_NOT_FOUND.getCode())));
    } catch (Exception e) {
      log.error(VehicleCommAppConstant.PAYMENT_METHOD_API_LOG_EXCEPTION, e);
      throw new NetworkException(e.getMessage(), ErrorCode.PAYMENT_METHOD_NOT_FOUND.getCode());
    }
  }

  @Override
  @Cacheable(value = VehicleCommAppConstant.VEHICLE_COMM_PAYMENT_METHOD, key = "#meCode")
  public PaymentMethodData getPaymentMethodData(Integer meCode) {
    try {
      PaymentMethodResponse paymentMethodResponse = paymentMethodApi.getPaymentMethodList().block();
      log.info(
          "[getPaymentMethodData by meCode] Response - {}",
          jsonHelper.pojoToJson(paymentMethodResponse));
      return paxPaymentMapper.paymentMethodResponseToPaymentMethodData(
          Objects.requireNonNull(Objects.requireNonNull(paymentMethodResponse).getData()).stream()
              .filter(paymentsMethodData -> meCode.equals(paymentsMethodData.getPaymentMode()))
              .findAny()
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          String.valueOf(meCode), ErrorCode.ME_CODE_NOT_FOUND.getCode())));
    } catch (Exception e) {
      log.error(VehicleCommAppConstant.PAYMENT_METHOD_API_LOG_EXCEPTION, e);
      throw new NetworkException(e.getMessage(), ErrorCode.ME_CODE_NOT_FOUND.getCode());
    }
  }
}
