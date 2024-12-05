package com.cdg.pmg.ngp.me.vehiclecomm.techframework.cache.service.impl;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.BookingProductData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.PaymentMethodData;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.NetworkException;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.JsonHelper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.bookingservice.client.apis.BookingControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.bookingservice.client.models.BookingProducts;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.bookingservice.client.models.BookingProductsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.client.api.service.impl.BookingAPIServiceImpl;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.client.api.service.impl.PaxPaymentAPIServiceImpl;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mappers.BookingMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mappers.PaxPaymentMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.paxpaymentservice.client.apis.PaymentMethodApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.paxpaymentservice.client.models.PaymentMethodResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.paxpaymentservice.client.models.PaymentsMethodData;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class VehicleCommCacheServiceImplTest {
  @InjectMocks private VehicleCommCacheServiceImpl vehicleCommCacheOutboundPortImpl;
  @InjectMocks private PaxPaymentAPIServiceImpl paxPaymentAPIService;
  @InjectMocks private BookingAPIServiceImpl bookingAPIService;
  @InjectMocks private VehicleCommCacheServiceImpl vehicleCommCacheService;
  @Mock private PaymentMethodApi paymentMethodApi;
  @Mock private PaxPaymentMapper paxPaymentMapper;
  @Mock private BookingControllerApi bookingControllerApi;
  @Mock private BookingMapper bookingMapper;
  @Mock private JsonHelper jsonHelper;
  private PaymentMethodResponse paymentMethodResponse;
  private List<PaymentsMethodData> paymentMethodDataList;
  private BookingProductsResponse bookingProductsResponse;
  private List<BookingProducts> bookingProductsList;

  @BeforeEach
  public void setUp() {
    PaymentsMethodData paymentMethodData = new PaymentsMethodData();
    paymentMethodDataList = List.of(paymentMethodData);
    paymentMethodResponse = new PaymentMethodResponse();
    paymentMethodResponse.setData(paymentMethodDataList);
    BookingProducts bookingProducts = new BookingProducts();
    bookingProductsList = List.of(bookingProducts);
    bookingProductsResponse = new BookingProductsResponse();
    bookingProductsResponse.setData(bookingProductsList);
  }

  /** Method to test negative scenario for KeyPresentInStoreForwardCache */
  @Test
  void testIsKeyPresentInStoreForwardCache() {
    assertFalse(
        vehicleCommCacheOutboundPortImpl.isKeyPresentInStoreForwardCache(null, 72365, null));
  }

  @Test
  void testIsKeyPresentInRedundantCache() {
    assertFalse(vehicleCommCacheOutboundPortImpl.isRedundantMessage(154, null));
  }

  @Test
  void testKeyNetworkException() {
    assertThrows(NetworkException.class, () -> paxPaymentAPIService.getPaymentMethodData("NET"));
  }

  @Test
  void testKeyCodeNetworkException() {
    assertThrows(NetworkException.class, () -> paxPaymentAPIService.getPaymentMethodData(18));
  }

  @Test
  void testProductIdNetworkException() {
    assertThrows(NetworkException.class, () -> bookingAPIService.getBookingProductData("FLAT-001"));
  }

  @Test
  void testKeyNotFoundCodeInCache() {
    Integer meCode = 18;

    PaymentsMethodData paymentMethodData = new PaymentsMethodData();
    paymentMethodData.setPaymentMode(meCode);
    paymentMethodResponse.setData(Collections.emptyList());

    PaymentMethodData expectedPaymentMethodData = new PaymentMethodData();
    expectedPaymentMethodData.setPaymentMode(meCode);

    when(paymentMethodApi.getPaymentMethodList()).thenReturn(Mono.just(paymentMethodResponse));
    when(paxPaymentMapper.paymentMethodResponseToPaymentMethodData(paymentMethodData))
        .thenReturn(expectedPaymentMethodData);

    assertThrows(NetworkException.class, () -> paxPaymentAPIService.getPaymentMethodData(meCode));
  }

  @Test
  void testKeyNotFoundNameInCache() {
    String name = "NET";

    PaymentsMethodData paymentMethodData = new PaymentsMethodData();
    paymentMethodData.setCode(name);
    paymentMethodResponse.setData(Collections.emptyList());

    PaymentMethodData expectedPaymentMethodData = new PaymentMethodData();
    expectedPaymentMethodData.setCode(name);

    when(paymentMethodApi.getPaymentMethodList()).thenReturn(Mono.just(paymentMethodResponse));
    when(paxPaymentMapper.paymentMethodResponseToPaymentMethodData(paymentMethodData))
        .thenReturn(expectedPaymentMethodData);

    assertThrows(NetworkException.class, () -> paxPaymentAPIService.getPaymentMethodData(name));
  }

  /** Method to test negative scenario for ibVehicleCode not available in cache */
  @Test
  void testInVehicleDeviceCodeNotAvailable() {
    String productId = "STD001";

    BookingProducts bookingProducts = new BookingProducts();
    bookingProducts.setInVehicleDeviceCode(BigDecimal.ZERO);
    bookingProducts.setProductId(productId);
    bookingProductsResponse.setData(Collections.emptyList());

    BookingProductData expectedBookingProductData = new BookingProductData();
    expectedBookingProductData.setProductId(productId);

    when(bookingControllerApi.getBookingProducts()).thenReturn(Mono.just(bookingProductsResponse));
    when(bookingMapper.bookingProductsToBookingProductData(bookingProducts))
        .thenReturn(expectedBookingProductData);
    Assertions.assertNull(bookingAPIService.getBookingProductDataForInVehicleDeviceCode("1"));
  }

  @Test
  void testKeyNotFoundProductInCache() {
    String name = "FLAT-001";

    BookingProducts bookingProducts = new BookingProducts();
    bookingProducts.setProductId(name);
    bookingProductsResponse.setData(Collections.emptyList());

    BookingProductData expectedBookingProductData = new BookingProductData();
    expectedBookingProductData.setProductId(name);

    when(bookingControllerApi.getBookingProducts()).thenReturn(Mono.just(bookingProductsResponse));
    when(bookingMapper.bookingProductsToBookingProductData(bookingProducts))
        .thenReturn(expectedBookingProductData);

    assertThrows(NetworkException.class, () -> bookingAPIService.getBookingProductData(name));
  }

  @Test
  void testPutDataToPaymentCacheAsId() {
    Integer responseCode =
        vehicleCommCacheService
            .putPaymentMethodToCacheAsId(paymentMethodData(), 8)
            .getPaymentMode();
    Assertions.assertEquals(8, responseCode);
  }

  @Test
  void testPutDataToPaymentCacheAsName() {
    String responseMethod =
        vehicleCommCacheService
            .putPaymentMethodToCacheAsName(paymentMethodData(), "NETS")
            .getCode();
    Assertions.assertEquals("NETS", responseMethod);
  }

  @Test
  void testPutDataToBookingCache() {
    String responseMethod =
        vehicleCommCacheService
            .putBookingProductDetailsToCache(bookingProductData(), "FLAT-001")
            .getProductId();
    Assertions.assertEquals("FLAT-001", responseMethod);
  }

  /** Method to test insert vehicleId to inVehicleDeviceCache */
  @Test
  void testPutDataToInVehicleDeviceCache() {
    String responseMethod =
        vehicleCommCacheService
            .putBookingInVehicleDeviceCodeToCache(bookingProductData(), "221")
            .getProductId();
    Assertions.assertEquals("FLAT-001", responseMethod);
  }

  @Test
  void testGetPaymentMethodDataCodeSuccess() {

    Integer meCode = 8;

    PaymentsMethodData paymentMethodData = new PaymentsMethodData();
    paymentMethodData.setPaymentMode(meCode);
    paymentMethodResponse.setData(Collections.singletonList(paymentMethodData));

    PaymentMethodData expectedPaymentMethodData = new PaymentMethodData();
    expectedPaymentMethodData.setPaymentMode(meCode);

    when(paymentMethodApi.getPaymentMethodList()).thenReturn(Mono.just(paymentMethodResponse));
    when(paxPaymentMapper.paymentMethodResponseToPaymentMethodData(paymentMethodData))
        .thenReturn(expectedPaymentMethodData);
    when(jsonHelper.pojoToJson(any())).thenReturn(StringUtils.EMPTY);
    PaymentMethodData result = paxPaymentAPIService.getPaymentMethodData(meCode);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedPaymentMethodData, result);
  }

  @Test
  void testGetPaymentMethodDataNameSuccess() {

    String name = "NETS";

    PaymentsMethodData paymentMethodData = new PaymentsMethodData();
    paymentMethodData.setCode(name);
    paymentMethodResponse.setData(Collections.singletonList(paymentMethodData));

    PaymentMethodData expectedPaymentMethodData = new PaymentMethodData();
    expectedPaymentMethodData.setCode(name);

    when(paymentMethodApi.getPaymentMethodList()).thenReturn(Mono.just(paymentMethodResponse));
    when(paxPaymentMapper.paymentMethodResponseToPaymentMethodData(paymentMethodData))
        .thenReturn(expectedPaymentMethodData);
    when(jsonHelper.pojoToJson(any())).thenReturn(StringUtils.EMPTY);
    PaymentMethodData result = paxPaymentAPIService.getPaymentMethodData(name);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedPaymentMethodData, result);
  }

  @Test
  void testGetProductIdSuccess() {

    String name = "FLAT-001";

    BookingProducts bookingProducts = new BookingProducts();
    bookingProducts.setProductId(name);
    bookingProductsResponse.setData(Collections.singletonList(bookingProducts));

    BookingProductData expectedProductData = new BookingProductData();
    expectedProductData.setProductId(name);

    when(bookingControllerApi.getBookingProducts()).thenReturn(Mono.just(bookingProductsResponse));
    when(bookingMapper.bookingProductsToBookingProductData(bookingProducts))
        .thenReturn(expectedProductData);
    when(jsonHelper.pojoToJson(any())).thenReturn(StringUtils.EMPTY);
    BookingProductData result = bookingAPIService.getBookingProductData(name);

    Assertions.assertNotNull(result);
    Assertions.assertEquals(expectedProductData, result);
  }

  @Test
  void testGetPaymentMethodListSuccess() {

    PaymentMethodData paymentMethodData = new PaymentMethodData();
    List<PaymentMethodData> paymentMethodDataLists = List.of(paymentMethodData);
    paymentMethodResponse = new PaymentMethodResponse();
    paymentMethodResponse.setData(paymentMethodDataList);

    when(paymentMethodApi.getPaymentMethodList()).thenReturn(Mono.just(paymentMethodResponse));
    when(paxPaymentMapper.paymentMethodResponseMapper(paymentMethodDataList))
        .thenReturn(paymentMethodDataLists);
    when(jsonHelper.pojoToJson(any())).thenReturn(StringUtils.EMPTY);
    Optional<List<PaymentMethodData>> result = paxPaymentAPIService.getPaymentMethodList();

    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals(paymentMethodDataLists, result.get());
  }

  @Test
  void testGetPaymentMethodListEmptyData() {
    paymentMethodResponse.setData(null);
    when(paymentMethodApi.getPaymentMethodList()).thenReturn(Mono.just(paymentMethodResponse));

    Optional<List<PaymentMethodData>> result = paxPaymentAPIService.getPaymentMethodList();

    assertFalse(result.isPresent());
  }

  @Test
  void testGetPaymentMethodListExceptionScenario() {
    when(paymentMethodApi.getPaymentMethodList()).thenThrow(new RuntimeException("API error"));

    Optional<List<PaymentMethodData>> result = paxPaymentAPIService.getPaymentMethodList();

    assertFalse(result.isPresent());
  }

  @Test
  void testGetProductListSuccess() {

    BookingProductData bookingProductData = new BookingProductData();
    List<BookingProductData> bookingProductDataLists = List.of(bookingProductData);
    bookingProductsResponse = new BookingProductsResponse();
    bookingProductsResponse.setData(bookingProductsList);

    when(bookingControllerApi.getBookingProducts()).thenReturn(Mono.just(bookingProductsResponse));
    when(bookingMapper.bookingResponseProductToBookingProduct(bookingProductsList))
        .thenReturn(bookingProductDataLists);
    when(jsonHelper.pojoToJson(any())).thenReturn(StringUtils.EMPTY);
    Optional<List<BookingProductData>> result = bookingAPIService.getBookingProductList();

    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals(bookingProductDataLists, result.get());
  }

  @Test
  void testGetProductListEmptyData() {
    bookingProductsResponse.setData(null);
    when(bookingControllerApi.getBookingProducts()).thenReturn(Mono.just(bookingProductsResponse));

    Optional<List<BookingProductData>> result = bookingAPIService.getBookingProductList();

    assertFalse(result.isPresent());
  }

  @Test
  void testGetProductListExceptionScenario() {
    when(bookingControllerApi.getBookingProducts()).thenThrow(new RuntimeException("API error"));

    Optional<List<BookingProductData>> result = bookingAPIService.getBookingProductList();

    assertFalse(result.isPresent());
  }

  public PaymentMethodData paymentMethodData() {
    PaymentMethodData paymentsMethodData = new PaymentMethodData();
    paymentsMethodData.setPaymentMode(8);
    paymentsMethodData.setCode("NETS");
    return paymentsMethodData;
  }

  public BookingProductData bookingProductData() {
    BookingProductData bookingProductData = new BookingProductData();
    bookingProductData.setProductId("FLAT-001");
    bookingProductData.setInVehicleDeviceCode(227);
    return bookingProductData;
  }
}
