package integration.tests.vehiclecomm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.configs.DomainEventByteArrayJsonSchemaMessageConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ivdresponse.IvdResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.BadRequestError;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.VehicleTrackEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.VehicleTrackRequest;
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

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class VehicleTrackEventIT extends IntegrationTestBase {
  @Value("${event2Topic.IvdResponse.name}")
  String jobEventIvdResponse;

  private final DomainEventByteArrayJsonSchemaMessageConverter messageConverter;

  @Test
  public void shouldReturn200WithSuccessfulVehicleTrackingApi() {

    // setup request
    VehicleTrackRequest vehicleTrackRequest =
        new VehicleTrackRequest().duration(2).id(1).ipAddress("10.2.140.40").interval(2).ivdNo(0);
    try {
      // act
      Response<Void> response =
          vehicleCommControllerApi
              .sendVehicleTrackingByEvent(VehicleTrackEnum.START, vehicleTrackRequest)
              .execute();
      // verify
      Assertions.assertNotNull(response);
      Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), response.code());
      validateKafkaIvdResponse(vehicleTrackRequest.getIvdNo());
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
  }

  @Test
  public void shouldReturn200WithSuccessfulForStopVehicleTrackingApi() {

    // setup request
    VehicleTrackRequest vehicleTrackRequest =
        new VehicleTrackRequest().duration(2).id(1).ipAddress("10.2.140.40").interval(2).ivdNo(0);
    try {
      // act
      Response<Void> response =
          vehicleCommControllerApi
              .sendVehicleTrackingByEvent(VehicleTrackEnum.STOP, vehicleTrackRequest)
              .execute();
      // verify
      Assertions.assertNotNull(response);
      Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), response.code());
      validateKafkaIvdResponse(vehicleTrackRequest.getIvdNo());
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
  }

  /** required field validation */
  @Test
  public void shouldReturn400BadRequestIfNotificationTypeAndPlaceHolderIsNull() {
    // setup request
    VehicleTrackRequest vehicleTrackRequest =
        new VehicleTrackRequest().duration(2).id(1).ipAddress(null).interval(2).ivdNo(12);

    try {
      // act
      Response<Void> response =
          vehicleCommControllerApi
              .sendVehicleTrackingByEvent(VehicleTrackEnum.START, vehicleTrackRequest)
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
                    assertEquals(ivdNo, ivdResponse.getIvdNo());
                  });
              return true;
            });
    consumer.close();
  }
}
