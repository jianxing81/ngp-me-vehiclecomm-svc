package integration;

import static org.mockito.Mockito.when;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.TechFrameworkApplication;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.bookingservice.client.apis.BookingControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.paxpaymentservice.client.apis.PaymentMethodApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import integration.containers.SingletonKafkaContainer;
import integration.containers.SingletonPostgreSQLContainer;
import integration.containers.SingletonRedisContainer;
import integration.containers.SingletonSchemaRegistryContainer;
import integration.containers.base.SchemaRegistryContainer;
import integration.rest.RetryControllerApi;
import integration.rest.VehicleCommControllerApi;
import integration.utils.JobDispatchServiceUtility;
import java.net.URI;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DeleteTopicsResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import reactor.core.publisher.Mono;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = TechFrameworkApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Slf4j
public abstract class IntegrationTestBase {

  @LocalServerPort protected int port;
  protected VehicleCommControllerApi vehicleCommControllerApi;
  protected RetryControllerApi retryControllerApi;
  @Autowired protected ObjectMapper objectMapper;

  @MockBean protected PaymentMethodApi paymentMethodApi;
  @MockBean protected BookingControllerApi bookingControllerApi;

  @Value("${spring.kafka.vehcomm.group}")
  String groupId;

  @Value("${spring.kafka.vehcomm.testcase.group}")
  String groupId2;

  @BeforeAll
  public void beforeAll() {
    // Generate base URI for making http requests
    URI baseUri = URI.create("http://localhost:" + port);

    // Register JSON schema to Kafka schema registry
    Map.ofEntries(
            Map.entry(
                "ngp.me.vehiclecomm.error_out-VehicleCommFailedRequest",
                "VehicleCommFailedRequest.json"),
            Map.entry("ngp.me.vehiclecomm.ivd_job_event-EsbJobEvent", "EsbJobEvent.json"),
            Map.entry("ngp.me.esbcomm.ivd_response-IvdResponse", "IvdResponse.json"),
            Map.entry("ngp.me.vehiclecomm.job_event-JobDispatchEvent", "JobDispatchEvent.json"),
            Map.entry(
                "ngp.me.vehiclecomm.ivd_vehicle_event-EsbVehicleEvent", "EsbVehicleEvent.json"),
            Map.entry("ngp.me.trip.upload_trip.r2-UploadTripEvent", "TripUpload.json"),
            Map.entry(
                "ngp.common.notifications.subscribe-NotificationMessageEvent",
                "NotificationMessageEvent.json"),
            Map.entry("ngp.me.vehiclecomm.ivd_rcsa_event-RcsaEvent", "RcsaEvent.json"),
            Map.entry("ngp.me.rcsa.event-ProduceRcsaEvent", "ProduceRcsaEvent.json"),
            Map.entry(
                "ngp.me.vehiclecomm.rcsa.message.event-RcsaMessageEvent", "RcsaMessageEvent.json"),
            Map.entry("ngp.me.vehiclecomm.vehicle_event-VehicleEvent", "VehicleEvent.json"),
            Map.entry("ngp.me.vehiclecomm.driver_event-DriverEvent", "DriverEvent.json"),
            Map.entry(
                "ngp.me.vehiclecomm.ivd_regular_report_event-EsbRegularReportEvent",
                "EsbRegularReportEvent.json"))
        .forEach(this::registerSchema);

    Retrofit retrofit =
        new Retrofit.Builder()
            .baseUrl(baseUri.toString())
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .build();
    vehicleCommControllerApi = retrofit.create(VehicleCommControllerApi.class);
    retryControllerApi = retrofit.create(RetryControllerApi.class);
    when(paymentMethodApi.getPaymentMethodList())
        .thenReturn(Mono.just(JobDispatchServiceUtility.paymentMethodResponse()));
    when(bookingControllerApi.getBookingProducts())
        .thenReturn(Mono.just(JobDispatchServiceUtility.bookingProductResponse()));
  }

  private void registerSchema(String subject, String jsonFileName) {

    SchemaRegistryContainer schemaRegistryContainer =
        SingletonSchemaRegistryContainer.getInstance();

    try {
      String schemaRegistryUrl = schemaRegistryContainer.getUrl();
      RestTemplate restTemplate = new RestTemplate();
      ClassPathResource resource = new ClassPathResource("json/" + jsonFileName);
      byte[] jsonData = StreamUtils.copyToByteArray(resource.getInputStream());
      JSONObject schema = new JSONObject();
      schema.put("schema", new String(jsonData));
      schema.put("schemaType", "JSON");

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<String> requestEntity = new HttpEntity<>(schema.toString(), httpHeaders);
      restTemplate.exchange(
          schemaRegistryUrl + "/subjects/" + subject + "/versions",
          HttpMethod.POST,
          requestEntity,
          String.class);
    } catch (Exception e) {
      log.error("Unable to register schema in Kafka schema registry ", e);
    }
  }

  @DynamicPropertySource
  public static void registerProperties(DynamicPropertyRegistry registry) {

    KafkaContainer kafkaContainer = SingletonKafkaContainer.getInstance();
    SchemaRegistryContainer schemaRegistryContainer =
        SingletonSchemaRegistryContainer.getInstance();
    GenericContainer<?> redisContainer = SingletonRedisContainer.getInstance();
    PostgreSQLContainer<?> postgreSQLContainer = SingletonPostgreSQLContainer.getInstance();

    registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    registry.add("spring.kafka.properties.schema.registry.url", schemaRegistryContainer::getUrl);

    registry.add(
        "spring.datasource.url",
        () -> postgreSQLContainer.getJdbcUrl() + "&currentSchema=ngp_me_vehiclecomm");
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);

    registry.add("app.redis.host", redisContainer::getHost);
    registry.add("app.redis.port", redisContainer::getFirstMappedPort);
    registry.add("app.redis.ssl", () -> false);
    registry.add("storeForwardEvents", () -> "139,140,159,160,161,169,172,212,221,219,131");
  }

  protected KafkaConsumer<String, byte[]> getKafkaConsumer() {
    return new KafkaConsumer<>(
        Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            SingletonKafkaContainer.getInstance().getBootstrapServers(),
            ConsumerConfig.GROUP_ID_CONFIG,
            groupId,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
            "earliest"),
        new StringDeserializer(),
        new ByteArrayDeserializer());
  }

  protected KafkaConsumer<String, byte[]> getKafkaConsumer2() {
    return new KafkaConsumer<>(
        Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
            SingletonKafkaContainer.getInstance().getBootstrapServers(),
            ConsumerConfig.GROUP_ID_CONFIG,
            groupId2,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
            "earliest"),
        new StringDeserializer(),
        new ByteArrayDeserializer());
  }

  protected void clearTopic(String topicName) {
    Properties properties = new Properties();
    properties.put(
        AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
        SingletonKafkaContainer.getInstance().getBootstrapServers());
    AdminClient admin = AdminClient.create(properties);
    List<String> topics = Collections.singletonList(topicName);
    DeleteTopicsResult result = admin.deleteTopics(topics);
    try {
      result.all().get();
    } catch (Exception e) {
      log.error("Unable to clear the topic {}", e.getMessage());
      Thread.currentThread().interrupt();
    }
  }
}
