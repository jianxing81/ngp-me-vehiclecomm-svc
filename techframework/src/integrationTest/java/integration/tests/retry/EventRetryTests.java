package integration.tests.retry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.configs.DomainEventByteArrayJsonSchemaMessageConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.esbjobevent.EsbJobEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ivdresponse.IvdResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.tripupload.UploadTripEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.vehiclecommfailedrequest.VehicleCommFailedRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.IVDMessageType;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.entities.SchedulerEntity;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.enums.RetryStatus;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.repositories.SchedulerRepository;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.apis.MdtControllerApi;
import integration.IntegrationTestBase;
import integration.constants.IntegrationTestConstants;
import integration.utils.EsbJobEventRequestUtility;
import integration.utils.JobDispatchServiceUtility;
import integration.utils.MdtServiceResponseUtiltiy;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.MessageSource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/*
 This class is used to test the retry flow on a whole.
 1) There should not be any entry in retry table, if the event is not part of retryable events enum
 2) Whenever there is a network exception while calling MDT service, an entry should be made in retry table
 3) When the retry API is then called, it should process the entry made in the previous step. Upload trip message
    should be successfully produced and the entry in the retry table should be deleted
*/
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EventRetryTests extends IntegrationTestBase {

  private final KafkaTemplate<String, Object> kafkaProducer;
  private final DomainEventByteArrayJsonSchemaMessageConverter messageConverter;
  private final MessageSource messageSource;
  private final SchedulerRepository schedulerRepository;
  private final CacheManager cacheManager;

  @Value("${spring.kafka.vehcomm.topic.ivd_job_event}")
  String ivdJobEventTopic;

  @Value("${event2Topic.VehicleCommFailedRequest.name}")
  String jobEventErrorOutTopic;

  @Value("${event2Topic.IvdResponse.name}")
  String jobEventIvdResponse;

  @Value("${event2Topic.UploadTripEvent.name}")
  String uploadTripTopic;

  @MockBean MdtControllerApi mdtControllerApi;

  @BeforeAll
  @AfterAll
  void beforeAllTests() {
    schedulerRepository.deleteAll();
  }

  @AfterEach
  void afterEach() {
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();
  }

  @Test
  @Order(1)
  @Sql(
      statements =
          "ALTER SEQUENCE ngp_me_vehiclecomm.SEQ_vehicle_comm_scheduler__vehicle_comm_scheduler_id RESTART WITH 1;")
  void
      shouldProduceToErrorOutButShouldNotInsertIntoSchedulerRetryTableDueToAbsenceOfEventIdInRetryableEvents() {
    try {
      EsbJobEvent esbJobEvent =
          EsbJobEventRequestUtility.constructJobEventForAcknowledgeJobCancellation();
      when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
          .thenThrow(
              new WebClientResponseException(
                  500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));
      MessageBuilder<EsbJobEvent> messageBuilder =
          MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
      kafkaProducer.send(messageBuilder.build());

      // Check if a message is available in error out and the message should be MDT service network
      // error
      validateKafkaErrorOut(
          messageSource.getMessage(
              ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.ENGLISH));

      // Check if an entry is available in scheduler table
      Optional<SchedulerEntity> schedulerEntityOptional = schedulerRepository.findById(1L);
      assertTrue(schedulerEntityOptional.isEmpty());
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
  }

  @Test
  @Order(2)
  void shouldProduceToErrorOutAndInsertIntoSchedulerRetryTable() {
    try {
      EsbJobEvent esbJobEvent =
          EsbJobEventRequestUtility.constructJobEventForReportTripInfoNormal();
      when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
          .thenThrow(
              new WebClientResponseException(
                  500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));
      MessageBuilder<EsbJobEvent> messageBuilder =
          MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);

      IntStream.range(0, 2)
          .forEach(
              i -> {
                kafkaProducer.send(messageBuilder.build());
                // Check if a message is available in IVD response
                validateKafkaIvdAckResponse(
                    String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));

                // Check if a message is available in error out and the message should be MDT
                // service network
                // error
                validateKafkaErrorOut(
                    messageSource.getMessage(
                        ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(),
                        null,
                        Locale.ENGLISH));

                // Clear entries available in store and forward cache
                Objects.requireNonNull(
                        cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
                    .clear();
                Objects.requireNonNull(
                        cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
                    .clear();
              });

      List<SchedulerEntity> schedulerEntityList = schedulerRepository.findAll();
      assertEquals(2, schedulerEntityList.size());

    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
  }

  @Test
  @Order(3)
  @Sql(
      statements =
          """
      UPDATE ngp_me_vehiclecomm.vehicle_comm_scheduler
      SET created_dt = created_dt - INTERVAL '49 hours'
      WHERE ID = 2;
      """,
      executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
  void shouldSuccessfullyProcessTheRetryRecords() {
    try {
      when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
          .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));
      when(bookingControllerApi.getBookingProducts())
          .thenReturn(Mono.just(EsbJobEventRequestUtility.generateBookingProductResponse()));
      when(paymentMethodApi.getPaymentMethodList())
          .thenReturn(Mono.just(JobDispatchServiceUtility.paymentMethodResponse()));

      /* Hit retry endpoint. At this point, records which are long PENDING should be deleted and the others should be
      processed and marked SUCCESS */
      retryControllerApi.processRetry("REPORT_TRIP_INFO_NORMAL").execute();

      // Check if a message is available in IVD response
      validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));

      // Check if a message is available in upload trip topic
      validateKafkaUploadTripMessage();

      // Check if an entry is available in scheduler table for ID 1 and it should be success
      Optional<SchedulerEntity> schedulerEntityOptional = schedulerRepository.findById(1L);
      assertTrue(schedulerEntityOptional.isPresent());
      schedulerEntityOptional.ifPresent(
          entity -> assertEquals(RetryStatus.SUCCESS, entity.getStatus()));

      // Check if an entry is available in scheduler table for ID 2. It should not be available
      schedulerEntityOptional = schedulerRepository.findById(2L);
      assertTrue(schedulerEntityOptional.isEmpty());

      // Hit retry endpoint. At this point, the record which is marked SUCCESS should be deleted
      retryControllerApi.processRetry("REPORT_TRIP_INFO_NORMAL").execute();

      // Check if an entry is available in scheduler table for ID 1. It should not be available
      Awaitility.await()
          .atMost(3, TimeUnit.SECONDS)
          .pollInterval(500, TimeUnit.MILLISECONDS)
          .untilAsserted(() -> assertTrue(schedulerRepository.findById(1L).isEmpty()));

    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
  }

  private void validateKafkaIvdAckResponse(String eventIdentifier) {
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
                    assertEquals(eventIdentifier, ivdResponse.getEventIdentifier());
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

  private void validateKafkaUploadTripMessage() {
    KafkaConsumer<String, byte[]> consumer = getKafkaConsumer();
    consumer.subscribe(Collections.singletonList(uploadTripTopic));
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
                    UploadTripEvent uploadTrip =
                        (UploadTripEvent)
                            messageConverter.extractAndConvertValue(payload, UploadTripEvent.class);
                    assertEquals("5000514482", uploadTrip.getJobNo());
                    assertEquals("0656753", uploadTrip.getDriverId());
                    assertEquals("030809572", uploadTrip.getTripId());
                    assertEquals("STD001", uploadTrip.getProductId());
                  });
              return true;
            });
    consumer.close();
  }
}
