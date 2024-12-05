package integration.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.esbjobevent.EsbJobEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.bookingservice.client.models.BookingProducts;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.bookingservice.client.models.BookingProductsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.paxpaymentservice.client.models.PaymentMethodResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.paxpaymentservice.client.models.PaymentsMethodData;
import integration.constants.IntegrationTestConstants;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EsbJobEventRequestUtility {

  private static final String ESB_JOB_EVENT_FIELD = "EsbJobEvent";

  public static EsbJobEvent constructJobEventForAccept() {

    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("154")
        .withMessage(IntegrationTestConstants.JOB_ACCEPT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructJobEventForRejectJobModification() {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("179")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructJobEventForAcknowledgeJobCancellation() {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("153")
        .withMessage(IntegrationTestConstants.ACKNOWLEDGE_JOB_CANCELLATION)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructJobEventForJobConfirmAcknowledge() {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("188")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructJobEventForCallout() {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("158")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructJobEventForAcknowledgeConvertStreethail() {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("151")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructIVDEventForMsgAlreadyProcessed(String eventId) {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier(eventId)
        .withMessage(
            "DB DC 7F C6 65 8C 3F 70 00 22 80 00 BA 9F EA 65 00 31 00 04 00 D1 07 00 00 BE 00 01 00 32 31 30 2E 36 31 2E 32 36 2E 32 33 39 20 20 20")
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructArrivePickUpEvent() {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("156")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructNotifyOncallEvent() {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("184")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructUpdateStopEvent() {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("213")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructJobEventForReportTripInfoNormal() {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("221")
        .withMessage(IntegrationTestConstants.REPORT_TRIP_INFO_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public EsbJobEvent constructJobEventForReportTripInfoNormalStreethail() {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("221")
        .withMessage(IntegrationTestConstants.REPORT_TRIP_INFO_STREETHAIL_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructJobEventWithBlankMessage(String eventId) {

    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier(eventId)
        .withMessage("  ")
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructJobEventForInvalidEventId() {

    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("90909090")
        .withMessage(IntegrationTestConstants.REPORT_TRIP_INFO_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructNoShowEvent() {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("159")
        .withMessage(IntegrationTestConstants.NO_SHOW_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructNoShowEventForInvalidLocation() {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("159")
        .withMessage(IntegrationTestConstants.NO_SHOW_INVALID_LOCATION_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructMeterOnStreethailJobEvent() {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("139")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructMeterOffStreethailJobEvent() {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("140")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructAutoAcceptJobConfirmationAck() {

    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("190")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /** job event for job modify */
  public static EsbJobEvent constructJobEventForModification() {

    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("152")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Method to construct JobEvent For Message Acknowledge
   *
   * @return EsbJobEvent
   */
  public static EsbJobEvent constructJobEventForMessageAcknowledge() {

    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("251")
        .withMessage(IntegrationTestConstants.MESSAGE_ACKNOWLEDGEMENT_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /** Method to construct meter off event * @return EsbJobEvent */
  public static EsbJobEvent constructMeterOffEvent() {

    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("161")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructMeterOnDispatchEvent() {

    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("160")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructJobEventForRejectWithMessage(String message) {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("155")
        .withMessage(message)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructJobEventForMdtRequest() {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("176")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /** job event for Fare Calculation Request */
  public static EsbJobEvent constructFareCalculationRequest() {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("194")
        .withMessage(IntegrationTestConstants.FARE_REQUEST)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructFareCalculationMessageRequest() {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("194")
        .withMessage(IntegrationTestConstants.FARE_REQUEST)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructAdvanceJobRemindEvent() {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("169")
        .withMessage(IntegrationTestConstants.ADVANCED_JOB_REMIND_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbJobEvent constructJobNumberBlockRequestEvent() {
    return EsbJobEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("141")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static PaymentMethodResponse generatePaymentMethodResponse() {
    PaymentMethodResponse paymentMethodResponse;
    List<PaymentsMethodData> paymentMethodDataList;
    PaymentsMethodData paymentMethodDataOnBoard = new PaymentsMethodData();
    paymentMethodDataOnBoard.paymentMode(7);
    paymentMethodDataOnBoard.code("ON_BOARD");
    paymentMethodDataOnBoard.codeDesc("ON BOARD");
    paymentMethodDataOnBoard.entryMode(1);
    PaymentsMethodData paymentMethodData = new PaymentsMethodData();
    paymentMethodData.paymentMode(8);
    paymentMethodData.code("PAYMENT_MODE_8");
    paymentMethodData.codeDesc("PAYMENT MODE 8");
    paymentMethodData.entryMode(1);
    paymentMethodDataList = List.of(paymentMethodDataOnBoard, paymentMethodData);
    paymentMethodResponse = new PaymentMethodResponse();
    paymentMethodResponse.setData(paymentMethodDataList);
    return paymentMethodResponse;
  }

  public static BookingProductsResponse generateBookingProductResponse() {
    BookingProductsResponse bookingProductsResponse = new BookingProductsResponse();
    List<BookingProducts> bookingProductsList;
    BookingProducts bookingProducts = new BookingProducts();
    bookingProducts.setProductId("FLAT-001");
    bookingProducts.setInVehicleDeviceCode(new BigDecimal(227));
    bookingProductsList = List.of(bookingProducts);
    bookingProductsResponse.setData(bookingProductsList);
    return bookingProductsResponse;
  }

  public static BookingProductsResponse generateAlternateBookingProductResponse() {
    BookingProductsResponse bookingProductsResponse = new BookingProductsResponse();
    List<BookingProducts> bookingProductsList;
    BookingProducts bookingProducts = new BookingProducts();
    bookingProducts.setProductId("FLAT-001");
    bookingProducts.setInVehicleDeviceCode(null);
    bookingProductsList = List.of(bookingProducts);
    bookingProductsResponse.setData(bookingProductsList);
    return bookingProductsResponse;
  }
}
