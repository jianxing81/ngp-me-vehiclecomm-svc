package com.cdg.pmg.ngp.me.vehiclecomm.techframework.cache.configs;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.BookingProductData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.PaymentMethodData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.BookingAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.PaxPaymentAPIService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationStartupCacheConfig implements ApplicationListener<ApplicationReadyEvent> {

  private final PaxPaymentAPIService paxPaymentAPIService;
  private final BookingAPIService bookingAPIService;
  private final VehicleCommCacheService vehicleCommCacheService;

  /**
   * Load payment mode data on startup of an application
   *
   * @param event the event to respond to
   */
  @Override
  public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {

    Optional<List<PaymentMethodData>> paymentMethodResponse =
        paxPaymentAPIService.getPaymentMethodList();
    log.info("[onApplicationEvent] PaymentMethodResponse API response {}:", paymentMethodResponse);
    paymentMethodResponse.ifPresent(
        paymentMethods ->
            paymentMethods.forEach(
                payment -> {
                  vehicleCommCacheService.putPaymentMethodToCacheAsId(
                      payment, payment.getPaymentMode());
                  vehicleCommCacheService.putPaymentMethodToCacheAsName(payment, payment.getCode());
                }));

    Optional<List<BookingProductData>> bookingProductDataResponse =
        bookingAPIService.getBookingProductList();
    log.info(
        "[onApplicationEvent] BookingProductResponse API response {}:", bookingProductDataResponse);
    bookingProductDataResponse.ifPresent(
        (bookingProduct ->
            bookingProduct.forEach(
                product -> {
                  vehicleCommCacheService.putBookingProductDetailsToCache(
                      product, product.getProductId());
                  vehicleCommCacheService.putBookingInVehicleDeviceCodeToCache(
                      product, String.valueOf(product.getInVehicleDeviceCode()));
                })));
  }
}
