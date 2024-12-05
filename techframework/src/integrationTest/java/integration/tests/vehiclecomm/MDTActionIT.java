package integration.tests.vehiclecomm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.configs.DomainEventByteArrayJsonSchemaMessageConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ivdresponse.IvdResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.apis.MdtControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.models.MdtPowerUpRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.vehicle.client.apis.VehicleManagementApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import integration.IntegrationTestBase;
import integration.constants.IntegrationTestConstants;
import integration.utils.MdtActionUtiltiy;
import jakarta.annotation.Nullable;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class MDTActionIT extends IntegrationTestBase {
  private final MockMvc mockMvc;
  @MockBean MdtControllerApi mdtControllerApi;
  @MockBean VehicleManagementApi vehicleManagementApi;
  private final DomainEventByteArrayJsonSchemaMessageConverter messageConverter;

  @Value("${event2Topic.IvdResponse.name}")
  String jobEventIvdResponse;

  @Test
  void successfulLogOffMdtAction() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    String json = objectMapper.writeValueAsString(MdtActionUtiltiy.constructMdtLogOffRequest());
    when(mdtControllerApi.mdtLogoff(any()))
        .thenReturn(Mono.just(MdtActionUtiltiy.logOffResponse()));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(IntegrationTestConstants.IVD_LOGOUT_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());
    validateKafkaIvdJobResponse("125");
  }

  @Test
  void successfulLogOffMdtActionEmptyResponseScenario() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    String json = objectMapper.writeValueAsString(MdtActionUtiltiy.constructMdtLogOffRequest());
    when(mdtControllerApi.mdtLogoff(any()))
        .thenThrow(new RuntimeException(IntegrationTestConstants.MDT_RUNTIME_ERROR_MESSAGE));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(IntegrationTestConstants.IVD_LOGOUT_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is5xxServerError());
    validateKafkaIvdJobResponse("125");
  }

  @Test
  void failureLogOffMdtAction() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    String json = objectMapper.writeValueAsString(MdtActionUtiltiy.constructMdtLogOffRequest());
    String logOffResponse =
        objectMapper.writeValueAsString(MdtActionUtiltiy.logOffResponseWithoutIP());
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    WebClientResponseException webClientResponseException =
        WebClientResponseException.create(
            HttpStatus.BAD_REQUEST.value(),
            IntegrationTestConstants.MDT_BAD_REQUEST_ERROR_MESSAGE,
            headers,
            logOffResponse.getBytes(),
            null,
            null);
    webClientResponseException.setBodyDecodeFunction(
        initDecodeFunction(logOffResponse.getBytes(), MediaType.APPLICATION_JSON));
    when(mdtControllerApi.mdtLogoff(any())).thenThrow(webClientResponseException);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(IntegrationTestConstants.IVD_LOGOUT_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());
    validateKafkaIvdJobResponse("125");
  }

  @Test
  void successfulLogOnMdtAction() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    String json = objectMapper.writeValueAsString(MdtActionUtiltiy.constructMdtLogOnRequest());
    when(mdtControllerApi.mdtLogOn(any())).thenReturn(Mono.just(MdtActionUtiltiy.logOnResponse()));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(IntegrationTestConstants.IVD_LOGON_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void failureLogOnMdtAction() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    String json = objectMapper.writeValueAsString(MdtActionUtiltiy.constructMdtLogOnRequest());
    String logOnResponse =
        objectMapper.writeValueAsString(MdtActionUtiltiy.constructMdtLogOnRequest());
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    WebClientResponseException webClientResponseException =
        WebClientResponseException.create(
            HttpStatus.BAD_REQUEST.value(),
            IntegrationTestConstants.MDT_BAD_REQUEST_ERROR_MESSAGE,
            headers,
            logOnResponse.getBytes(),
            null,
            null);
    webClientResponseException.setBodyDecodeFunction(
        initDecodeFunction(logOnResponse.getBytes(), MediaType.APPLICATION_JSON));
    when(mdtControllerApi.mdtLogOn(ArgumentMatchers.any())).thenThrow(webClientResponseException);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(IntegrationTestConstants.IVD_LOGON_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void logOnMdtActionExceptionScenario() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    String json = objectMapper.writeValueAsString(MdtActionUtiltiy.constructMdtLogOnRequest());
    when(mdtControllerApi.mdtLogOn(any()))
        .thenThrow(new RuntimeException(IntegrationTestConstants.MDT_RUNTIME_ERROR_MESSAGE));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(IntegrationTestConstants.IVD_LOGON_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is5xxServerError());
  }

  @Test
  void successfulIVDDeviceConfigMdtAction() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    String json =
        objectMapper.writeValueAsString(MdtActionUtiltiy.constructMdtIVDDeviceConfigRequest());
    when(mdtControllerApi.mdtIvdDeviceConfig(anyInt(), any()))
        .thenReturn(Mono.just(MdtActionUtiltiy.ivdDeviceConfigResponse()));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(IntegrationTestConstants.IVD_HARDWARE_INFO_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void successfulIVDDeviceConfigMdtActionEmptyResponse() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    String json =
        objectMapper.writeValueAsString(MdtActionUtiltiy.constructMdtIVDDeviceConfigRequest());
    when(mdtControllerApi.mdtIvdDeviceConfig(anyInt(), any()))
        .thenThrow(new WebClientResponseException(100, "", null, null, null));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(IntegrationTestConstants.IVD_HARDWARE_INFO_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void failureIVDDeviceConfigMdtAction() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    String json =
        objectMapper.writeValueAsString(MdtActionUtiltiy.constructMdtIVDDeviceConfigRequest());
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    WebClientResponseException webClientResponseException =
        WebClientResponseException.create(
            HttpStatus.BAD_REQUEST.value(),
            IntegrationTestConstants.MDT_BAD_REQUEST_ERROR_MESSAGE,
            headers,
            json.getBytes(),
            null,
            null);
    webClientResponseException.setBodyDecodeFunction(
        initDecodeFunction(json.getBytes(), MediaType.APPLICATION_JSON));
    when(mdtControllerApi.mdtIvdDeviceConfig(anyInt(), ArgumentMatchers.any()))
        .thenThrow(webClientResponseException);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(IntegrationTestConstants.IVD_HARDWARE_INFO_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void ivdDeviceConfigMdtActionBadRequestExceptionScenario() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    String json =
        objectMapper.writeValueAsString(MdtActionUtiltiy.constructMdtIVDDeviceConfigRequest());
    when(mdtControllerApi.mdtIvdDeviceConfig(anyInt(), any()))
        .thenThrow(new RuntimeException(IntegrationTestConstants.MDT_RUNTIME_ERROR_MESSAGE));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(IntegrationTestConstants.IVD_HARDWARE_INFO_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is5xxServerError());
  }

  @Test
  void successfulPowerUpMdtAction() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    String json = objectMapper.writeValueAsString(MdtActionUtiltiy.constructMdtPowerUpRequest());
    when(mdtControllerApi.mdtPowerUp(any()))
        .thenReturn(Mono.just(MdtActionUtiltiy.powerUpResponse()));
    when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenReturn(Mono.empty());
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(IntegrationTestConstants.IVD_POWER_UP_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());
    validateKafkaIvdJobResponse("125");
  }

  @Test
  void successfulPowerUpMdtActionWhenVehicleServiceThrowsException() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    String json = objectMapper.writeValueAsString(MdtActionUtiltiy.constructMdtPowerUpRequest());
    when(mdtControllerApi.mdtPowerUp(any()))
        .thenReturn(Mono.just(MdtActionUtiltiy.powerUpResponse()));
    when(vehicleManagementApi.updateVehicleState(anyString(), anyString(), any()))
        .thenThrow(
            new WebClientResponseException(
                500, IntegrationTestConstants.INTERNAL_SERVER_ERROR, null, null, null));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(IntegrationTestConstants.IVD_POWER_UP_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());
    validateKafkaIvdJobResponse("125");
  }

  @Test
  void successfulPowerUpMdtActionEmptyResponseScenario() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    String json = objectMapper.writeValueAsString(MdtActionUtiltiy.constructMdtPowerUpRequest());
    when(mdtControllerApi.mdtPowerUp(any()))
        .thenThrow(new RuntimeException(IntegrationTestConstants.MDT_RUNTIME_ERROR_MESSAGE));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(IntegrationTestConstants.IVD_POWER_UP_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is5xxServerError());
    validateKafkaIvdJobResponse("125");
  }

  @Test
  void failurePowerUpMdtAction() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    String json = objectMapper.writeValueAsString(MdtActionUtiltiy.constructMdtPowerUpRequest());
    WebClientResponseException webClientResponseException =
        WebClientResponseException.create(
            HttpStatus.BAD_REQUEST.value(),
            IntegrationTestConstants.MDT_BAD_REQUEST_ERROR_MESSAGE,
            headers,
            json.getBytes(),
            null,
            null);
    webClientResponseException.setBodyDecodeFunction(
        initDecodeFunction(json.getBytes(), MediaType.APPLICATION_JSON));
    when(mdtControllerApi.mdtPowerUp(ArgumentMatchers.any())).thenThrow(webClientResponseException);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(IntegrationTestConstants.IVD_POWER_UP_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());
    validateKafkaIvdJobResponse("125");
  }

  @Test
  void failurePowerUpInternalMdtAction() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    String json = objectMapper.writeValueAsString(MdtActionUtiltiy.constructMdtPowerUpRequest());
    WebClientResponseException webClientResponseException =
        WebClientResponseException.create(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            IntegrationTestConstants.INTERNAL_SERVER_ERROR,
            headers,
            json.getBytes(),
            null,
            null);
    webClientResponseException.setBodyDecodeFunction(
        initDecodeFunction(json.getBytes(), MediaType.APPLICATION_JSON));
    when(mdtControllerApi.mdtPowerUp(ArgumentMatchers.any())).thenThrow(webClientResponseException);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(IntegrationTestConstants.IVD_POWER_UP_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());
    validateKafkaIvdJobResponse("125");
  }

  @Test
  void failurePowerUpReasonCodeNotNullScenario() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    String json = objectMapper.writeValueAsString(MdtActionUtiltiy.constructMdtPowerUpRequest());
    String json1 = objectMapper.writeValueAsString(MdtActionUtiltiy.powerUpfailureResponse());

    WebClientResponseException webClientResponseException =
        WebClientResponseException.create(
            HttpStatus.BAD_REQUEST.value(),
            IntegrationTestConstants.MDT_BAD_REQUEST_ERROR_MESSAGE,
            headers,
            json1.getBytes(),
            null,
            null);
    webClientResponseException.setBodyDecodeFunction(
        initDecodeFunction(json1.getBytes(), MediaType.APPLICATION_JSON));
    when(mdtControllerApi.mdtPowerUp(any(MdtPowerUpRequest.class)))
        .thenThrow(webClientResponseException);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(IntegrationTestConstants.IVD_POWER_UP_URL)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());
    validateKafkaIvdJobResponse("125");
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

  private void validateKafkaIvdJobResponse(String eventName) {
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

                    assertEquals(eventName, ivdResponse.getEventIdentifier());
                  });
              return true;
            });
    consumer.close();
  }
}
