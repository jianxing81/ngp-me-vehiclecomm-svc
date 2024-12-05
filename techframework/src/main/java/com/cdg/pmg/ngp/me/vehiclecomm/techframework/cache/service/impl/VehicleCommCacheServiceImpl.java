package com.cdg.pmg.ngp.me.vehiclecomm.techframework.cache.service.impl;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.BookingProductData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.PaymentMethodData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache.VehicleCommCacheService;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.BookingAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest.PaxPaymentAPIService;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.VehCommUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

/** VehicleCommCacheServiceImpl handle implementation of cache communication. */
@RequiredArgsConstructor
@ServiceComponent
@Slf4j
public class VehicleCommCacheServiceImpl implements VehicleCommCacheService {

  private final CacheManager cacheManager;
  private final PaxPaymentAPIService paxPaymentAPIService;
  private final BookingAPIService bookingAPIService;

  /**
   * This method checks if an event is already processed by checking store and forward cache
   *
   * @param messageId messageId
   * @param ivdNo ivdNo
   * @param serialNumber serialNumber
   */
  @Override
  public boolean isKeyPresentInStoreForwardCache(
      Integer messageId, Integer ivdNo, Integer serialNumber) {
    if (messageId == null || ivdNo == null || serialNumber == null) {
      log.info(
          "[isKeyPresentInStoreForwardCache] Null values found - MessageId: {} IvdNo: {} SerialNumber: {}",
          messageId,
          ivdNo,
          serialNumber);
      return false;
    }
    Cache cache = cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME);
    String cacheKey = String.valueOf(messageId) + ivdNo + serialNumber;
    if (cache != null && cache.get(cacheKey) == null) {
      log.debug("[isKeyPresentInStoreForwardCache] Cache put operation for key {}", cacheKey);
      cache.put(cacheKey, true);
      return false;
    }
    return true;
  }

  /**
   * This method checks if an event is already processed
   *
   * @param messageId - event identifier
   * @param uniqueEventKey - unique event key jobNo/vehicle id based on event
   * @return boolean
   */
  @Override
  public boolean isRedundantMessage(Integer messageId, String uniqueEventKey) {
    if (messageId == null || StringUtils.isEmpty(uniqueEventKey)) {
      log.info(
          "[isRedundantMessage] Null values found - MessageId: {} uniqueEventKey: {}",
          messageId,
          uniqueEventKey);
      return false;
    }
    Cache cache = cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE);
    String cacheKey = messageId + uniqueEventKey;
    if (cache != null && cache.get(cacheKey) == null) {
      log.debug("[isRedundantMessage] Cache put operation for key {}", cacheKey);
      cache.put(cacheKey, true);
      return false;
    }
    return true;
  }

  /**
   * This method makes and entry in the store and forward cache.
   *
   * @param messageId messageId
   * @param ivdNo ivdNo
   * @param jobNo jobNo
   */
  @Override
  public boolean isCacheEntryInStoreAndForward(Integer messageId, Integer ivdNo, String jobNo) {
    Cache cache = cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME);
    String cacheKey = String.valueOf(messageId) + ivdNo + VehCommUtils.getSerialNum();
    if (cache != null && cache.get(cacheKey) == null) {
      log.info(
          "storeForwardKey key for job_offer request is : {} for job_no : {} ", cacheKey, jobNo);
      cache.put(cacheKey, jobNo);
      return false;
    }
    return true;
  }

  /**
   * get Job number from cache
   *
   * @param msgId -received message id
   * @param ivdNo -driver id
   * @param msgSn -received message sn
   * @return job number
   */
  @Override
  @Cacheable(
      value = VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME,
      key = "#msgId + #ivdNo + #msgSn")
  public String getJobNumberFromCache(String msgId, Integer ivdNo, String msgSn) {
    return null;
  }

  @Override
  @CacheEvict(
      value = VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME,
      key = "#msgId + #ivdNo + #msgSn")
  public void removeJobNumberFromCache(String msgId, Integer ivdNo, String msgSn) {
    log.info("job number is removed from cache for key {} ", msgId + ivdNo + msgSn);
  }

  /**
   * Add payment method to cache using me code as key
   *
   * @param paymentMethodData - Payment Methods
   * @param meCode - payment me code
   * @return paymentMethodData
   */
  @Override
  @CachePut(value = VehicleCommAppConstant.VEHICLE_COMM_PAYMENT_METHOD, key = "#meCode")
  public PaymentMethodData putPaymentMethodToCacheAsId(
      PaymentMethodData paymentMethodData, Integer meCode) {
    return paymentMethodData;
  }

  /**
   * Add payment method to cache using payment method name as key
   *
   * @param paymentMethodData - Payment Methods
   * @param paymentMethod - payment method name
   * @return paymentMethodData
   */
  @Override
  @CachePut(value = VehicleCommAppConstant.VEHICLE_COMM_PAYMENT_METHOD, key = "#paymentMethod")
  public PaymentMethodData putPaymentMethodToCacheAsName(
      PaymentMethodData paymentMethodData, String paymentMethod) {
    return paymentMethodData;
  }

  /**
   * Load payment method from cache using me code as key
   *
   * @param meCode - payment me code
   * @return paymentMethodData
   */
  @Override
  @Cacheable(value = VehicleCommAppConstant.VEHICLE_COMM_PAYMENT_METHOD, key = "#meCode")
  public PaymentMethodData getPaymentMethodFromCacheAsId(Integer meCode) {
    return paxPaymentAPIService.getPaymentMethodData(meCode);
  }

  /**
   * Load payment method from cache using payment method name as key
   *
   * @param paymentMethod - payment method name
   * @return paymentMethodData
   */
  @Override
  @Cacheable(value = VehicleCommAppConstant.VEHICLE_COMM_PAYMENT_METHOD, key = "#paymentMethod")
  public PaymentMethodData getPaymentMethodFromCacheAsName(String paymentMethod) {
    return paxPaymentAPIService.getPaymentMethodData(paymentMethod);
  }

  @Override
  @CachePut(value = VehicleCommAppConstant.VEHICLE_COMM_BOOKING_PRODUCT, key = "#productId")
  public BookingProductData putBookingProductDetailsToCache(
      BookingProductData bookingProductData, String productId) {
    return bookingProductData;
  }

  @Override
  @Cacheable(value = VehicleCommAppConstant.VEHICLE_COMM_BOOKING_PRODUCT, key = "#productId")
  public BookingProductData getBookingProductDetailsFromCache(String productId) {
    return bookingAPIService.getBookingProductData(productId);
  }

  /**
   * put productId to cache based on inVehicleDeviceCode
   *
   * @param bookingProductData -booking product data
   * @param inVehicleDeviceCode -in Vehicle Device Code
   * @return BookingProductData
   */
  @Override
  @CachePut(
      value = VehicleCommAppConstant.VEHICLE_COMM_IN_VEHICLE_DEVICE_CODE,
      key = "#inVehicleDeviceCode")
  public BookingProductData putBookingInVehicleDeviceCodeToCache(
      BookingProductData bookingProductData, String inVehicleDeviceCode) {
    return bookingProductData;
  }

  /**
   * get productId from cache based on inVehicleDeviceCode
   *
   * @param inVehicleDeviceCode -in Vehicle Device Code
   * @return BookingProductData
   */
  @Override
  @Cacheable(
      value = VehicleCommAppConstant.VEHICLE_COMM_IN_VEHICLE_DEVICE_CODE,
      key = "#inVehicleDeviceCode",
      unless = "#result==null")
  public BookingProductData getBookingInVehicleDeviceCodeFromCache(String inVehicleDeviceCode) {
    return bookingAPIService.getBookingProductDataForInVehicleDeviceCode(inVehicleDeviceCode);
  }
}
