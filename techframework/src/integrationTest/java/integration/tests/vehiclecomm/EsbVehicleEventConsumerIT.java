package integration.tests.vehiclecomm;

/*
 * Integration testing for ivd job event
 */

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
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.esbregularreportevent.EsbRegularReportEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.esbvehicleevent.EsbVehicleEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ivdresponse.IvdResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.vehiclecommfailedrequest.VehicleCommFailedRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.IVDMessageType;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fleetAnalytic.client.apis.DriverPerformanceServiceApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.apis.MdtControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MdtForgotPasswordRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.vehicle.client.apis.VehicleManagementApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.vehicle.client.models.ReportVehicleTotalMileageRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.vehicle.client.models.UpdateMdtInformationRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.vehicle.client.models.UpdateVehicleStateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import integration.IntegrationTestBase;
import integration.constants.IntegrationTestConstants;
import integration.utils.EsbVehicleEventRequestUtility;
import integration.utils.FleetAnalyticServiceUtility;
import integration.utils.MdtServiceResponseUtiltiy;
import jakarta.annotation.Nullable;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.MessageSource;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class EsbVehicleEventConsumerIT extends IntegrationTestBase {

  private static final String FORGOT_PASSWORD_STORE_FORWARD_CACHE_KEY = "43584103";

  private static final int VERIFY_OTP_RESPONSE_MESSAGE_ID = 29;

  private final KafkaTemplate<String, Object> kafkaProducer;

  private final MessageSource messageSource;
  @MockBean MdtControllerApi mdtControllerApi;

  private final DomainEventByteArrayJsonSchemaMessageConverter messageConverter;
  private final CacheManager cacheManager;

  @MockBean VehicleManagementApi vehicleManagementApi;

  @MockBean
  @Qualifier("vehicleManagementRegularReportApi")
  VehicleManagementApi vehicleManagementRegularReportApi;

  @MockBean DriverPerformanceServiceApi driverPerformanceServiceApi;

  @Value("${spring.kafka.vehcomm.topic.ivd_vehicle_event}")
  String ivdVehicleEventTopic;

  @Value("${spring.kafka.vehcomm.topic.ivd_regular_report_event}")
  private String ivdRegularReportEventTopic;

  @Value("${event2Topic.VehicleCommFailedRequest.name}")
  String vehicleEventErrorOutTopic;

  @Value("${event2Topic.IvdResponse.name}")
  String vehicleEventIvdResponse;

  @AfterEach
  void afterEach() {
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(cacheManager.getCache(VehicleCommAppConstant.REDUNDANT_MSG_CACHE))
        .clear();
  }

  /* update STC event success scenario */
  @Test
  void shouldSuccessfullyConsumeUpdateStcEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructUpdateStcEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(ArgumentMatchers.anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                verify(vehicleManagementApi, atLeastOnce())
                    .updateVehicleState(anyString(), anyString(), any()));
  }

  /** update Busy event success scenario */
  @Test
  void shouldSuccessfullyConsumeBusyEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructBusyEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(ArgumentMatchers.anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(
            vehicleManagementApi.updateVehicleState(
                anyString(), anyString(), any(UpdateVehicleStateRequest.class)))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                verify(vehicleManagementApi, atLeastOnce())
                    .updateVehicleState(anyString(), anyString(), any()));
  }

  /** update Busy event success scenario */
  @Test
  void shouldSuccessfullyConsumeRegularEvent() {
    // arrange
    EsbRegularReportEvent esbRegularReportEvent =
        EsbVehicleEventRequestUtility.constructRegularEvent1();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(
            vehicleManagementRegularReportApi.updateVehicleState(
                anyString(), anyString(), any(UpdateVehicleStateRequest.class)))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbRegularReportEvent> messageBuilder =
        MessageBuilder.withPayload(esbRegularReportEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdRegularReportEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                verify(vehicleManagementRegularReportApi, atLeastOnce())
                    .updateVehicleState(anyString(), anyString(), any()));
  }

  /** update Verify Otp event success scenario */
  @Test
  void shouldSuccessfullyConsumeVerifyOtpEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructVerifyOtpEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.mdtVerifyOtp(any()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.verifyOtpResponse()));

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaAckAndIvdVehicleResponse(String.valueOf(VERIFY_OTP_RESPONSE_MESSAGE_ID));

    clearTopic(ivdVehicleEventTopic);
  }

  /** update Verify Otp event failure scenario */
  @Test
  void shouldProduceToKafkaErrorOutDueToNetworkErrorWhileCallingMdtServiceVerifyOtpEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructVerifyOtpEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.mdtVerifyOtp(any()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.getDefault()));

    clearTopic(ivdVehicleEventTopic);
  }

  /** update change pin event server error scenario */
  @Test
  void serverExceptionOnConsumeChangePinEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructChangePinEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.mdtChangePin(any())).thenThrow(new RuntimeException("error"));

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaErrorOut(
        messageSource
            .getMessage(
                ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.getDefault())
            .concat(" - ")
            .concat(VehicleCommAppConstant.DOMAIN_EXCEPTION));
  }

  /** update change pin event server error scenario */
  @Test
  void badRequestnOnConsumeChangePinEvent() throws JsonProcessingException {
    // arrange
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructChangePinEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    String changePinApiResponseJson =
        objectMapper.writeValueAsString(MdtServiceResponseUtiltiy.changePinResponse());
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    WebClientResponseException webClientResponseException =
        WebClientResponseException.create(
            HttpStatus.BAD_REQUEST.value(),
            "bad request",
            headers,
            changePinApiResponseJson.getBytes(),
            null,
            null);
    webClientResponseException.setBodyDecodeFunction(
        initDecodeFunction(changePinApiResponseJson.getBytes(), MediaType.APPLICATION_JSON));

    when(mdtControllerApi.mdtChangePin(any())).thenThrow(webClientResponseException);

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaAckAndIvdVehicleResponse(
        String.valueOf(IVDMessageType.CHANGE_PIN_REQUEST.getId()));
    clearTopic(ivdVehicleEventTopic);
  }

  private Function<ResolvableType, ?> initDecodeFunction(
      byte[] body, @Nullable MediaType contentType) {
    return targetType -> {
      if (ObjectUtils.isEmpty(body)) {
        return null;
      }
      Decoder<?> decoder = null;
      for (HttpMessageReader<?> reader : ExchangeStrategies.withDefaults().messageReaders()) {
        if (reader.canRead(targetType, contentType)) {
          if (reader instanceof DecoderHttpMessageReader<?> decoderReader) {
            decoder = decoderReader.getDecoder();
            break;
          }
        }
      }
      Assert.state(decoder != null, "No suitable decoder");
      DataBuffer buffer = DefaultDataBufferFactory.sharedInstance.wrap(body);
      return decoder.decode(buffer, targetType, null, Collections.emptyMap());
    };
  }

  /** update change pin event success scenario */
  @Test
  void shouldSuccessfullyConsumeChangePinEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructChangePinEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.mdtChangePin(any()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.changePinResponse()));

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaAckAndIvdVehicleResponse(
        String.valueOf(IVDMessageType.CHANGE_PIN_REQUEST.getId()));
    clearTopic(ivdVehicleEventTopic);

    // clean up
    clearTopic(IntegrationTestConstants.VEHICLE_COMM_FAILED_TOPIC);
  }

  /** byte array empty scenario */
  @Test
  void shouldProduceToKafkaErrorOutTopicDueToEmptyMessage() {
    // arrange
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructEmptyVehicleEvent();

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);

    // act
    kafkaProducer.send(messageBuilder.build());

    // verify
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.ESB_MESSAGE_REQUIRED.getCode().toString(), null, Locale.getDefault()));
  }

  @Test
  void shouldSuccessfullyConsumeUpdateIvdPing() {
    // arrange
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructUpdatePingEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.mdtIvdPingUpdate(
            any(
                com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models
                    .IvdPingUpdateRequest.class)))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.ivdPingResponse()));

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                verify(mdtControllerApi, atLeastOnce())
                    .mdtIvdPingUpdate(
                        any(
                            com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models
                                .IvdPingUpdateRequest.class)));
  }

  @Test
  void shouldProduceToKafkaErrorOutTopicDueToNetworkErrorWhileUpdatePingEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructUpdatePingEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.mdtIvdPingUpdate(
            any(
                com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models
                    .IvdPingUpdateRequest.class)))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));
    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);

    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.getDefault()));
  }

  /** MDT Service Exception scenario */
  @Test
  void shouldProduceToKafkaErrorOutTopicDueToNetworkErrorWhileCallingMDTService() {
    // arrange
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.UPDATE_STC.getId()));

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /** MDT Service Exception scenario */
  @Test
  void shouldProduceToKafkaErrorOutTopicDueToNetworkErrorWhileCallingMDTServiceRegularEvent() {
    // arrange
    EsbRegularReportEvent esbRegularReportEvent =
        EsbVehicleEventRequestUtility.constructIVDRegularReportEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.REGULAR_REPORT.getId()));

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);

    MessageBuilder<EsbRegularReportEvent> messageBuilder =
        MessageBuilder.withPayload(esbRegularReportEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdRegularReportEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /** MDT Service Exception scenario */
  @Test
  void shouldProduceToKafkaErrorOutTopicDueToNetworkErrorWhileCallingMDTServiceBusyEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.BUSY_UPDATE.getId()));

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /** report total mileage event success scenario */
  @Test
  void shouldSuccessfullyConsumeReportTotalMileageEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructReportTotalMileageEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(ArgumentMatchers.anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(
            vehicleManagementApi.reportVehicleTotalMileage(
                anyString(), any(ReportVehicleTotalMileageRequest.class)))
        .thenReturn(Mono.empty());
    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));

    // clean up
    clearTopic(IntegrationTestConstants.VEHICLE_COMM_FAILED_TOPIC);
  }

  /** report total mileage event byte array empty scenario */
  @Test
  void shouldProduceToKafkaErrorOutTopicDueToEmptyMessageOnReportTotalMileageEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructEmptyReportTotalMileageEvent();

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);

    // act
    kafkaProducer.send(messageBuilder.build());

    // verify
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.ESB_MESSAGE_REQUIRED.getCode().toString(), null, Locale.ENGLISH));
  }

  /** MDT Service Exception scenario */
  @Test
  void
      shouldProduceToKafkaErrorOutTopicDueToNetworkErrorWhileCallingMDTServiceOnReportTotalMileageEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructReportTotalMileageEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);

    // act
    kafkaProducer.send(messageBuilder.build());

    // verify
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.ENGLISH));
  }

  /** vehicle service Exception scenario */
  @Test
  void
      shouldProduceToKafkaErrorOutDueToNetworkErrorWhileCallingVehicleServiceOnReportTotalMileageEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructReportTotalMileageEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    when(vehicleManagementApi.reportVehicleTotalMileage(
            anyString(), any(ReportVehicleTotalMileageRequest.class)))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));
    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);

    // act
    kafkaProducer.send(messageBuilder.build());

    // verify
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.VEHICLE_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.ENGLISH));
  }

  /** vehicle service Exception scenario */
  @Test
  void shouldProduceToKafkaErrorOutDueToNetworkErrorWhileCallingVehicleServiceRegularEvent() {
    // arrange
    EsbRegularReportEvent esbRegularReportEvent =
        EsbVehicleEventRequestUtility.constructRegularEvent1();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));
    Mockito.when(
            vehicleManagementRegularReportApi.updateVehicleState(
                anyString(), anyString(), any(UpdateVehicleStateRequest.class)))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));
    MessageBuilder<EsbRegularReportEvent> messageBuilder =
        MessageBuilder.withPayload(esbRegularReportEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdRegularReportEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());

    // verify
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.VEHICLE_SERVICE_NETWORK_ERROR.getCode().toString(),
            null,
            Locale.getDefault()));
  }

  /** This test will successfully end the process when store and forward cache is not null */
  @Test
  void shouldSuccessfullyStopTheProcessWhenStoreAndForwardCacheNotNullOnReportTotalMileageEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructReportTotalMileageEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);

    // act
    kafkaProducer.send(messageBuilder.build());

    // verify
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.getDefault()));
  }

  /** vehicle service Exception scenario */
  @Test
  void shouldProduceToKafkaErrorOutDueToNetworkErrorWhileCallingVehicleService() {
    // arrange
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructUpdateStcEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.VEHICLE_SERVICE_NETWORK_ERROR.getCode().toString(),
            null,
            Locale.getDefault()));
  }

  /** vehicle service Exception scenario */
  @Test
  void shouldProduceToKafkaErrorOutDueToNetworkErrorWhileCallingVehicleServiceBusyEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructBusyEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(
            vehicleManagementApi.updateVehicleState(
                anyString(), anyString(), any(UpdateVehicleStateRequest.class)))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));
    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());

    // verify
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.VEHICLE_SERVICE_NETWORK_ERROR.getCode().toString(),
            null,
            Locale.getDefault()));
  }

  /** This test will successfully end the process when store and forward cache is not null */
  @Test
  void shouldSuccessfullyStopTheProcessWhenStoreAndForwardCacheNotNull() {
    // arrange
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.UPDATE_STC.getId()));

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /**
   * This test will successfully end the process when store and forward cache is not null for busy
   * event
   */
  @Test
  void shouldSuccessfullyStopTheProcessWhenStoreAndForwardCacheNotNullBusyEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.BUSY_UPDATE.getId()));

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /**
   * This test will successfully end the process when store and forward cache is not null for
   * regular event
   */
  @Test
  void shouldSuccessfullyStopTheProcessWhenStoreAndForwardCacheNotNullRegularEvent() {
    // arrange
    EsbRegularReportEvent esbRegularReportEvent =
        EsbVehicleEventRequestUtility.constructIVDRegularReportEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.REGULAR_REPORT.getId()));

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);

    MessageBuilder<EsbRegularReportEvent> messageBuilder =
        MessageBuilder.withPayload(esbRegularReportEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdRegularReportEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /**
   * This test will successfully end the process when store and forward cache is not null for verify
   * Otp event
   */
  @Test
  void shouldSuccessfullyStopTheProcessWhenStoreAndForwardCacheNotNullForVerifyOtp() {
    // arrange
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.VERIFY_OTP.getId()));

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /**
   * validateKafkaErrorOut
   *
   * @param message message
   */
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

  /**
   * Method to validate kafka response for verify otp event
   *
   * @param eventIdentifier event identifier
   */
  private void validateKafkaIvdVehicleResponse(String eventIdentifier) {
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
                    assertEquals(eventIdentifier, ivdResponse.getEventIdentifier());
                  });
              return true;
            });
    consumer.close();
  }

  private void validateKafkaAckAndIvdVehicleResponse(String eventIdentifier) {
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

                    assert (List.of(eventIdentifier.split(","))
                        .contains(ivdResponse.getEventIdentifier()));
                  });
              return true;
            });
    consumer.close();
  }

  /** Method to test IVD event when event type is disable auto-bid and cache having value */
  @Test
  void shouldSuccessfullyStopTheProcessWhenStoreAndForwardCacheNotNullIvdEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructIVDEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY, true);

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /** Method to test IVD event when event type is Enable auto-bid */
  @Test
  void shouldSuccessfullyConsumeIVDEventWhenEventTypeIsEnableAutoBid() {
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructIVDEventForEnableAutoBid();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /** Method to test IVD event when event type is Enable auto accept */
  @Test
  void shouldSuccessfullyConsumeIVDEventWhenEventTypeIsEnableAutoAccept() {
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructIVDEventForEnableAutoAccept();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /**
   * This test will successfully end the process when store and forward cache is not null for change
   * pin event
   */
  @Test
  void shouldSuccessfullyStopTheProcessWhenStoreAndForwardCacheNotNullForChangePin() {
    // arrange
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.CHANGE_PIN_REQUEST.getId()));

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /** Method to test IVD event when event type is CROSSING_ZONE */
  @Test
  void shouldSuccessfullyConsumeIVDEventWhenEventTypeIsCrossingZone() {
    EsbVehicleEvent vehicleEvent = EsbVehicleEventRequestUtility.constructVehicleMdtUpdate("133");

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(
            vehicleManagementApi.updateMdtInformation(
                anyString(), any(UpdateMdtInformationRequest.class)))
        .thenReturn(Mono.empty());
    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(vehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /** Method to test IVD event when event type is CROSSING_ZONE */
  @Test
  void shouldSuccessfullyConsumeIVDEventWhenEventTypeIsCrossingZoneCacheNotNull() {
    EsbVehicleEvent vehicleEvent = EsbVehicleEventRequestUtility.constructVehicleMdtUpdate("133");

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY, true);

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(vehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /** Method to test IVD event when event type is CROSSING_ZONE */
  @Test
  void shouldSuccessfullyConsumeIVDEventWhenEventTypeIsCrossingZoneNetworkError() {
    EsbVehicleEvent vehicleEvent = EsbVehicleEventRequestUtility.constructVehicleMdtUpdate("133");

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(vehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));

    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.getDefault()));
  }

  /** Method to test IVD event when event type is CROSSING_ZONE */
  @Test
  void shouldProduceToKafkaErrorOutDueToNetworkErrorWhileCallingVehicleServiceCrossingZoneEvent() {
    EsbVehicleEvent vehicleEvent = EsbVehicleEventRequestUtility.constructVehicleMdtUpdate("133");

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(
            vehicleManagementApi.updateMdtInformation(
                anyString(), any(UpdateMdtInformationRequest.class)))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(vehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));

    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.VEHICLE_SERVICE_NETWORK_ERROR.getCode().toString(),
            null,
            Locale.getDefault()));
  }

  @Test
  void shouldSuccessfullyConsumeIVDEventWhenEventTypeIsChangeShiftUpdate() {
    EsbVehicleEvent vehicleEvent =
        EsbVehicleEventRequestUtility.constructVehicleMdtUpdate(
            IntegrationTestConstants.CHANGE_SHIFT_UPDATE_EVENT_ID);

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    when(mdtControllerApi.getIvdZoneInfoDetailsByZoneIvdNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.zoneInfoDetailsResponse()));

    Mockito.when(
            vehicleManagementApi.updateMdtInformation(
                anyString(), any(UpdateMdtInformationRequest.class)))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(vehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  @Test
  void shouldSuccessfullyConsumeIVDEventWhenEventTypeIsChangeShiftUpdateCacheNotNull() {
    EsbVehicleEvent vehicleEvent =
        EsbVehicleEventRequestUtility.constructVehicleMdtUpdate(
            IntegrationTestConstants.CHANGE_SHIFT_UPDATE_EVENT_ID);

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY, true);

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(vehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
    clearTopic(IntegrationTestConstants.VEHICLE_COMM_FAILED_TOPIC);
  }

  @Test
  void shouldSuccessfullyConsumeIVDEventWhenEventTypeIsChangeShiftUpdateNetworkError() {
    EsbVehicleEvent vehicleEvent =
        EsbVehicleEventRequestUtility.constructVehicleMdtUpdate(
            IntegrationTestConstants.CHANGE_SHIFT_UPDATE_EVENT_ID);

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(vehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));

    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.getDefault()));
  }

  @Test
  void shouldThrowMdtNetworkErrorForChangeShiftUpdate() {
    EsbVehicleEvent vehicleEvent =
        EsbVehicleEventRequestUtility.constructVehicleMdtUpdate(
            IntegrationTestConstants.CHANGE_SHIFT_UPDATE_EVENT_ID);

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    when(mdtControllerApi.getIvdZoneInfoDetailsByZoneIvdNo(anyInt()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(vehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));

    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.getDefault()));
    // clean up
    clearTopic(IntegrationTestConstants.VEHICLE_COMM_FAILED_TOPIC);
  }

  @Test
  void shouldSuccessfullyConsumeIVDEventWhenEventTypeIsUpdateDestination() {
    EsbVehicleEvent vehicleEvent = EsbVehicleEventRequestUtility.constructVehicleMdtUpdate("171");

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(
            vehicleManagementApi.updateMdtInformation(
                anyString(), any(UpdateMdtInformationRequest.class)))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(vehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));

    // clean up
    clearTopic(IntegrationTestConstants.VEHICLE_COMM_FAILED_TOPIC);
  }

  @Test
  void shouldSuccessfullyConsumeIVDEventWhenEventTypeIsUpdateDestinationCacheNotNull() {
    EsbVehicleEvent vehicleEvent = EsbVehicleEventRequestUtility.constructVehicleMdtUpdate("171");

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY, true);

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(vehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
    clearTopic(IntegrationTestConstants.VEHICLE_COMM_FAILED_TOPIC);
  }

  @Test
  void shouldSuccessfullyConsumeIVDEventWhenEventTypeIsUpdateDestinationNetworkError() {
    EsbVehicleEvent vehicleEvent = EsbVehicleEventRequestUtility.constructVehicleMdtUpdate("171");

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(vehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));

    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.getDefault()));
  }

  /** Method to test IVD event when event type is Driver performance */
  @Test
  void shouldSuccessfullyConsumeIVDEventWhenEventTypeIsDriverPerformance() {
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructDriverPerformance();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    when(driverPerformanceServiceApi.getDriverPerformanceHistory(any(), any(), any(), any()))
        .thenReturn(FleetAnalyticServiceUtility.driverPerformanceHistoryAPIResponse());

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);

    kafkaProducer.send(messageBuilder.build());
    String eventIdentifier =
        IVDMessageType.MESSAGE_AKNOWLEDGE.getId()
            + IntegrationTestConstants.COMMA
            + IVDMessageType.DRIVER_PERFORMANCE_RESPONSE.getId();
    validateKafkaAckAndIvdVehicleResponse(eventIdentifier);
    clearTopic(ivdVehicleEventTopic);
  }

  @Test
  void shouldSuccessfullyConsumeIVDEventWhenEventTypeIsDriverPerformanceMdtNetworkError() {
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructDriverPerformance();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);

    kafkaProducer.send(messageBuilder.build());

    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.getDefault()));
  }

  @Test
  void
      shouldSuccessfullyConsumeIVDEventWhenEventTypeIsDriverPerformanceFleetAnalyticNetworkError() {
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructDriverPerformance();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    when(driverPerformanceServiceApi.getDriverPerformanceHistory(any(), any(), any(), any()))
        .thenThrow(
            new WebClientResponseException(
                404, IntegrationTestConstants.NOT_FOUND_ERROR, null, null, null));

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);

    kafkaProducer.send(messageBuilder.build());

    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.FLEET_ANALYTIC_SERVICE_NETWORK_ERROR.getCode().toString(),
            null,
            Locale.getDefault()));
  }

  @Test
  void
      shouldSuccessfullyConsumeIVDEventWhenEventTypeIsDriverPerformanceFleetAnalyticNoPerformance() {
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructDriverPerformance();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    when(driverPerformanceServiceApi.getDriverPerformanceHistory(any(), any(), any(), any()))
        .thenReturn(FleetAnalyticServiceUtility.driverPerformanceHistoryAPIResponse());

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);

    kafkaProducer.send(messageBuilder.build());
    String eventIdentifier =
        IVDMessageType.MESSAGE_AKNOWLEDGE.getId()
            + IntegrationTestConstants.COMMA
            + IVDMessageType.DRIVER_PERFORMANCE_RESPONSE.getId();
    validateKafkaAckAndIvdVehicleResponse(eventIdentifier);
    clearTopic(ivdVehicleEventTopic);
  }

  /** Method to test IVD event when event type is Break and cache having value */
  @Test
  void shouldSuccessfullyConsumeBreakEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructBreakEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                verify(vehicleManagementApi, atLeastOnce())
                    .updateVehicleState(anyString(), anyString(), any()));
  }

  /** MDT Service Exception scenario */
  @Test
  void shouldProduceToKafkaErrorOutTopicDueToNetworkErrorWhileCallingMDTServiceBreakEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructBreakEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenThrow(
            new WebClientResponseException(
                404, IntegrationTestConstants.NOT_FOUND_ERROR, null, null, null));

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());

    // verify
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.getDefault()));
  }

  /** vehicle service Exception scenario */
  @Test
  void shouldProduceToKafkaErrorOutDueToNetworkErrorWhileCallingVehicleServiceBreakEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructBreakEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());

    // verify
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.VEHICLE_SERVICE_NETWORK_ERROR.getCode().toString(),
            null,
            Locale.getDefault()));
  }

  /**
   * This test will successfully end the process when store and forward cache is not null for Break
   * Event
   */
  @Test
  void shouldSuccessfullyStopTheProcessWhenStoreAndForwardCacheNotNullBreakEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.BREAK_UPDATE.getId()));

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /**
   * Method to test IVD event when event type is Expressway Status Update event and cache not having
   * value
   */
  @Test
  void shouldSuccessfullyConsumeExpresswayStatusUpdateEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructExpresswayStatusUpdateEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(
            vehicleManagementApi.updateVehicleState(
                anyString(), anyString(), any(UpdateVehicleStateRequest.class)))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                verify(vehicleManagementApi, atLeastOnce())
                    .updateVehicleState(anyString(), anyString(), any()));
  }

  /** MDT Service Exception scenario for Expressway Status Update event */
  @Test
  void
      shouldProduceToKafkaErrorOutTopicDueToNetworkErrorWhileCallingMDTServiceExpresswayStatusUpdateEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructExpresswayStatusUpdateEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    // verify
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.getDefault()));
  }

  /** vehicle service Exception scenario in Expressway Status Update event */
  @Test
  void
      shouldProduceToKafkaErrorOutDueToNetworkErrorWhileCallingVehicleServiceExpresswayStatusUpdateEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructExpresswayStatusUpdateEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(
            vehicleManagementApi.updateVehicleState(
                anyString(), anyString(), any(UpdateVehicleStateRequest.class)))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));
    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());

    // verify
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.VEHICLE_SERVICE_NETWORK_ERROR.getCode().toString(),
            null,
            Locale.getDefault()));
  }

  /**
   * This test will successfully end the process when store and forward cache is not null for
   * Expressway Status Update event
   */
  @Test
  void
      shouldSuccessfullyStopTheProcessWhenStoreAndForwardCacheNotNullExpresswayStatusUpdateEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.ON_EXPRESSWAY_UPDATE.getId()));

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /** Method to test IVD event when event type is Notify Static GPS and cache not having value */
  @Test
  void shouldSuccessfullyConsumeNotifyStaticGpsEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructNotifyStaticGpsEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    Mockito.when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenReturn(Mono.empty());

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                verify(vehicleManagementApi, atLeastOnce())
                    .updateVehicleState(anyString(), anyString(), any()));
  }

  /** MDT Service Exception scenario */
  @Test
  void
      shouldProduceToKafkaErrorOutTopicDueToNetworkErrorWhileCallingMDTServiceNotifyStaticGpsEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructNotifyStaticGpsEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenThrow(
            new WebClientResponseException(
                404, IntegrationTestConstants.NOT_FOUND_ERROR, null, null, null));

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());

    // verify
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.getDefault()));
  }

  /** vehicle service Exception scenario */
  @Test
  void
      shouldProduceToKafkaErrorOutDueToNetworkErrorWhileCallingVehicleServiceNotifyStaticGpsEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructNotifyStaticGpsEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();
    when(mdtControllerApi.getVehicleByIVDNo(anyInt()))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.vehicleDetailsResponse()));

    when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));
    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());

    // verify
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.VEHICLE_SERVICE_NETWORK_ERROR.getCode().toString(),
            null,
            Locale.getDefault()));
  }

  /**
   * This test will successfully end the process when store and forward cache is not null for Notify
   * Static Gps Event
   */
  @Test
  void shouldSuccessfullyStopTheProcessWhenStoreAndForwardCacheNotNullNotifyStaticGpsEvent() {
    // arrange
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructIVDEventForMsgAlreadyProcessed(
            String.valueOf(IVDMessageType.NOTIFY_STATIC_GPS.getId()));

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(IntegrationTestConstants.STORE_FORWARD_MSG_CACHE_KEY_FOR_MSG_ALREADY_PROCESSED, true);

    MessageBuilder<EsbVehicleEvent> messageBuilder =
        MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /** Forget Password event success scenario */
  @Test
  void shouldSuccessfullyConsumeForgotPasswordEventCacheNotNull() {
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructForgotPasswordMsgAlreadyProcessedEvent();
    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .put(FORGOT_PASSWORD_STORE_FORWARD_CACHE_KEY, true);
    org.springframework.messaging.support.MessageBuilder<EsbVehicleEvent> messageBuilder =
        org.springframework.messaging.support.MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdVehicleResponse(String.valueOf(IVDMessageType.MESSAGE_AKNOWLEDGE.getId()));
  }

  /** Forget Password event success scenario */
  @Test
  void shouldSuccessfullyConsumeForgotPasswordEvent() {
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructForgotPasswordEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.mdtForgotPassword(any(MdtForgotPasswordRequest.class)))
        .thenReturn(Mono.just(MdtServiceResponseUtiltiy.forgotPasswordEventResponse()));

    org.springframework.messaging.support.MessageBuilder<EsbVehicleEvent> messageBuilder =
        org.springframework.messaging.support.MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaAckAndIvdVehicleResponse(
        String.valueOf(IVDMessageType.FORGOT_PASSWORD_REQUEST.getId()));
    clearTopic(ivdVehicleEventTopic);
  }

  /** vehicle service Network Exception scenario */
  @Test
  void shouldSuccessfullyConsumeForgotPasswordEventNetworkError() {
    EsbVehicleEvent esbVehicleEvent = EsbVehicleEventRequestUtility.constructForgotPasswordEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    when(mdtControllerApi.mdtForgotPassword(any(MdtForgotPasswordRequest.class)))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));

    org.springframework.integration.support.MessageBuilder<EsbVehicleEvent> messageBuilder =
        org.springframework.integration.support.MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());

    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.MDT_SERVICE_NETWORK_ERROR.getCode().toString(), null, Locale.getDefault()));
  }

  /** vehicle service phone number is empty Exception scenario */
  @Test
  void shouldSuccessfullyConsumeForgotPasswordEventMobileNoNullError() {
    EsbVehicleEvent esbVehicleEvent =
        EsbVehicleEventRequestUtility.constructNormalForgotPasswordEvent();

    Objects.requireNonNull(
            cacheManager.getCache(VehicleCommAppConstant.STORE_FORWARD_MSG_CACHE_NAME))
        .clear();

    org.springframework.integration.support.MessageBuilder<EsbVehicleEvent> messageBuilder =
        org.springframework.integration.support.MessageBuilder.withPayload(esbVehicleEvent)
            .setHeader(KafkaHeaders.TOPIC, ivdVehicleEventTopic);
    // act
    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.MOBILE_NO_REQUIRED.getCode().toString(), null, Locale.getDefault()));
  }
}
