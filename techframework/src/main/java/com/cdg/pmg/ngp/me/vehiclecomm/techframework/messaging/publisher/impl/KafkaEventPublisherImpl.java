package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.publisher.impl;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.configs.KafkaConfig;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ParentRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.publisher.KafkaEventPublisher;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

/**
 * The type Kafka service.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 */
@Service
@AllArgsConstructor
@Slf4j
public class KafkaEventPublisherImpl<K, V extends ParentRequest>
    implements KafkaEventPublisher<K, V> {

  private final KafkaTemplate<K, V> kafkaTemplate;

  private final KafkaConfig kafkaConfig;

  @Override
  public void send(V value) {
    this.send(null, value);
  }

  @Override
  public void send(K key, V value) {
    long start = System.currentTimeMillis();
    CompletableFuture<SendResult<K, V>> future = this.sendAsync(key, value);
    future
        .thenAccept(
            result -> {
              var end = System.currentTimeMillis();
              log.info(
                  "Success send to topic {} run {}ms",
                  kafkaConfig.getMatchingTopic(value.getClass()),
                  (end - start));
            })
        .exceptionally(
            throwable -> {
              var end = System.currentTimeMillis();
              log.error(
                  "Failure send to topic {} run {} with exception",
                  kafkaConfig.getMatchingTopic(value.getClass()),
                  (end - start),
                  throwable);
              return null;
            });
  }

  @Override
  public void send(K key, V value, BiConsumer<SendResult<K, V>, Throwable> callback) {
    long start = System.currentTimeMillis();
    CompletableFuture<SendResult<K, V>> future = this.sendAsync(key, value);
    future.whenComplete(
        (result, throwable) -> {
          var end = System.currentTimeMillis();
          callback.accept(result, throwable);
          if (throwable != null) {
            log.error(
                "Failure send to topic {} run {} with exception",
                kafkaConfig.getMatchingTopic(value.getClass()),
                (end - start),
                throwable);
          }
        });
  }

  @Override
  public void send(
      K key,
      V value,
      BiConsumer<SendResult<K, V>, Throwable> successCallback,
      Function<Throwable, SendResult<K, V>> exceptionCallback) {

    CompletableFuture<SendResult<K, V>> future = this.sendAsync(key, value);
    future.whenComplete(
        (result, throwable) -> {
          if (throwable == null) {
            successCallback.accept(result, null);
          } else {
            SendResult<K, V> fallbackResult = exceptionCallback.apply(throwable);
            successCallback.accept(fallbackResult, throwable);
          }
        });
  }

  private CompletableFuture<SendResult<K, V>> sendAsync(K key, V value) {
    String topic = kafkaConfig.getMatchingTopic(value.getClass());
    MessageBuilder<V> builder =
        MessageBuilder.withPayload(value).setHeader(KafkaHeaders.TOPIC, topic);
    if (key != null) {
      builder.setHeader(KafkaHeaders.KEY, key);
    }
    return kafkaTemplate.send(builder.build()).toCompletableFuture();
  }
}
