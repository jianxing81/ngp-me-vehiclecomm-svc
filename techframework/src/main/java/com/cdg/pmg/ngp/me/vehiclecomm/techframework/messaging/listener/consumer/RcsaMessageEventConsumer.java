package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.consumer;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.DomainException;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.async.KafkaMessageHandler;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs.properties.RefreshableProperties;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.rcsamessageevent.RcsaMessageEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.mappers.RcsaMessageEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Component
public class RcsaMessageEventConsumer {

  private final KafkaMessageHandler kafkaMessageHandler;
  private final RcsaMessageEventMapper rcsaMessageEventMapper;
  private final RefreshableProperties refreshableProperties;

  @Value("${spring.kafka.vehcomm.topic.rcsa_message_event}")
  public String rcsaMessageEventTopic;

  /** Method to handle JobEvents from kafka */
  @RetryableTopic(
      attempts = "${spring.kafka.consumer.retry.max-attempts}",
      backoff =
          @Backoff(
              delayExpression = "${spring.kafka.consumer.retry.delay}",
              maxDelayExpression = "${spring.kafka.consumer.retry.max-delay}"),
      retryTopicSuffix = "${spring.kafka.consumer.retry.topic.suffix}",
      dltTopicSuffix = "${spring.kafka.consumer.topic.dead-letter.suffix}",
      autoCreateTopics = "${spring.kafka.consumer.retry.autoCreateTopics}",
      topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
      dltStrategy = DltStrategy.FAIL_ON_ERROR,
      exclude = DomainException.class)
  @KafkaListener(
      topics = "${spring.kafka.vehcomm.topic.rcsa_message_event}",
      groupId = "${spring.kafka.vehcomm.group}")
  public void handleRcsaMessageEvent(@Payload RcsaMessageEvent rcsaMessageEvent) {
    log.info("[handleRcsaMessageEvent]received rcsaMessageEvent: {}", rcsaMessageEvent);
    var rcsaMessageInboundEvent =
        rcsaMessageEventMapper.rcsaMessageEventToRcsaMessageInboundEvent(
            rcsaMessageEvent, refreshableProperties);
    log.info("[handleRcsaMessageEvent] convert to RcsaMessage: {}", rcsaMessageInboundEvent);
    kafkaMessageHandler.handleRcsaMessageEvent(rcsaMessageInboundEvent, rcsaMessageEventTopic);
  }
}
