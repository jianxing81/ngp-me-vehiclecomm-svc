package integration.tests.vehiclecomm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.configs.DomainEventByteArrayJsonSchemaMessageConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ivdresponse.IvdResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.notificationmessageevent.NotificationMessageEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.vehiclecommfailedrequest.VehicleCommFailedRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.vehicleevent.VehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.apis.MdtControllerApi;
import integration.IntegrationTestBase;
import integration.utils.MdtServiceResponseUtiltiy;
import integration.utils.VehicleEventRequestUtility;
import java.time.Duration;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class VehicleEventMessageIT extends IntegrationTestBase {
  private final KafkaTemplate<String, Object> kafkaProducer;

  private final MessageSource messageSource;

  private final DomainEventByteArrayJsonSchemaMessageConverter messageConverter;

  @MockBean MdtControllerApi mdtControllerApi;

  @Value("${event2Topic.VehicleCommFailedRequest.name}")
  String vehicleEventErrorOutTopic;

  @Value("${event2Topic.IvdResponse.name}")
  String vehicleEventIvdResponse;

  @Value("${spring.kafka.vehcomm.topic.vehicle_event}")
  String vehicleEventTopic;

  @Value("${event2Topic.NotificationMessageEvent.name}")
  String notifyResponse;

  @Test
  void shouldSuccessfullySynchronizedAutoBidStatusForMdt() {
    // arrange
    VehicleEvent vehicleEvent =
        VehicleEventRequestUtility.constructVehicleMessageEventStructureForMdt();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    MessageBuilder<VehicleEvent> messageBuilder =
        MessageBuilder.withPayload(vehicleEvent).setHeader(KafkaHeaders.TOPIC, vehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdResponseForIvdNo(vehicleEvent.getIvdNo());
  }

  @Test
  void shouldSuccessfullySynchronizedAutoBidStatusForIphone() {
    // arrange
    VehicleEvent vehicleEvent =
        VehicleEventRequestUtility.constructVehicleMessageEventStructureForIphone();
    MessageBuilder<VehicleEvent> messageBuilder =
        MessageBuilder.withPayload(vehicleEvent).setHeader(KafkaHeaders.TOPIC, vehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaAppNotificationJobResponse("AUTOBID_STATUS");
  }

  @Test
  void shouldSuccessfullyAppStatusSyncForAndroid() {
    // arrange
    VehicleEvent vehicleEvent = VehicleEventRequestUtility.constructAppStatusSyncEventForAndroid();
    MessageBuilder<VehicleEvent> messageBuilder =
        MessageBuilder.withPayload(vehicleEvent).setHeader(KafkaHeaders.TOPIC, vehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaAppNotificationJobResponse("MDT_COMPLETED");
  }

  @Test
  void shouldProduceToKafkaErrorOutDueToNetworkErrorWhileCallingMdtService() {
    // arrange
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenThrow(
            new WebClientResponseException(500, "Internal Server Exception", null, null, null));

    VehicleEvent vehicleEvent =
        VehicleEventRequestUtility.constructVehicleMessageEventStructureForMdt();
    MessageBuilder<VehicleEvent> messageBuilder =
        MessageBuilder.withPayload(vehicleEvent).setHeader(KafkaHeaders.TOPIC, vehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());

    // verify
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.getDefault()));
  }

  private void validateKafkaIvdResponseForIvdNo(Integer ivdNo) {
    KafkaConsumer<String, byte[]> consumer = getKafkaConsumer();
    consumer.subscribe(Collections.singletonList(vehicleEventIvdResponse));
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(500));
              if (records.isEmpty()) {
                return false;
              }
              records.forEach(
                  payload -> {
                    IvdResponse ivdResponse =
                        (IvdResponse)
                            messageConverter.extractAndConvertValue(payload, IvdResponse.class);
                    assertEquals(ivdNo, ivdResponse.getIvdNo());
                  });
              return true;
            });
    consumer.close();
  }

  private void validateKafkaErrorOut(String message) {
    KafkaConsumer<String, byte[]> consumer = getKafkaConsumer();
    consumer.subscribe(Collections.singletonList(vehicleEventErrorOutTopic));
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(500));
              if (records.isEmpty()) {
                return false;
              }
              records.forEach(
                  payload -> {
                    VehicleCommFailedRequest vehicleCommFailedRequest =
                        (VehicleCommFailedRequest)
                            messageConverter.extractAndConvertValue(
                                payload, VehicleCommFailedRequest.class);
                    assertEquals(message, vehicleCommFailedRequest.getMessage());
                  });
              return true;
            });
    consumer.close();
  }

  private void validateKafkaAppNotificationJobResponse(String eventName) {
    KafkaConsumer<String, byte[]> consumer = getKafkaConsumer();
    consumer.subscribe(Collections.singletonList(notifyResponse));
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(1000));
              if (records.isEmpty()) {
                return false;
              }
              records.forEach(
                  payload -> {
                    NotificationMessageEvent notificationMessageEvent =
                        (NotificationMessageEvent)
                            messageConverter.extractAndConvertValue(
                                payload, NotificationMessageEvent.class);
                    assertEquals(eventName, notificationMessageEvent.getTemplate().getEventType());
                  });
              return true;
            });
    consumer.close();
  }
}
