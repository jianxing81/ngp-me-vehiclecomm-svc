package com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.cache;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.BookingProductData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.PaymentMethodData;

/** interface or port to handle VehicleComm cache outbound call */
public interface VehicleCommCacheService {

  /**
   * This method checks if an event is already processed by checking store and forward cache
   *
   * @param messageId messageId
   * @param ivdNo ivdNo
   * @param serialNumber serialNumber
   */
  boolean isKeyPresentInStoreForwardCache(Integer messageId, Integer ivdNo, Integer serialNumber);

  boolean isRedundantMessage(Integer messageId, String uniqueEventKey);

  /**
   * This method makes and entry in the store and forward cache.
   *
   * @param messageId messageId
   * @param ivdNo ivdNo
   * @param jobNo jobNo
   */
  boolean isCacheEntryInStoreAndForward(Integer messageId, Integer ivdNo, String jobNo);

  /**
   * This method is used to get job number from cache based on cache key
   *
   * @param msgId -messageId
   * @param ivdNo -driverId
   * @param msgSn -messgaeSn
   * @return job number
   */
  String getJobNumberFromCache(String msgId, Integer ivdNo, String msgSn);

  /**
   * This method is used to remove job number from cache
   *
   * @param msgId -messageId
   * @param ivdNo -driverId
   * @param msgSn -messageSn
   */
  void removeJobNumberFromCache(String msgId, Integer ivdNo, String msgSn);

  /**
   * This method makes and entry in the cache for vehicle comm payments
   *
   * @param paymentMethodData - Payment Methods
   * @param meCode - payment me code
   * @return Payment methods
   */
  PaymentMethodData putPaymentMethodToCacheAsId(
      PaymentMethodData paymentMethodData, Integer meCode);

  PaymentMethodData putPaymentMethodToCacheAsName(
      PaymentMethodData paymentMethodData, String paymentMethod);

  /**
   * This method retrieves payment methods from vehicle comm payments cache
   *
   * @param meCode - payment me code
   * @return Payment methods
   */
  PaymentMethodData getPaymentMethodFromCacheAsId(Integer meCode);

  /**
   * This method retrieves payment methods from vehicle comm payments cache
   *
   * @param paymentMethod - payment method name
   * @return Payment methods
   */
  PaymentMethodData getPaymentMethodFromCacheAsName(String paymentMethod);

  /**
   * This method adds data to cache based on productId
   *
   * @param bookingProductData - booking product
   * @param productId - product id
   * @return Booking product details
   */
  BookingProductData putBookingProductDetailsToCache(
      BookingProductData bookingProductData, String productId);

  /**
   * This method retrieves data from cache based on productId
   *
   * @param productId - product id
   * @return Booking product details
   */
  BookingProductData getBookingProductDetailsFromCache(String productId);

  /**
   * This method adds data to cache based on inVehicleDeviceCode
   *
   * @param bookingProductData -booking product data
   * @param inVehicleDeviceCode -in Vehicle Device Code
   * @return Booking product details
   */
  BookingProductData putBookingInVehicleDeviceCodeToCache(
      BookingProductData bookingProductData, String inVehicleDeviceCode);

  /**
   * This method retrieves data from cache based on inVehicleDeviceCode
   *
   * @param inVehicleDeviceCode -in Vehicle Device Code
   * @return Booking product details
   */
  BookingProductData getBookingInVehicleDeviceCodeFromCache(String inVehicleDeviceCode);
}
