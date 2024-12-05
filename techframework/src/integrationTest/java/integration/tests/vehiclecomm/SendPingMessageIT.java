package integration.tests.vehiclecomm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.configs.DomainEventByteArrayJsonSchemaMessageConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ivdresponse.IvdResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.apis.MdtControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.BadRequestError;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.IvdPingMessage;
import integration.helpers.general.DataHelper;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import retrofit2.Response;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class SendPingMessageIT extends integration.IntegrationTestBase {

  private final DomainEventByteArrayJsonSchemaMessageConverter messageConverter;

  @Value("${event2Topic.IvdResponse.name}")
  String ivdResponse;

  @MockBean MdtControllerApi mdtControllerApi;

  @Test
  public void shouldReturn204WithSuccessfulSendPingMessage() {

    // setup request
    IvdPingMessage ivdPingMessage =
        new IvdPingMessage()
            .refNo(DataHelper.generateRandomIntegers(20))
            .seqNo(DataHelper.generateRandomIntegers(20))
            .ipAddress(DataHelper.generateRandomIPv4())
            .ivdNo(DataHelper.generateRandomIntegers(1028));
    try {
      // act
      when(mdtControllerApi.mdtIvdPingUpdate(
              any(
                  com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models
                      .IvdPingUpdateRequest.class)))
          .thenReturn(Mono.empty());
      Response<Void> response = vehicleCommControllerApi.sendPingMessage(ivdPingMessage).execute();
      // verify
      Assertions.assertNotNull(response);
      Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), response.code());
      validateKafkaIvdResponse(ivdPingMessage.getIvdNo());

    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
  }

  /**
   * validateKafkaIvdResponse
   *
   * @param message message
   */
  private void validateKafkaIvdResponse(Integer message) {
    KafkaConsumer<String, byte[]> consumer = getKafkaConsumer();
    consumer.subscribe(Collections.singletonList(ivdResponse));
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
                    IvdResponse ivdResponse =
                        (IvdResponse)
                            messageConverter.extractAndConvertValue(payload, IvdResponse.class);
                    assertEquals(message, ivdResponse.getIvdNo());
                  });
              return true;
            });
    consumer.close();
  }

  @Test
  public void shouldReturn400BadRequestIfIvdPingMessageIsNull() {
    // setup request
    IvdPingMessage ivdPingMessage =
        new IvdPingMessage().refNo(null).seqNo(null).ipAddress(null).ivdNo(null);

    try {
      // act
      Response<Void> response = vehicleCommControllerApi.sendPingMessage(ivdPingMessage).execute();

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
}
