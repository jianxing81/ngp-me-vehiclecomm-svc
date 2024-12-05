package integration.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.esbregularreportevent.EsbRegularReportEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.esbvehicleevent.EsbVehicleEvent;
import integration.constants.IntegrationTestConstants;
import java.time.LocalDateTime;
import java.util.UUID;

public class EsbVehicleEventRequestUtility {

  private EsbVehicleEventRequestUtility() {}

  private static final String ESB_JOB_EVENT_FIELD = "EsbVehicleEvent";
  private static final String ESB_REGULAR_REPORT_EVENT_FIELD = "EsbRegularReportEvent";

  public static EsbVehicleEvent constructUpdateStcEvent() {

    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("137")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbVehicleEvent constructBusyEvent() {

    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("135")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbVehicleEvent constructRegularEvent() {

    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("136")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbRegularReportEvent constructRegularEvent1() {

    return EsbRegularReportEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("136")
        .withMessage(IntegrationTestConstants.REGULAR_REPORT_BYTE_DATA_INPUT)
        .withEventType(ESB_REGULAR_REPORT_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Method to construct empty vehicle event
   *
   * @return EsbVehicleEvent
   */
  public static EsbVehicleEvent constructEmptyVehicleEvent() {

    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("137")
        .withMessage(" ")
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbVehicleEvent constructUpdatePingEvent() {

    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("163")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Method to construct ivd event
   *
   * @return EsbVehicleEvent
   */
  public static EsbVehicleEvent constructIVDEvent() {

    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("219")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Method to construct ivd event with invalid event details in byte array
   *
   * @return EsbVehicleEvent
   */
  public static EsbVehicleEvent constructIVDEventInvalidEventData() {
    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("219")
        .withMessage(
            "DB 8E 0C C6 00 00 00 00 00 00 0B 00 29 91 37 59 00 31 00 04 00 D0 07 00 00 BE 00 0E 00 54 68 69 73 20 69 73 20 61 20 74 65 73 74 31 30 2E 36 31 2E 35 33 2E 31 37 30 20 20 20")
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Method to construct ivd event for enable auto bid event details
   *
   * @return EsbVehicleEvent
   */
  public static EsbVehicleEvent constructIVDEventForEnableAutoBid() {

    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("219")
        .withMessage(
            "DB D7 8D C6 5A 8C 69 70 01 25 80 39 10 9E EA 65 00 31 00 04 00 D1 07 00 00 BE 00 01 00 31 31 30 2E 36 31 2E 35 34 2E 33 30 20 20 20 20")
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Method to construct ivd event for enable auto accept event details
   *
   * @return EsbVehicleEvent
   */
  public static EsbVehicleEvent constructIVDEventForEnableAutoAccept() {
    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("219")
        .withMessage(
            "DB DC 7F C6 65 8C 3F 70 00 22 80 00 BA 9F EA 65 00 31 00 04 00 D1 07 00 00 BE 00 01 00 32 31 30 2E 36 31 2E 32 36 2E 32 33 39 20 20 20")
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Method to construct IVD Event For Msg Already Processed
   *
   * @return EsbVehicleEvent
   */
  public static EsbVehicleEvent constructIVDEventForMsgAlreadyProcessed(String eventId) {
    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier(eventId)
        .withMessage(
            "DB DC 7F C6 65 8C 3F 70 00 22 80 00 BA 9F EA 65 00 31 00 04 00 D1 07 00 00 BE 00 01 00 32 31 30 2E 36 31 2E 32 36 2E 32 33 39 20 20 20")
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Method to construct IVD Event For Msg Already Processed
   *
   * @return EsbVehicleEvent
   */
  public static EsbRegularReportEvent constructIVDRegularReportEventForMsgAlreadyProcessed(
      String eventId) {
    return EsbRegularReportEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier(eventId)
        .withMessage(
            "DB DC 7F C6 65 8C 3F 70 00 22 80 00 BA 9F EA 65 00 31 00 04 00 D1 07 00 00 BE 00 01 00 32 31 30 2E 36 31 2E 32 36 2E 32 33 39 20 20 20")
        .withEventType(ESB_REGULAR_REPORT_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Method to construct ivd event for verify otp
   *
   * @return EsbVehicleEvent
   */
  public static EsbVehicleEvent constructVerifyOtpEvent() {

    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("227")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Method to construct ivd event for change pin
   *
   * @return EsbVehicleEvent
   */
  public static EsbVehicleEvent constructChangePinEvent() {

    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("150")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Method to construct ivd event for vehicle mdt update
   *
   * @return EsbVehicleEvent
   */
  public static EsbVehicleEvent constructVehicleMdtUpdate(String eventId) {

    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier(eventId)
        .withMessage(
            "AC D3 FC C5 61 8C 64 70 02 2F 01 39 CA 94 EB 57 00 A4 00 01 00 03 31 30 2E 36 31 2E 38 2E 31 32 31 20 20 20 20")
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Method to construct ivd event for vehicle mdt update
   *
   * @return EsbVehicleEvent
   */
  public static EsbVehicleEvent constructInvalidVehicleMdtUpdate() {

    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("133")
        .withMessage("123")
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Method to construct ivd event for driver performance
   *
   * @return EsbVehicleEvent
   */
  public static EsbVehicleEvent constructDriverPerformance() {

    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("208")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbVehicleEvent constructReportTotalMileageEvent() {
    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier(VehicleCommAppConstant.REPORT_TOTAL_MILEAGE_EVENT_ID)
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static EsbVehicleEvent constructEmptyReportTotalMileageEvent() {

    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier(VehicleCommAppConstant.REPORT_TOTAL_MILEAGE_EVENT_ID)
        .withMessage(" ")
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Method to construct ivd event for Break Event
   *
   * @return EsbVehicleEvent
   */
  public static EsbVehicleEvent constructBreakEvent() {

    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("134")
        .withMessage(
            "86 73 09 C6 9D 8C 13 70 01 26 82 39 1E 73 E6 65 00 31 30 2E 36 31 2E 31 38 2E 32 31 35 20 20 20")
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Method to construct ivd event for Expressway Status Update event
   *
   * @return EsbVehicleEvent
   */
  public static EsbVehicleEvent constructExpresswayStatusUpdateEvent() {

    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("132")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Method to construct ivd event for Notify Static Gps Event
   *
   * @return EsbVehicleEvent
   */
  public static EsbVehicleEvent constructNotifyStaticGpsEvent() {

    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("157")
        .withMessage(
            "9D 4B 2A C6 79 8C 43 70 00 20 0B 00 AB 69 E6 65 00 31 30 2E 36 31 2E 35 38 2E 35 20 20 20 20 20")
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Method to construct ivd event for Forgot Password Update event
   *
   * @return EsbVehicleEvent
   */
  public static EsbVehicleEvent constructForgotPasswordEvent() {
    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("226")
        .withMessage(IntegrationTestConstants.FORGET_PASSWORD_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Method to construct ivd event for Forgot Password Update event with msg already processed
   *
   * @return EsbVehicleEvent
   */
  public static EsbVehicleEvent constructForgotPasswordMsgAlreadyProcessedEvent() {
    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("226")
        .withMessage(IntegrationTestConstants.FORGET_PASSWORD_MSG_ALREADY_PROCESSED_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /**
   * Method to construct ivd event for Forgot Password event
   *
   * @return EsbVehicleEvent
   */
  public static EsbVehicleEvent constructNormalForgotPasswordEvent() {
    return EsbVehicleEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier("226")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(ESB_JOB_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }
}
