package com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.client.api.service.impl;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.BookingProductData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.BookingAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.NetworkException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.NotFoundException;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.JsonHelper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.bookingservice.client.apis.BookingControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.bookingservice.client.models.BookingProductsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mappers.BookingMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.Cacheable;

/** Internal class for wrapper API calls to Booking Service */
@RequiredArgsConstructor
@ServiceComponent
@Slf4j
public class BookingAPIServiceImpl implements BookingAPIService {

  private final BookingControllerApi bookingControllerApi;
  private final BookingMapper bookingMapper;
  private final JsonHelper jsonHelper;

  @Override
  public Optional<List<BookingProductData>> getBookingProductList() {
    try {
      BookingProductsResponse bookingProductsResponse =
          bookingControllerApi.getBookingProducts().block();
      log.info(
          "[getBookingProductList] Response - {}", jsonHelper.pojoToJson(bookingProductsResponse));
      if (Objects.nonNull(bookingProductsResponse)
          && ObjectUtils.isNotEmpty(bookingProductsResponse.getData())) {
        return Optional.of(
            bookingMapper.bookingResponseProductToBookingProduct(
                bookingProductsResponse.getData()));
      }
    } catch (Exception e) {
      log.error(VehicleCommAppConstant.BOOKING_PRODUCT_API_LOG_EXCEPTION, e);
    }
    return Optional.empty();
  }

  @Override
  @Cacheable(value = VehicleCommAppConstant.VEHICLE_COMM_BOOKING_PRODUCT, key = "#productId")
  public BookingProductData getBookingProductData(String productId) {
    try {
      BookingProductsResponse bookingProductsResponse =
          bookingControllerApi.getBookingProducts().block();
      log.info(
          "[getBookingProductData] Response - {}", jsonHelper.pojoToJson(bookingProductsResponse));
      return bookingMapper.bookingProductsToBookingProductData(
          Objects.requireNonNull(Objects.requireNonNull(bookingProductsResponse).getData()).stream()
              .filter(bookingProductData -> productId.equals(bookingProductData.getProductId()))
              .findAny()
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          productId, ErrorCode.BOOKING_PRODUCT_NOT_FOUND.getCode())));
    } catch (Exception e) {
      log.error(VehicleCommAppConstant.BOOKING_PRODUCT_API_LOG_EXCEPTION, e);
      throw new NetworkException(e.getMessage(), ErrorCode.BOOKING_PRODUCT_NOT_FOUND.getCode());
    }
  }

  /**
   * Get booking productId using inVehicleDeviceCode
   *
   * @param inVehicleDeviceCode inVehicleDeviceCode
   * @return BookingProductData
   */
  @Override
  @Cacheable(
      value = VehicleCommAppConstant.VEHICLE_COMM_IN_VEHICLE_DEVICE_CODE,
      key = "#inVehicleDeviceCode",
      unless = "#result ==null")
  public BookingProductData getBookingProductDataForInVehicleDeviceCode(
      String inVehicleDeviceCode) {
    try {
      BookingProductsResponse bookingProductsResponse =
          bookingControllerApi.getBookingProducts().block();
      log.info(
          "[getBookingProductData] Response - {}", jsonHelper.pojoToJson(bookingProductsResponse));
      return bookingMapper.bookingProductsToBookingProductData(
          Objects.requireNonNull(Objects.requireNonNull(bookingProductsResponse).getData()).stream()
              .filter(
                  bookingProductData ->
                      new BigDecimal(inVehicleDeviceCode)
                          .equals(bookingProductData.getInVehicleDeviceCode()))
              .findAny()
              .orElse(null));
    } catch (Exception e) {
      log.error(VehicleCommAppConstant.BOOKING_PRODUCT_API_LOG_EXCEPTION, e);
      throw new NetworkException(e.getMessage(), ErrorCode.BOOKING_PRODUCT_NOT_FOUND.getCode());
    }
  }
}
