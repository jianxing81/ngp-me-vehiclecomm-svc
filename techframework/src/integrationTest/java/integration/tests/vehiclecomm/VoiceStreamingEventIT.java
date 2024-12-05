package integration.tests.vehiclecomm;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.configs.DomainEventByteArrayJsonSchemaMessageConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ivdresponse.IvdResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.BadRequestError;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.VoiceEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.VoiceStreamEnum;
import integration.IntegrationTestBase;
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
import org.springframework.http.HttpStatus;
import retrofit2.Response;

/** Spring Integration Testing for Voice Streaming Event API */
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class VoiceStreamingEventIT extends IntegrationTestBase {
  @Value("${event2Topic.IvdResponse.name}")
  String jobEventIvdResponse;

  private final DomainEventByteArrayJsonSchemaMessageConverter messageConverter;

  @Test
  public void shouldReturn200WithSuccessfulForStartVoiceStreamingApi() {

    // setup request
    VoiceEventRequest voiceEventRequest =
        new VoiceEventRequest().duration(2).id(1).ipAddress("10.2.140.40").ivdNo(0);
    try {
      // act
      Response<Void> response =
          vehicleCommControllerApi
              .sendVoiceStreaming(VoiceStreamEnum.START, voiceEventRequest)
              .execute();
      // verify
      Assertions.assertNotNull(response);
      Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), response.code());
      validateKafkaIvdResponse(voiceEventRequest.getIvdNo());
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
  }

  @Test
  public void shouldReturn200WithSuccessfulForStopVoiceStreamingApi() {

    // setup request
    VoiceEventRequest voiceEventRequest =
        new VoiceEventRequest().duration(5).id(2).ipAddress("10.2.140.40").ivdNo(12);
    try {
      // act
      Response<Void> response =
          vehicleCommControllerApi
              .sendVoiceStreaming(VoiceStreamEnum.STOP, voiceEventRequest)
              .execute();
      // verify
      Assertions.assertNotNull(response);
      Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), response.code());
      validateKafkaIvdResponse(voiceEventRequest.getIvdNo());
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
  }

  /** required field validation */
  @Test
  public void shouldReturn400BadRequestIfRequestBodyInvalid() {
    // setup request
    VoiceEventRequest voiceEventRequest =
        new VoiceEventRequest().duration(2).id(1).ipAddress(null).ivdNo(12);

    try {
      // act
      Response<Void> response =
          vehicleCommControllerApi
              .sendVoiceStreaming(VoiceStreamEnum.START, voiceEventRequest)
              .execute();
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

  /**
   * validateKafkaIvdResponse
   *
   * @param ivdNo ivdNo
   */
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
}
