package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.publisher;

import java.util.function.BiConsumer;
import java.util.function.Function;
import org.springframework.kafka.support.SendResult;

/**
 * The interface Kafka service.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 */
public interface KafkaEventPublisher<K, V> {

  /**
   * Send message.
   *
   * @param value the value
   */
  void send(V value);

  /**
   * Send message.
   *
   * @param key the key
   * @param value the value
   */
  void send(K key, V value);

  /**
   * Send message.
   *
   * @param key the key
   * @param value the value
   * @param callback the callback
   */
  void send(K key, V value, BiConsumer<SendResult<K, V>, Throwable> callback);

  /**
   * Send message.
   *
   * @param key the key
   * @param value the value
   * @param successCallback the success callback
   * @param exceptionCallback the exception callback
   */
  void send(
      K key,
      V value,
      BiConsumer<SendResult<K, V>, Throwable> successCallback,
      Function<Throwable, SendResult<K, V>> exceptionCallback);
}
