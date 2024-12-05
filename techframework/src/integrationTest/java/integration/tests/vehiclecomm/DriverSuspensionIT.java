package integration.tests.vehiclecomm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.configs.DomainEventByteArrayJsonSchemaMessageConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.driverevent.DriverEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ivdresponse.IvdResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.notificationmessageevent.NotificationMessageEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.apis.MdtControllerApi;
import integration.IntegrationTestBase;
import integration.utils.DriverEventUtility;
import integration.utils.MdtServiceResponseUtiltiy;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class DriverSuspensionIT extends IntegrationTestBase {

  private final KafkaTemplate<String, Object> kafkaProducer;

  private final DomainEventByteArrayJsonSchemaMessageConverter messageConverter;

  @MockBean MdtControllerApi mdtControllerApi;

  @Value("${event2Topic.IvdResponse.name}")
  String jobEventIvdResponse;

  @Value("${event2Topic.VehicleCommFailedRequest.name}")
  String driverEventErrorOutTopic;

  @Value("${spring.kafka.vehcomm.topic.driver_event}")
  String driverEventTopic;

  @Value("${event2Topic.NotificationMessageEvent.name}")
  String notifyResponse;

  @Test
  void successfulAutoAcceptFlagTrue() {

    DriverEvent driverEvent = DriverEventUtility.constructDriverEventAutoAcceptTrue();
    MessageBuilder<DriverEvent> messageBuilder =
        MessageBuilder.withPayload(driverEvent).setHeader(KafkaHeaders.TOPIC, driverEventTopic);

    kafkaProducer.send(messageBuilder.build());
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    validateKafkaIvdResponse(12345);
  }

  @Test
  void successfulAutoAcceptFlagTrueCommandTypeFive() {

    DriverEvent driverEvent =
        DriverEventUtility.constructDriverEventAutoAcceptTrueCommandTypeFive();
    MessageBuilder<DriverEvent> messageBuilder =
        MessageBuilder.withPayload(driverEvent).setHeader(KafkaHeaders.TOPIC, driverEventTopic);

    kafkaProducer.send(messageBuilder.build());
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    validateKafkaIvdResponse(12345);
  }

  @Test
  void successfulAutoAcceptBidTrue() {

    DriverEvent driverEvent = DriverEventUtility.constructDriverEventAutoBidTrue();
    MessageBuilder<DriverEvent> messageBuilder =
        MessageBuilder.withPayload(driverEvent).setHeader(KafkaHeaders.TOPIC, driverEventTopic);

    kafkaProducer.send(messageBuilder.build());
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    validateKafkaIvdResponse(12345);
  }

  @Test
  void successfulAutoAcceptBidTrueIphone() {

    DriverEvent driverEvent = DriverEventUtility.constructDriverEventAutoAcceptTrueIphone();
    MessageBuilder<DriverEvent> messageBuilder =
        MessageBuilder.withPayload(driverEvent).setHeader(KafkaHeaders.TOPIC, driverEventTopic);

    kafkaProducer.send(messageBuilder.build());
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    validateKafkaAppNotification("DRIVER_SUSPEND");
  }

  @Test
  void successfulAutoAcceptBidTrueIphoneCommandTypeFive() {

    DriverEvent driverEvent =
        DriverEventUtility.constructDriverEventAutoAcceptTrueIphoneCommandTypeFive();
    MessageBuilder<DriverEvent> messageBuilder =
        MessageBuilder.withPayload(driverEvent).setHeader(KafkaHeaders.TOPIC, driverEventTopic);

    kafkaProducer.send(messageBuilder.build());
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    validateKafkaAppNotification("DRIVER_SUSPEND");
  }

  @Test
  void successfulAutoBidTrueIphoneCommandTypeFive() {

    DriverEvent driverEvent =
        DriverEventUtility.constructDriverEventAutoBidTrueIphoneCommandTypeFive();
    MessageBuilder<DriverEvent> messageBuilder =
        MessageBuilder.withPayload(driverEvent).setHeader(KafkaHeaders.TOPIC, driverEventTopic);

    kafkaProducer.send(messageBuilder.build());
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    validateKafkaAppNotification("DRIVER_SUSPEND");
  }

  private void validateKafkaIvdResponse(Integer ivdNo) {
    KafkaConsumer<String, byte[]> consumer = getKafkaConsumer();
    consumer.subscribe(Collections.singletonList(jobEventIvdResponse));
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
                    Assertions.assertEquals(ivdNo, ivdResponse.getIvdNo());
                  });
              return true;
            });
    consumer.close();
  }

  private void validateKafkaAppNotification(String eventName) {
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
