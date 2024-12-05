package com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.configs;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs.properties.ConnectionPoolProperties;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils.JsonHelper;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.bookingservice.client.apis.BookingControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fare.client.apis.BookingPaymentsApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fleetAnalytic.client.apis.DriverPerformanceServiceApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.apis.JobControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.apis.JobEventLogControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.apis.JobNoBlockControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.ApiClient;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mdt.client.apis.MdtControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.paxpaymentservice.client.apis.PaymentMethodApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.vehicle.client.apis.VehicleManagementApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class WebConfig {
  @Value("${client.mdt.url}")
  private String mdtSvcUrl;

  @Value("${client.jobDispatch.url}")
  private String jobDispatchUrl;

  @Value("${client.fleetAnalytic.url}")
  private String fleetAnalyticSvcUrl;

  @Value("${client.fare.url}")
  private String fareSvcUrl;

  @Value("${client.vehicle.url}")
  private String vehicleSvcUrl;

  @Value("${client.paxPayment.url}")
  private String paxPaymentSvcUrl;

  @Value("${client.bookingService.url}")
  private String bookingSvcUrl;

  private final JsonHelper jsonHelper;

  /**
   * Returns Http connection provider
   *
   * @return ConnectionProvider
   */
  @Bean
  ConnectionProvider connectionProvider(ConnectionPoolProperties connectionPoolProperties) {
    log.info("Http connection pool properties {}", connectionPoolProperties);
    ConnectionProvider.Builder connectionProviderBuilder =
        ConnectionProvider.builder("vehCommConnectionPool");
    if (Objects.nonNull(connectionPoolProperties.getMaxConnections())) {
      connectionProviderBuilder.maxConnections(connectionPoolProperties.getMaxConnections());
    }
    if (Objects.nonNull(connectionPoolProperties.getPendingAcquireTimeout())) {
      connectionProviderBuilder.pendingAcquireTimeout(
          connectionPoolProperties.getPendingAcquireTimeout());
    }
    if (Objects.nonNull(connectionPoolProperties.getDisposeInterval())
        || Objects.nonNull(connectionPoolProperties.getPoolInactivity())) {
      connectionProviderBuilder.disposeInactivePoolsInBackground(
          connectionPoolProperties.getDisposeInterval(),
          connectionPoolProperties.getPoolInactivity());
    }
    if (Objects.nonNull(connectionPoolProperties.getMaxIdleTime())) {
      connectionProviderBuilder.maxIdleTime(connectionPoolProperties.getMaxIdleTime());
    }
    if (Objects.nonNull(connectionPoolProperties.getEvictDuration())) {
      connectionProviderBuilder.evictInBackground(connectionPoolProperties.getEvictDuration());
    }
    if (Objects.nonNull(connectionPoolProperties.getPendingAcquireMaxCount())) {
      connectionProviderBuilder.pendingAcquireMaxCount(
          connectionPoolProperties.getPendingAcquireMaxCount());
    }
    return connectionProviderBuilder.build();
  }

  @Bean
  @Primary
  WebClient webClient(
      ObjectMapper objectMapper,
      ConnectionPoolProperties connectionPoolProperties,
      ConnectionProvider connectionProvider) {
    HttpClient httpClient =
        HttpClient.create(connectionProvider)
            .option(
                ChannelOption.CONNECT_TIMEOUT_MILLIS,
                connectionPoolProperties.getConnectionTimeout() * 1000)
            .doOnConnected(
                connection -> {
                  connection.addHandlerLast(
                      new ReadTimeoutHandler(connectionPoolProperties.getReadTimeout()));
                  connection.addHandlerLast(
                      new WriteTimeoutHandler(connectionPoolProperties.getWriteTimeout()));
                });
    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .codecs(
            clientCodecConfigurer -> {
              clientCodecConfigurer
                  .defaultCodecs()
                  .jackson2JsonEncoder(
                      new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
              clientCodecConfigurer
                  .defaultCodecs()
                  .jackson2JsonDecoder(
                      new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
            })
        .filter(logRequest())
        .filter(logResponse())
        .build();
  }

  @Bean
  @Qualifier("webClientWithoutLogger")
  WebClient webClientWithoutLogger(
      ObjectMapper objectMapper,
      ConnectionProvider connectionProvider,
      ConnectionPoolProperties connectionPoolProperties) {
    HttpClient httpClient =
        HttpClient.create(connectionProvider)
            .option(
                ChannelOption.CONNECT_TIMEOUT_MILLIS,
                connectionPoolProperties.getConnectionTimeout() * 1000)
            .doOnConnected(
                connection -> {
                  connection.addHandlerLast(
                      new ReadTimeoutHandler(connectionPoolProperties.getReadTimeout()));
                  connection.addHandlerLast(
                      new WriteTimeoutHandler(connectionPoolProperties.getWriteTimeout()));
                });
    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .codecs(
            clientCodecConfigurer -> {
              clientCodecConfigurer
                  .defaultCodecs()
                  .jackson2JsonEncoder(
                      new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
              clientCodecConfigurer
                  .defaultCodecs()
                  .jackson2JsonDecoder(
                      new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
            })
        .build();
  }

  @Bean
  public MdtControllerApi getMdtApi(WebClient webClient) {
    ApiClient apiClient = new ApiClient(webClient);
    apiClient.setBasePath(mdtSvcUrl);
    return new MdtControllerApi(apiClient);
  }

  @Bean
  public BookingPaymentsApi getFareApi(WebClient webClient) {
    com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fare.client.ApiClient apiClient =
        new com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fare.client.ApiClient(webClient);
    apiClient.setBasePath(fareSvcUrl);
    return new BookingPaymentsApi(apiClient);
  }

  @Bean
  public DriverPerformanceServiceApi getDriverPerformanceServiceApi(WebClient webClient) {
    com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fleetAnalytic.client.ApiClient apiClient =
        new com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fleetAnalytic.client.ApiClient(
            webClient);
    apiClient.setBasePath(fleetAnalyticSvcUrl);
    return new DriverPerformanceServiceApi(apiClient);
  }

  @Bean
  public JobControllerApi getJobControllerApi(WebClient webClient) {
    com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.ApiClient
        apiClient =
            new com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client
                .ApiClient(webClient);
    apiClient.setBasePath(jobDispatchUrl);
    return new JobControllerApi(apiClient);
  }

  @Bean
  public JobEventLogControllerApi getJobEventLogControllerApi(WebClient webClient) {
    com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.ApiClient
        apiClient =
            new com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client
                .ApiClient(webClient);
    apiClient.setBasePath(jobDispatchUrl);
    return new JobEventLogControllerApi(apiClient);
  }

  @Bean
  public JobNoBlockControllerApi getJobNoBlockControllerApi(WebClient webClient) {
    com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client.ApiClient
        apiClient =
            new com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.jobdispatchservice.client
                .ApiClient(webClient);
    apiClient.setBasePath(jobDispatchUrl);
    return new JobNoBlockControllerApi(apiClient);
  }

  @Bean
  @Primary
  public VehicleManagementApi getVehicleManagementApi(WebClient webClient) {
    com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.vehicle.client.ApiClient apiClient =
        new com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.vehicle.client.ApiClient(webClient);
    apiClient.setBasePath(vehicleSvcUrl);
    return new VehicleManagementApi(apiClient);
  }

  @Bean
  @Qualifier("vehicleManagementRegularReportApi")
  public VehicleManagementApi vehicleManagementRegularReportApi(
      @Qualifier("webClientWithoutLogger") WebClient webClient) {
    com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.vehicle.client.ApiClient apiClient =
        new com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.vehicle.client.ApiClient(webClient);
    log.debug("Vehicle Management API for Regular Report");
    apiClient.setBasePath(vehicleSvcUrl);
    return new VehicleManagementApi(apiClient);
  }

  @Bean
  public PaymentMethodApi getPaxPaymentMethodApi(WebClient webClient) {
    com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.paxpaymentservice.client.ApiClient apiClient =
        new com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.paxpaymentservice.client.ApiClient(
            webClient);
    apiClient.setBasePath(paxPaymentSvcUrl);
    return new PaymentMethodApi(apiClient);
  } // bookingSvcUrl

  @Bean
  public BookingControllerApi getBookingSvcApi(WebClient webClient) {
    com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.bookingservice.client.ApiClient apiClient =
        new com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.bookingservice.client.ApiClient(
            webClient);
    apiClient.setBasePath(bookingSvcUrl);
    return new BookingControllerApi(apiClient);
  }

  private ExchangeFilterFunction logRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(
        clientRequest -> {
          Map<String, Object> logDetails =
              Map.of(
                  "method", clientRequest.method().name(),
                  "url", clientRequest.url().toString(),
                  "headers", clientRequest.headers().toSingleValueMap());
          log.info("[HTTP Request Headers] - {}", jsonHelper.pojoToJson(logDetails));
          return Mono.just(clientRequest);
        });
  }

  private ExchangeFilterFunction logResponse() {
    return ExchangeFilterFunction.ofResponseProcessor(
        clientResponse -> {
          Map<String, Object> logDetails =
              new java.util.HashMap<>(
                  Map.of(
                      "status", clientResponse.statusCode().toString(),
                      "headers", clientResponse.headers().asHttpHeaders().toSingleValueMap()));
          log.info("[HTTP Response Headers] - {}", jsonHelper.pojoToJson(logDetails));
          return Mono.just(clientResponse);
        });
  }
}
