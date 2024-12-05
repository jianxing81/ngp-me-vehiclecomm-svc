package integration.tests.fromivdevents;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cdg.pmg.ngp.me.vehiclecomm.application.constants.VehicleCommAppConstant;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.configs.DomainEventByteArrayJsonSchemaMessageConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.esbjobevent.EsbJobEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ivdresponse.IvdResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.jobdispatchevent.JobDispatchEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.producercsaevent.ProduceRcsaEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.rcsaevent.RcsaEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.tripupload.UploadTripEvent;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
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
import reactor.core.publisher.Mono;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class SuccessCaseIT extends IntegrationTestBase {

  private final KafkaTemplate<String, Object> kafkaProducer;
  private final DomainEventByteArrayJsonSchemaMessageConverter messageConverter;
  private final CacheManager cacheManager;
  private final MessageSource messageSource;
  private static final String STORE_FORWARD_CACHE_KEY = "1545068493";
  private static final String STORE_FORWARD_CACHE_KEY_FOR_NO_SHOW = "1595084580";
  private static final String STORE_FORWARD_CACHE_KEY_FOR_EMERGENCY_REPORT = "1645070824";
  private static final String ADVANCED_JOB_REMIND_EVENT_STORE_FORWARD_CACHE_KEY = "1695002528";

  private static final String RCSA_FALSE_ALARAM_STORE_FORWARD_CACHE_KEY = "1865081725";
  private static final String RCSA_RESPOND_TO_STRUCTURE = "16763058118";
  private static final String RCSA_RESPONSE_TO_SIMPLE_MESSAGE_STORE_FORWARD_CACHE_KEY =
      "1665069948";
  private static final String STORE_FORWARD_CACHE_KEY_FOR_MESSAGE_ACK = "35084553";
  private static final String REDUNDANT_MESSAGE_CACHE_KEY = "219null";

  @MockBean MdtControllerApi mdtControllerApi;
  @MockBean JobNoBlockControllerApi jobNoBlockControllerApi;
  @MockBean JobEventLogControllerApi jobEventLogControllerApi;
  @MockBean JobControllerApi jobControllerApi;
  @MockBean VehicleManagementApi vehicleManagementApi;
  @MockBean BookingPaymentsApi bookingPaymentsApi;

  @Value("${spring.kafka.vehcomm.topic.ivd_job_event}")
  String ivdJobEventTopic;

  @Value("${spring.kafka.vehcomm.topic.rcsa_event}")
  public String rcsaEventTopic;

  @Value("${event2Topic.IvdResponse.name}")
  String jobEventIvdResponse;

  @Value("${event2Topic.ProduceRcsaEvent.name}")
  String produceRcsaEventResponse;

  @Value("${event2Topic.UploadTripEvent.name}")
  String uploadTripTopic;

  @Value("${spring.kafka.vehcomm.topic.job_event}")
  String jobEventTopic;

  @Value("${event2Topic.VehicleCommFailedRequest.name}")
  String jobEventErrorOutTopic;

  @AfterEach
  void afterAll() {
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();
  }

  @Test
  void jobAccept() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructJobEventForAccept();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(jobControllerApi.handleDriverResponse(anyString(), any()))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> verify(jobControllerApi, atLeastOnce()).handleDriverResponse(anyString(), any()));
  }

  @Test
  void acknowledgeJobCancellation() {
    EsbJobEvent esbJobEvent =
        EsbJobEventRequestUtility.constructJobEventForAcknowledgeJobCancellation();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(jobControllerApi.handleDriverResponse(anyString(), any()))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> verify(jobControllerApi, atLeastOnce()).handleDriverResponse(anyString(), any()));
  }

  @Test
  void jobConfirmAcknowledge() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructJobEventForJobConfirmAcknowledge();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(jobControllerApi.handleDriverResponse(anyString(), any()))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> verify(jobControllerApi, atLeastOnce()).handleDriverResponse(anyString(), any()));
  }

  @Test
  void rejectJobModification() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructJobEventForRejectJobModification();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));
    Mockito.when(jobControllerApi.handleDriverResponse(anyString(), any()))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> verify(jobControllerApi, atLeastOnce()).handleDriverResponse(anyString(), any()));
  }

  @Test
  void callout() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructJobEventForCallout();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(jobControllerApi.handleDriverResponse(anyString(), any()))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> verify(jobControllerApi, atLeastOnce()).handleDriverResponse(anyString(), any()));
  }

  @Test
  void acknowledgeConvertStreethail() {
    EsbJobEvent esbJobEvent =
        EsbJobEventRequestUtility.constructJobEventForAcknowledgeConvertStreethail();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(jobControllerApi.handleDriverResponse(anyString(), any()))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> verify(jobControllerApi, atLeastOnce()).handleDriverResponse(anyString(), any()));
  }

  @Test
  void arrivePickUp() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructArrivePickUpEvent();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenReturn(Mono.empty());
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                verify(vehicleManagementApi, atLeastOnce())
                    .updateVehicleState(anyString(), anyString(), any()));
  }

  @Test
  void notifyOncall() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructNotifyOncallEvent();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenReturn(Mono.empty());
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                verify(vehicleManagementApi, atLeastOnce())
                    .updateVehicleState(anyString(), anyString(), any()));
  }

  @Test
  void updateStop() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructUpdateStopEvent();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenReturn(Mono.empty());
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                verify(vehicleManagementApi, atLeastOnce())
                    .updateVehicleState(anyString(), anyString(), any()));
  }

  @Test
  void reportTripInfoNormal() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructJobEventForReportTripInfoNormal();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));
    when(bookingControllerApi.getBookingProducts())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generateBookingProductResponse()));
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
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

  @Test
  void noShow() {
    // arrange
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructNoShowEvent();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenReturn(Mono.empty());
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void shouldSuccessfullyEndTheProcessIfLocationIsInvalidForNoShow() {
    // arrange
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructNoShowEventForInvalidLocation();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void meterOnStreethailJob() {
    // arrange
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructMeterOnStreethailJobEvent();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenReturn(Mono.empty());
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void meterOffStreethailJob() {
    // arrange
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructMeterOffStreethailJobEvent();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenReturn(Mono.empty());
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void autoAcceptJobConfirmationAcknowledge() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructAutoAcceptJobConfirmationAck();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(jobControllerApi.handleDriverResponse(anyString(), any()))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> verify(jobControllerApi, atLeastOnce()).handleDriverResponse(anyString(), any()));
  }

  @Test
  void voiceStreamingMessage() {
    RcsaEvent rcsaEvent = RcsaEventRequestUtility.constructRcsaEventForVoiceStreaming();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    MessageBuilder<RcsaEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaEvent).setHeader(KafkaHeaders.TOPIC, rcsaEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateRcsaEventResponse();
  }

  @Test
  void sendMessage() {
    RcsaEvent rcsaEvent = RcsaEventRequestUtility.constructRcsaEventForSendMessage();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));
    MessageBuilder<RcsaEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaEvent).setHeader(KafkaHeaders.TOPIC, rcsaEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateRcsaEventResponse();
  }

  // Add tests for message already processed scenarios here

  @Test
  void messageAlreadyProcessedForCallout() {
    EsbJobEvent esbJobEvent =
        EsbJobEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.CALL_OUT.getId()));
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void messageAlreadyProcessedForAcknowledgeConvertStreethail() {
    EsbJobEvent esbJobEvent =
        EsbJobEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.ACKNOWLEDGE_CONVERT_STREET_HAIL.getId()));
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void redundantMessageForAcknowledgeConvertStreethail() {
    EsbJobEvent esbJobEvent =
        EsbJobEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.ACKNOWLEDGE_CONVERT_STREET_HAIL.getId()));
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .put(REDUNDANT_MESSAGE_CACHE_KEY, true);
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void redundantMessageInvalidKeyForAcknowledgeConvertStreethail() {
    EsbJobEvent esbJobEvent =
        EsbJobEventRequestUtility.constructJobEventForAcknowledgeConvertStreethail();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .put(STORE_FORWARD_CACHE_KEY, true);
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.INTERNAL_SERVER_ERROR.getCode().toString(), null, Locale.ENGLISH));
  }

  @Test
  void messageAlreadyProcessedForEmergencyReport() {
    RcsaEvent rcsaEvent = RcsaEventRequestUtility.constructRcsaEventForEmergencyReport();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(STORE_FORWARD_CACHE_KEY_FOR_EMERGENCY_REPORT, true);
    MessageBuilder<RcsaEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaEvent).setHeader(KafkaHeaders.TOPIC, rcsaEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void messageAlreadyProcessedForReportTripInfoNormal() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructJobEventForReportTripInfoNormal();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put("10123450", true);
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void messageAlreadyProcessedForArrivePickUpEvent() {
    EsbJobEvent esbJobEvent =
        EsbJobEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.ARRIVAL.getId()));
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void messageAlreadyProcessedForNotifyOncallEvent() {
    EsbJobEvent esbJobEvent =
        EsbJobEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.NOTIFY_ONCALL.getId()));
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void messageAlreadyProcessedForUpdateStopEvent() {
    EsbJobEvent esbJobEvent =
        EsbJobEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.UPDATE_STOP.getId()));
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void messageAlreadyProcessedForMeterOffStreethailJobEvent() {

    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructMeterOffStreethailJobEvent();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(STORE_FORWARD_CACHE_KEY, true);
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void messageAlreadyProcessedForNoShowEvent() {

    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructNoShowEvent();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(STORE_FORWARD_CACHE_KEY_FOR_NO_SHOW, true);
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void messageAlreadyProcessedForVoiceStreamingMessage() {
    RcsaEvent rcsaEvent =
        RcsaEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.VOICE_STREAMING_MESSAGE.getId()));
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);
    MessageBuilder<RcsaEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaEvent).setHeader(KafkaHeaders.TOPIC, rcsaEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void messageAlreadyProcessedForSendMessage() {
    RcsaEvent rcsaEvent =
        RcsaEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.SEND_MESSAGE.getId()));
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);
    MessageBuilder<RcsaEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaEvent).setHeader(KafkaHeaders.TOPIC, rcsaEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void messageAlreadyProcessedForMeterOnStreetHailJobEvent() {

    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructMeterOnStreethailJobEvent();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(STORE_FORWARD_CACHE_KEY, true);
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /** Advance Job Remind event success scenario */
  @Test
  void shouldSuccessfullyConsumeAdvanceJobRemindEventCacheNotNull() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructAdvanceJobRemindEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(ADVANCED_JOB_REMIND_EVENT_STORE_FORWARD_CACHE_KEY, true);
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void shouldSuccessfullyConsumeAdvanceJobRemindEvent() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructAdvanceJobRemindEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));
    Mockito.when(jobEventLogControllerApi.insertJobEventLog(any())).thenReturn(Mono.empty());

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /** This test is to check Fare Calculation Request Event on success scenario */
  @Test
  void shouldSuccessfullyWhenFareCalculationRequest() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructFareCalculationRequest();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    when(bookingPaymentsApi.getTariffCalculation(any()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.fareTariffResponse()));

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);

    when(paymentMethodApi.getPaymentMethodList())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generatePaymentMethodResponse()));

    kafkaProducer.send(messageBuilder.build());

    validateKafkaAckAndIvdResponse(
        String.valueOf(IVDMessageType.FARE_CALCULATION_RESPONSE.getId()));
    clearTopic(jobEventIvdResponse);
  }

  /**
   * This test is to check Fare Calculation Request on Event Acknowledgement when
   * StoreAndForwardCacheNotNull scenario
   */
  @Test
  void fareCalculationRequestWhenStoreAndForwardCacheNotNull() {
    EsbJobEvent esbJobEvent =
        EsbJobEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.FARE_CALCULATION_REQUEST.getId()));

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();

    when(paymentMethodApi.getPaymentMethodList())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generatePaymentMethodResponse()));

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /** validateKafkaIvdResponse */
  private void validateKafkaIvdResponseForIvdNo() {
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
                    assertEquals((Integer) 50684, ivdResponse.getIvdNo());
                  });
              return true;
            });
    consumer.close();
  }

  private void validateRcsaEventResponse() {
    KafkaConsumer<String, byte[]> consumer = getKafkaConsumer();
    consumer.subscribe(Collections.singletonList(produceRcsaEventResponse));
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
                    ProduceRcsaEvent produceRcsaEvent =
                        (ProduceRcsaEvent)
                            messageConverter.extractAndConvertValue(
                                payload, ProduceRcsaEvent.class);
                    assertEquals((Integer) 50684, produceRcsaEvent.getIvdNo());
                  });
              return true;
            });
    consumer.close();
  }

  /**
   * Method to validate RcsaEvent response
   *
   * @param uniqueMsgId uniqueMsgId
   */
  private void validateRcsaEventResponseforUniqueMsgId(String uniqueMsgId) {
    KafkaConsumer<String, byte[]> consumer = getKafkaConsumer();
    consumer.subscribe(Collections.singletonList(produceRcsaEventResponse));
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
                    ProduceRcsaEvent produceRcsaEvent =
                        (ProduceRcsaEvent)
                            messageConverter.extractAndConvertValue(
                                payload, ProduceRcsaEvent.class);
                    assertEquals(uniqueMsgId, produceRcsaEvent.getMessage().getUniqueMsgId());
                  });
              return true;
            });
    consumer.close();
  }

  /** Method to validate emergency id response */
  private void validateRcsaEmergencyIdResponse() {
    KafkaConsumer<String, byte[]> consumer = getKafkaConsumer();
    consumer.subscribe(Collections.singletonList(produceRcsaEventResponse));
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
                    ProduceRcsaEvent produceRcsaEvent =
                        (ProduceRcsaEvent)
                            messageConverter.extractAndConvertValue(
                                payload, ProduceRcsaEvent.class);
                    assertEquals(
                        IntegrationTestConstants.EMERGENCY_ID, produceRcsaEvent.getEmergencyId());
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

  private void validateKafkaAckAndIvdResponse(String eventIdentifier) {
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
                    assert (List.of(eventIdentifier.split(","))
                        .contains(ivdResponse.getEventIdentifier()));
                  });
              return true;
            });
    consumer.close();
  }

  @Test
  void rcsaFalseAlarmEvent() {
    RcsaEvent rcsaEvent = RcsaEventRequestUtility.constructRcsaEventForFalseAlarm();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));
    Mockito.when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenReturn(Mono.empty());
    MessageBuilder<RcsaEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaEvent).setHeader(KafkaHeaders.TOPIC, rcsaEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
    validateRcsaEmergencyIdResponse();
  }

  @Test
  void messageAlreadyProcessedForFalseAlarm() {
    RcsaEvent rcsaEvent = RcsaEventRequestUtility.constructRcsaEventForFalseAlarm();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(RCSA_FALSE_ALARAM_STORE_FORWARD_CACHE_KEY, true);
    MessageBuilder<RcsaEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaEvent).setHeader(KafkaHeaders.TOPIC, rcsaEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void jobModification() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructJobEventForModification();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(jobControllerApi.handleDriverResponse(anyString(), any()))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> verify(jobControllerApi, atLeastOnce()).handleDriverResponse(anyString(), any()));
  }

  @Test
  void jobMessageAcknowledge() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructJobEventForMessageAcknowledge();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(STORE_FORWARD_CACHE_KEY_FOR_MESSAGE_ACK, "50008020");

    Mockito.when(jobControllerApi.handleDriverResponse(anyString(), any()))
        .thenReturn(Mono.empty());
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> verify(jobControllerApi, atLeastOnce()).handleDriverResponse(anyString(), any()));
  }

  @Test
  void successfullyConsumeMeterOffEvent() {
    // arrange
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructMeterOffEvent();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenReturn(Mono.empty());
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void messageAlreadyProcessedForMeterOffEvent() {

    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructMeterOffEvent();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(STORE_FORWARD_CACHE_KEY, true);
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void successfullyConsumeMeterOnEvent() {
    // arrange
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructMeterOnDispatchEvent();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenReturn(Mono.empty());
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void messageAlreadyProcessedForMeterOnEvent() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructMeterOnDispatchEvent();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(STORE_FORWARD_CACHE_KEY, true);
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void jobRejectForReasonCode4() {
    EsbJobEvent esbJobEvent =
        EsbJobEventRequestUtility.constructJobEventForRejectWithMessage(
            "9B 01 26 C6 47 8C 32 70 00 2B 80 39 09 7C E6 65 00 87 00 01 00 00 44 00 08 00 6D CA 0D 2A 01 00 00 00 79 00 01 00 04");

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(jobControllerApi.handleDriverResponse(anyString(), any()))
        .thenReturn(Mono.empty());
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> verify(jobControllerApi, atLeastOnce()).handleDriverResponse(anyString(), any()));
  }

  @Test
  void jobRejectForReasonCode5() {
    EsbJobEvent esbJobEvent =
        EsbJobEventRequestUtility.constructJobEventForRejectWithMessage(
            "9B 01 26 C6 47 8C 32 70 00 2B 80 39 09 7C E6 65 00 87 00 01 00 00 44 00 08 00 6D CA 0D 2A 01 00 00 00 79 00 01 00 05");

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));
    Mockito.when(jobControllerApi.handleDriverResponse(anyString(), any()))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> verify(jobControllerApi, atLeastOnce()).handleDriverResponse(anyString(), any()));
  }

  @Test
  void jobRejectForReasonCode6() {
    EsbJobEvent esbJobEvent =
        EsbJobEventRequestUtility.constructJobEventForRejectWithMessage(
            "9B 01 26 C6 47 8C 32 70 00 2B 80 39 09 7C E6 65 00 87 00 01 00 00 44 00 08 00 6D CA 0D 2A 01 00 00 00 79 00 01 00 06");

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(jobControllerApi.handleDriverResponse(anyString(), any()))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () -> verify(jobControllerApi, atLeastOnce()).handleDriverResponse(anyString(), any()));
  }

  @Test
  void shouldSuccessfulyMdtRequestEventAckNegativeCase() {
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
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.jobOffersResponse()));
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.INTERNAL_SERVER_ERROR.getCode().toString(), null, Locale.getDefault()));
    KafkaConsumer<String, byte[]> consumer = getKafkaConsumer2();
    consumer.subscribe(Collections.singletonList(jobEventTopic));
    Awaitility.await()
        .atMost(5, TimeUnit.SECONDS)
        .until(
            () -> {
              ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(500));
              if (records.isEmpty()) {
                return false;
              }
              records.forEach(
                  payload -> {
                    JobDispatchEvent jobDispatchEvent =
                        (JobDispatchEvent)
                            messageConverter.extractAndConvertValue(
                                payload, JobDispatchEvent.class);
                    assertEquals("50684", jobDispatchEvent.getIvdNo());
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
                    assertThat(vehicleCommFailedRequest.getMessage())
                        .startsWith(IntegrationTestConstants.INTERNAL_SERVER_ERROR_GENERIC);
                  });
              return true;
            });
    consumer.close();
  }

  @Test
  void shouldSuccessfulyMdtRequestEventCacheNotNull() {
    EsbJobEvent esbJobEvent =
        EsbJobEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.MDT_SYNC_REQUEST.getId()));
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void emergencyReport() {
    RcsaEvent rcsaEvent = RcsaEventRequestUtility.constructRcsaEventForEmergencyReport();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));
    Mockito.when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenReturn(Mono.empty());
    MessageBuilder<RcsaEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaEvent).setHeader(KafkaHeaders.TOPIC, rcsaEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
    validateRcsaEventResponseUsingVehicleId(
        MdtServiceResponseUtiltiy.vehicleDetailsResponse().getVehicleId());
  }

  @Test
  void messageAlreadyProcessedForJobNumberBlockRequestEvent() {
    EsbJobEvent esbJobEvent =
        EsbJobEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.JOB_NUMBER_BLOCK_REQUEST.getId()));
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);
    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void jobNumberBlockRequest() {
    EsbJobEvent esbJobEvent = EsbJobEventRequestUtility.constructJobNumberBlockRequestEvent();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    when(mdtControllerApi.jobNoBlockRequest(any()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.jobNoBlockAPIResponse()));

    MessageBuilder<EsbJobEvent> messageBuilder =
        MessageBuilder.withPayload(esbJobEvent).setHeader(KafkaHeaders.TOPIC, ivdJobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaAckAndIvdResponse(
        String.valueOf(IVDMessageType.JOB_NUMBER_BLOCK_RESPONSE.getId()));
  }

  private void validateRcsaEventResponseUsingVehicleId(String vehicleId) {
    KafkaConsumer<String, byte[]> consumer = getKafkaConsumer();
    consumer.subscribe(Collections.singletonList(produceRcsaEventResponse));
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
                    ProduceRcsaEvent produceRcsaEvent =
                        (ProduceRcsaEvent)
                            messageConverter.extractAndConvertValue(
                                payload, ProduceRcsaEvent.class);
                    assertEquals(vehicleId, produceRcsaEvent.getVehicleId());
                  });
              return true;
            });
    consumer.close();
  }

  @Test
  void rcsaEmergencyInitiated() {

    RcsaEvent rcsaEvent = RcsaEventRequestUtility.constructRcsaEventForEmergencyInitiated();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));
    MessageBuilder<RcsaEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaEvent).setHeader(KafkaHeaders.TOPIC, rcsaEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
    validateRcsaEmergencyIdResponse();
  }

  @Test
  void messageAlreadyProcessedForEmergencyInitiated() {
    RcsaEvent rcsaEvent = RcsaEventRequestUtility.constructRcsaEventForEmergencyInitiated();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(RCSA_FALSE_ALARAM_STORE_FORWARD_CACHE_KEY, true);
    MessageBuilder<RcsaEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaEvent).setHeader(KafkaHeaders.TOPIC, rcsaEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /**
   * This test is to check Already Processed For Respond To Structure Message Event on success
   * scenario
   */
  @Test
  void messageAlreadyProcessedForRespondToStructureMessage() {
    RcsaEvent rcsaEvent = RcsaEventRequestUtility.constructRcsaEventForrespondToStructureMessage();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(RCSA_RESPOND_TO_STRUCTURE, true);
    MessageBuilder<RcsaEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaEvent).setHeader(KafkaHeaders.TOPIC, rcsaEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /** This test is to check Respond To Structure Message Event on success scenario */
  @Test
  void shouldSuccessfullyConsumeRespondToStructureMessage() {
    RcsaEvent rcsaEvent = RcsaEventRequestUtility.constructRcsaEventForrespondToStructureMessage();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));
    MessageBuilder<RcsaEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaEvent).setHeader(KafkaHeaders.TOPIC, rcsaEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
    validateRcsaEventResponseforUniqueMsgId("15062017073901951194");
  }

  /** This test is to check Respond To Simple Message Event on success scenario */
  @Test
  void messageAlreadyProcessedForResponseToSimpleMessage() {
    RcsaEvent rcsaEvent = RcsaEventRequestUtility.constructToResponseToSimpleMassage();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(RCSA_RESPONSE_TO_SIMPLE_MESSAGE_STORE_FORWARD_CACHE_KEY, true);
    MessageBuilder<RcsaEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaEvent).setHeader(KafkaHeaders.TOPIC, rcsaEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void responseToSimpleMessage() {
    RcsaEvent rcsaEvent = RcsaEventRequestUtility.constructToResponseToSimpleMassage();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));
    MessageBuilder<RcsaEvent> messageBuilder =
        MessageBuilder.withPayload(rcsaEvent).setHeader(KafkaHeaders.TOPIC, rcsaEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdAckResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
    validateRcsaEventResponseforUniqueMsgId("060620170851363304039");
  }
}
