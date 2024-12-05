package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.configs;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.DomainException;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ParentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.SocketTimeoutException;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@RequiredArgsConstructor
@ConfigurationProperties
@Data
public class KafkaConfig {

  @Value("${spring.kafka.consumer.retry.max-attempts}")
  private Integer maxAttempts;

  @Value("${spring.kafka.consumer.retry.delay}")
  private Integer delay;

  @Value("${vehiclecomm.kafka.listener.concurrency}")
  private Integer concurrency;

  private Map<String, TopicConfig> event2Topic;

  private final KafkaProperties kafkaProperties;
  private final DomainEventByteArrayJsonSchemaMessageConverter messageConverter;

  @Bean
  public <T, V extends ParentRequest> KafkaTemplate<T, V> kafkaTemplate(
      ProducerFactory<T, V> producerFactory) {
    KafkaTemplate<T, V> kafkaTemplate = new KafkaTemplate<>(producerFactory);
    // The following code enable observation in the producer extends DomainEvent
    kafkaTemplate.setMessageConverter(messageConverter);
    kafkaTemplate.setObservationEnabled(true);
    return kafkaTemplate;
  }

  private Class<?> toClass(String clazz) {
    try {
      return Class.forName(clazz);
    } catch (ClassNotFoundException e) {
      throw new DomainException(e.getMessage(), (long) HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }

  @Autowired
  public KafkaConfig(
      ObjectMapper objectMapper,
      KafkaProperties kafkaProperties,
      DomainEventByteArrayJsonSchemaMessageConverter messageConverter) {
    this.kafkaProperties = kafkaProperties;
    this.messageConverter = messageConverter;
    var provider = new ClassPathScanningCandidateComponentProvider(false);
    provider.addIncludeFilter(new AssignableTypeFilter(ParentRequest.class));

    provider.findCandidateComponents(ParentRequest.class.getPackage().getName()).stream()
        .map(BeanDefinition::getBeanClassName)
        .map(this::toClass)
        .forEach(objectMapper::registerSubtypes);
  }

  public ConsumerFactory<String, Object> consumerFactory() {
    Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>>
      kafkaListenerContainerFactory(DefaultErrorHandler errorHandler) {
    ConcurrentKafkaListenerContainerFactory<String, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setRecordMessageConverter(messageConverter);
    factory.setCommonErrorHandler(errorHandler);
    factory.setConcurrency(concurrency);
    factory.getContainerProperties().setObservationEnabled(true);
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
    return factory;
  }

  @Bean
  public DefaultErrorHandler errorHandler() {
    var fixedBackOff = new FixedBackOff(delay, maxAttempts);
    DefaultErrorHandler errorHandler =
        new DefaultErrorHandler((consumerRecord, e) -> {}, fixedBackOff);
    errorHandler.addRetryableExceptions(SocketTimeoutException.class);
    errorHandler.addNotRetryableExceptions(DomainException.class);

    return errorHandler;
  }

  public <V extends ParentRequest> String getMatchingTopic(Class<V> eventClass) {
    return event2Topic.get(eventClass.getSimpleName()).name;
  }

  @AllArgsConstructor
  @Data
  public static class TopicConfig {

    private String name;

    private int numPartitions;

    private short replicationFactor;
  }
}
