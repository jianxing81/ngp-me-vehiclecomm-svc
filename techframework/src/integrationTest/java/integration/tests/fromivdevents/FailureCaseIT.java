package integration.tests.fromivdevents;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.configs.DomainEventByteArrayJsonSchemaMessageConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.esbjobevent.EsbJobEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ivdresponse.IvdResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.rcsaevent.RcsaEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.vehiclecommfailedrequest.VehicleCommFailedRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.IVDMessageType;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fare.client.apis.BookingPaymentsApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.apis.JobControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.apis.JobEventLogControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.apis.JobNoBlockControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.apis.MdtControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.vehicle.client.apis.VehicleManagementApi;
import integration.IntegrationTestBase;
import integration.constants.IntegrationTestConstants;
import integration.utils.EsbJobEventRequestUtility;
import integration.utils.MdtServiceResponseUtiltiy;
import integration.utils.RcsaEventRequestUtility;
import java.time.Duration;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.MessageSource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class FailureCaseIT extends IntegrationTestBase {

  private final KafkaTemplate<String, Object> kafkaProducer;
  private final DomainEventByteArrayJsonSchemaMessageConverter messageConverter;
  private final MessageSource messageSource;
  private final CacheManager cacheManager;

  @MockBean MdtControllerApi mdtControllerApi;
  @MockBean JobNoBlockControllerApi jobNoBlockControllerApi;
  @MockBean JobEventLogControllerApi jobEventLogControllerApi;
  @MockBean BookingPaymentsApi bookingPaymentsApi;
  @MockBean VehicleManagementApi vehicleManagementApi;
  @MockBean JobControllerApi jobControllerApi;

  @Value("${spring.kafka.vehcomm.topic.ivd_job_event}")
  String ivdJobEventTopic;

  @Value("${event2Topic.IvdResponse.name}")
  String jobEventIvdResponse;

  @Value("${event2Topic.VehicleCommFailedRequest.name}")
  String jobEventErrorOutTopic;

  @Value("${spring.kafka.vehcomm.topic.rcsa_event}")
  public String rcsaEventTopic;

  @AfterAll
  public void afterAll() {
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();
  }

  @Test
  void shouldThrowConstraintException() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructJobEventWithBlankMessage("154");
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.ESB_MESSAGE_REQUIRED.getCode().toString(), null, Locale.ENGLISH));
  }

  @Test
  void shouldThrowInternalServerError() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructJobEventForInvalidEventId();
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOutForInternalServerError();
  }

  @Test
  void shouldThrowApplicationException() {
    EsbJobEvent esbJobEvent =
        EsbJobEventRequestUtility.constructJobEventForAcknowledgeJobCancellation();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.ENGLISH));
  }

  @Test
  void shouldSuccessfulyMdtRequestEventCaseNetworkError() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructJobEventForMdtRequest();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    org.springframework.integration.support.MessageBuilder<EsbJobEvent> messageBuilder =
        org.springframework.integration.support.MessageBuilder.withPayload(esbJobEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());

    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.getDefault()));
  }

  @Test
  void shouldConsumeAdvanceJobRemindEventJobServiceThrowInternalServerError() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructAdvanceJobRemindEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    when(jobEventLogControllerApi.insertJobEventLog(any()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    org.springframework.integration.support.MessageBuilder<EsbJobEvent> messageBuilder =
        org.springframework.integration.support.MessageBuilder.withPayload(esbJobEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.JOB_DISPATCH_SERVICE_NETWORK_ERROR.getCode().toString(),
            null,
            Locale.getDefault()));
  }

  @Test
  void shouldConsumeAdvanceJobRemindEventMDTThrowInternalServerError() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructAdvanceJobRemindEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    org.springframework.integration.support.MessageBuilder<EsbJobEvent> messageBuilder =
        org.springframework.integration.support.MessageBuilder.withPayload(esbJobEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.getDefault()));
  }

  /**
   * This test is to check Fare Calculation Request Event when MDT service network error scenario
   */
  @Test
  void fareCalculationRequestWhenMdtServiceNetworkError() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructFareCalculationRequest();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    when(paymentMethodApi.getPaymentMethodList())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generatePaymentMethodResponse()));

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);

    kafkaProducer.send(messageBuilder.build());

    // verify
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.ENGLISH));
  }

  /**
   * This test is to check Fare Calculation Request Event when fare service network error scenario
   */
  @Test
  void fareCalculationRequestWhenFareServiceNetworkError() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructFareCalculationMessageRequest();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    when(bookingPaymentsApi.getTariffCalculation(any()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    when(paymentMethodApi.getPaymentMethodList())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generatePaymentMethodResponse()));

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);

    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.FARE_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.ENGLISH));
  }

  @Test
  void shouldProduceToKafkaErrorOutDueToNetworkErrorWhileCallingJobDispatchService() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructJobNumberBlockRequestEvent();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));
    when(mdtControllerApi.jobNoBlockRequest(any()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.JOB_DISPATCH_SERVICE_NETWORK_ERROR.getCode().toString(),
            null,
            Locale.ENGLISH));
  }

  @Test
  void shouldProduceToKafkaErrorOutDueToByteParseError() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructJobNumberBlockRequestEvent();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetails()));
    when(mdtControllerApi.jobNoBlockRequest(any()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.jobNoBlockAPIResponse()));

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.PARSING_ERROR.getCode().toString(), null, Locale.ENGLISH));
  }

  /** Job dispatch service Exception scenario in job accept event */
  @Test
  void shouldProduceToKafkaErrorOutDueToNetworkErrorWhileCallingJobDispatchServiceJobAcceptEvent() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructJobEventForAccept();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(jobControllerApi.handleDriverResponse(anyString(), any()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.JOB_DISPATCH_SERVICE_NETWORK_ERROR.getCode().toString(),
            null,
            Locale.ENGLISH));
  }

  /** Job dispatch service Exception scenario in Mdt Sync Request event */
  @Test
  void shouldProduceToKafkaErrorOutDueToNetworkErrorWhileCallingJobDispatchServiceMdtSyncRequest() {
    // arrange
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructJobEventForMdtRequest();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    when(jobControllerApi.getJobOfferByVehicleId(anyString(), anyString()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.JOB_DISPATCH_SERVICE_NETWORK_ERROR.getCode().toString(),
            null,
            Locale.ENGLISH));
  }

  @Test
  void shouldSuccessfullyProduceEmergencyReportEventNetworkError() {
    RcsaEvent rcsaEvent = RcsaEventRequestUtility.constructRcsaEventForEmergencyReport();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    MessageBuilder<RcsaEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaEvent).setHeader(KafkaHeaders.TOPIC, rcsaEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());

    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.VEHICLE_SERVICE_NETWORK_ERROR.getCode().toString(),
            null,
            Locale.getDefault()));
  }

  @Test
  void rcsaEmergencyInitiatedWithInvalidEmergencyId() {

    RcsaEvent rcsaEvent =
        RcsaEventRequestUtility.constructRcsaEventForEmergencyInitiatedWithInvalidEmergencyId();
    MessageBuilder<RcsaEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaEvent).setHeader(KafkaHeaders.TOPIC, rcsaEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.EMERGENCY_ID_REQUIRED.getCode().toString(), null, Locale.getDefault()));
  }

  private void validateKafkaErrorOut(String message) {
    KafkaConsumer<String, byte[]> consumer = getKafkaConsumer();
    consumer.subscribe(Collections.singletonList(jobEventErrorOutTopic));
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

  private void validateKafkaErrorOutForInternalServerError() {
    KafkaConsumer<String, byte[]> consumer = getKafkaConsumer();
    consumer.subscribe(Collections.singletonList(jobEventErrorOutTopic));
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
                    VehicleCommFailedRequest vehicleCommFailedRequest =
                        (VehicleCommFailedRequest)
                            messageConverter.extractAndConvertValue(
                                payload, VehicleCommFailedRequest.class);
                    assertThat(vehicleCommFailedRequest.getMessage())
                        .startsWith(IntegrationTestConstants.INTERNAL_SERVER_ERROR_GENERIC);
                  });
              return true;
            });
    consumer.close();
  }

  private void validateKafkaIvdResponse(String message) {
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
                    assertEquals(message, ivdResponse.getMessage());
                  });
              return true;
            });
    consumer.close();
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
}
