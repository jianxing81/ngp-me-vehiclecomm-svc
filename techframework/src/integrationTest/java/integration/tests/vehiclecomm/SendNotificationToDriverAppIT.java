package integration.tests.vehiclecomm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.configs.DomainEventByteArrayJsonSchemaMessageConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.notificationmessageevent.NotificationMessageEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.BadRequestError;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.NotificationRequest;
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
import org.springframework.http.HttpStatus;
import retrofit2.Response;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class SendNotificationToDriverAppIT extends integration.IntegrationTestBase {

  private final DomainEventByteArrayJsonSchemaMessageConverter messageConverter;

  @Value("${event2Topic.NotificationMessageEvent.name}")
  String notificationMessageResponse;

  @Test
  public void shouldReturn200WithSuccessfulNotificationToDriverApp() {

    // setup request
    NotificationRequest notificationRequest =
        new NotificationRequest().notificationType("JOB_CONFIRMED").placeHolder(objectMapper);
    try {
      // act
      Response<Void> response =
          vehicleCommControllerApi.sendNotification("SHA001", notificationRequest).execute();
      // verify
      Assertions.assertNotNull(response);
      Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), response.code());
      validateKafkaAppNotificationJobResponse(notificationRequest.getNotificationType());
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
  }

  @Test
  public void shouldReturn400BadRequestIfNotificationTypeAndPlaceHolderIsNull() {
    // setup request
    NotificationRequest notificationRequest =
        new NotificationRequest().notificationType(null).placeHolder(null);

    try {
      // act
      Response<Void> response =
          vehicleCommControllerApi.sendNotification("SHA001", notificationRequest).execute();
      // verify
      Assertions.assertNotNull(response);
      Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.code());
      Assertions.assertNotNull(response.errorBody());
      BadRequestError errorResponse =
          objectMapper.readValue(response.errorBody().string(), BadRequestError.class);
      Assertions.assertNotNull(errorResponse);

    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
  }

  private void validateKafkaAppNotificationJobResponse(String eventName) {
    KafkaConsumer<String, byte[]> consumer = getKafkaConsumer();
    consumer.subscribe(Collections.singletonList(notificationMessageResponse));
    Awaitility.await()
        .atMost(5, TimeUnit.SECONDS)
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
