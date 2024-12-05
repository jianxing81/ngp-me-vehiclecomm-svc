package integration.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.rcsaevent.RcsaEvent;
import integration.constants.IntegrationTestConstants;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RcsaEventRequestUtility {

  private static final String RCSA_EVENT_FIELD = "RcsaEvent";

  public static RcsaEvent constructRcsaEventForVoiceStreaming() {

    return RcsaEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventIdentifier("187")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(RCSA_EVENT_FIELD)
        .withEventDate(LocalDateTime.now())
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static RcsaEvent constructRcsaEventForFalseAlarm() {

    return RcsaEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventIdentifier("186")
        .withMessage(
            "BA 99 81 C6 9B 8C 47 70 00 20 0B 00 EA EB F3 65 00 29 00 10 00 45 4D 31 35 30 33 32 30 32 34 31 34 33 34 31 35 34 00 01 00 02 31 30 2E 36 31 2E 36 31 2E 33 32 20 20 20 20")
        .withEventType(RCSA_EVENT_FIELD)
        .withEventDate(LocalDateTime.now())
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static RcsaEvent constructRcsaEventForEmergencyReport() {

    return RcsaEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventIdentifier("164")
        .withMessage(IntegrationTestConstants.EMERGENCY_REPORT_BYTE_DATA_INPUT)
        .withEventType(RCSA_EVENT_FIELD)
        .withEventDate(LocalDateTime.now())
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static RcsaEvent constructRcsaEventForEmergencyInitiated() {

    return RcsaEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventIdentifier("185")
        .withMessage(
            "BA 99 81 C6 9B 8C 47 70 00 20 0B 00 EA EB F3 65 00 29 00 10 00 45 4D 31 35 30 33 32 30 32 34 31 34 33 34 31 35 34 00 01 00 02 31 30 2E 36 31 2E 36 31 2E 33 32 20 20 20 20")
        .withEventType(RCSA_EVENT_FIELD)
        .withEventDate(LocalDateTime.now())
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static RcsaEvent constructRcsaEventForEmergencyInitiatedWithInvalidEmergencyId() {

    return RcsaEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventIdentifier("185")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(RCSA_EVENT_FIELD)
        .withEventDate(LocalDateTime.now())
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  /** rcsa event for RESPOND_TO_STRUCTURE_MESSAGE */
  public static RcsaEvent constructRcsaEventForrespondToStructureMessage() {

    return RcsaEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventIdentifier("167")
        .withMessage(IntegrationTestConstants.RESPOND_TO_STRUCTURE_MESSAGE)
        .withEventType(RCSA_EVENT_FIELD)
        .withEventDate(LocalDateTime.now())
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static RcsaEvent constructToResponseToSimpleMassage() {

    return RcsaEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventIdentifier("166")
        .withMessage(IntegrationTestConstants.RESPONSE_TO_SIMPLE_MESSAGE)
        .withEventType(RCSA_EVENT_FIELD)
        .withEventDate(LocalDateTime.now())
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static RcsaEvent constructRcsaEventForSendMessage() {

    return RcsaEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventIdentifier("165")
        .withMessage(IntegrationTestConstants.GENERIC_BYTE_DATA_INPUT)
        .withEventType(RCSA_EVENT_FIELD)
        .withEventDate(LocalDateTime.now())
        .withOccurredAt(LocalDateTime.now())
        .build();
  }

  public static RcsaEvent constructIVDEventForMsgAlreadyProcessed(String eventId) {
    return RcsaEvent.builder()
        .withEventId(UUID.randomUUID())
        .withEventDate(LocalDateTime.now())
        .withEventIdentifier(eventId)
        .withMessage(
            "DB DC 7F C6 65 8C 3F 70 00 22 80 00 BA 9F EA 65 00 31 00 04 00 D1 07 00 00 BE 00 01 00 32 31 30 2E 36 31 2E 32 36 2E 32 33 39 20 20 20")
        .withEventType(RCSA_EVENT_FIELD)
        .withOccurredAt(LocalDateTime.now())
        .build();
  }
}
