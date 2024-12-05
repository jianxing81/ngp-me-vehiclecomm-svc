package integration.tests.vehiclecomm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.configs.DomainEventByteArrayJsonSchemaMessageConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ivdresponse.IvdResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.rcsamessageevent.RcsaMessageEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.vehiclecommfailedrequest.VehicleCommFailedRequest;
import integration.IntegrationTestBase;
import integration.utils.RcsaMessageEventUtility;
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
import org.springframework.context.MessageSource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class RcsaMessageIT extends IntegrationTestBase {

  private final KafkaTemplate<String, Object> kafkaProducer;

  private final MessageSource messageSource;

  private final DomainEventByteArrayJsonSchemaMessageConverter messageConverter;

  @Value("${spring.kafka.vehcomm.topic.rcsa_message_event}")
  String rcsaMessageEventTopic;

  @Value("${event2Topic.IvdResponse.name}")
  String jobEventIvdResponse;

  @Value("${event2Topic.VehicleCommFailedRequest.name}")
  String jobEventErrorOutTopic;

  @Test
  void successfulRcsaMessageStructure() {

    RcsaMessageEvent rcsaMessageEvent =
        RcsaMessageEventUtility.constructRcsaMessageEventStructure();
    MessageBuilder<RcsaMessageEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaMessageEvent)
            .setHeader(KafkaHeaders.TOPIC, rcsaMessageEventTopic);

    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(rcsaMessageEvent.getIvdNo());
  }

  @Test
  void successfulRcsaMessageSimple() {

    RcsaMessageEvent rcsaMessageEvent = RcsaMessageEventUtility.constructRcsaMessageEventSimple();
    MessageBuilder<RcsaMessageEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaMessageEvent)
            .setHeader(KafkaHeaders.TOPIC, rcsaMessageEventTopic);

    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(rcsaMessageEvent.getIvdNo());
  }

  @Test
  void successfulRcsaMessage() {

    RcsaMessageEvent rcsaMessageEvent = RcsaMessageEventUtility.constructRcsaMessageEvent();
    MessageBuilder<RcsaMessageEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaMessageEvent)
            .setHeader(KafkaHeaders.TOPIC, rcsaMessageEventTopic);

    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(rcsaMessageEvent.getIvdNo());
  }

  @Test
  void successfulRcsaMessageForCommandTypeOne() {

    RcsaMessageEvent rcsaMessageEvent = RcsaMessageEventUtility.constructRcsaMessageEventForOne();
    MessageBuilder<RcsaMessageEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaMessageEvent)
            .setHeader(KafkaHeaders.TOPIC, rcsaMessageEventTopic);

    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(rcsaMessageEvent.getIvdNo());
  }

  @Test
  void successfulRcsaMessageForThree() {

    RcsaMessageEvent rcsaMessageEvent = RcsaMessageEventUtility.constructRcsaMessageEventForThree();
    MessageBuilder<RcsaMessageEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaMessageEvent)
            .setHeader(KafkaHeaders.TOPIC, rcsaMessageEventTopic);

    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(rcsaMessageEvent.getIvdNo());
  }

  @Test
  void successfulRcsaMessageForFour() {

    RcsaMessageEvent rcsaMessageEvent = RcsaMessageEventUtility.constructRcsaMessageEventForFour();
    MessageBuilder<RcsaMessageEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaMessageEvent)
            .setHeader(KafkaHeaders.TOPIC, rcsaMessageEventTopic);

    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(rcsaMessageEvent.getIvdNo());
  }

  @Test
  void successfulRcsaMessageForFive() {

    RcsaMessageEvent rcsaMessageEvent = RcsaMessageEventUtility.constructRcsaMessageEventForFive();
    MessageBuilder<RcsaMessageEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaMessageEvent)
            .setHeader(KafkaHeaders.TOPIC, rcsaMessageEventTopic);

    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(rcsaMessageEvent.getIvdNo());
  }

  @Test
  void successfulRcsaMessageForCommandTypeSix() {

    RcsaMessageEvent rcsaMessageEvent = RcsaMessageEventUtility.constructRcsaMessageEventForSix();
    MessageBuilder<RcsaMessageEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaMessageEvent)
            .setHeader(KafkaHeaders.TOPIC, rcsaMessageEventTopic);

    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(rcsaMessageEvent.getIvdNo());
  }

  @Test
  void successfulRcsaMessageForDeviceTypeMdtAndMessageTypeNull() {

    RcsaMessageEvent rcsaMessageEvent =
        RcsaMessageEventUtility.constructRcsaMessageEventForDeviceType();
    MessageBuilder<RcsaMessageEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaMessageEvent)
            .setHeader(KafkaHeaders.TOPIC, rcsaMessageEventTopic);

    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(rcsaMessageEvent.getIvdNo());
  }

  @Test
  void errorOutRcsaMessage() {

    RcsaMessageEvent rcsaMessageEvent = RcsaMessageEventUtility.constructRcsaMessageEventErrorOut();
    MessageBuilder<RcsaMessageEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaMessageEvent)
            .setHeader(KafkaHeaders.TOPIC, rcsaMessageEventTopic);

    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.INVALID_MESSAGE_TYPE.getCode().toString(), null, Locale.getDefault()));
  }

  @Test
  void errorOutRcsaMessageInvalidMessageType() {

    RcsaMessageEvent rcsaMessageEvent =
        RcsaMessageEventUtility.constructRcsaMessageEventErrorOutInvalidMessageType();
    MessageBuilder<RcsaMessageEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaMessageEvent)
            .setHeader(KafkaHeaders.TOPIC, rcsaMessageEventTopic);

    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.INVALID_MESSAGE_TYPE.getCode().toString(), null, Locale.getDefault()));
  }

  private void validateKafkaIvdJobResponse(Integer eventName) {
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
                    assertEquals(eventName, ivdResponse.getIvdNo());
                  });
              return true;
            });
    consumer.close();
  }

  private void validateKafkaErrorOut(String message) {
    KafkaConsumer<String, byte[]> consumer = getKafkaConsumer();
    consumer.subscribe(Collections.singletonList(jobEventErrorOutTopic));
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
}
