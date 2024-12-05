package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.consumer;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.DomainException;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.async.KafkaMessageHandler;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs.properties.RefreshableProperties;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.esbregularreportevent.EsbRegularReportEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.mappers.IvdRegularReportEventMapper;
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
public class EsbRegularReportEventConsumer {

  private final KafkaMessageHandler kafkaMessageHandler;
  private final IvdRegularReportEventMapper ivdRegularReportEventMapper;
  private final RefreshableProperties refreshableProperties;

  @Value("${spring.kafka.vehcomm.topic.ivd_regular_report_event}")
  private String ivdRegularReportEventTopic;

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
      topics = "${spring.kafka.vehcomm.topic.ivd_regular_report_event}",
      groupId = "${spring.kafka.vehcomm.group}")
  public void handleEsbRegularReportEvent(@Payload EsbRegularReportEvent esbRegularReportEvent) {
    var ivdInboundEvent =
        ivdRegularReportEventMapper.esbRegularReportEventToIvdInboundEvent(
            esbRegularReportEvent, refreshableProperties);
    kafkaMessageHandler.esbRegularReportEventHandler(ivdInboundEvent, ivdRegularReportEventTopic);
  }
}
