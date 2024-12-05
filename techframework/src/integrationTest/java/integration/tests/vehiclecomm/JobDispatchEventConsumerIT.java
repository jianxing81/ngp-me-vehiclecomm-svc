package integration.tests.vehiclecomm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.configs.DomainEventByteArrayJsonSchemaMessageConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.ivdresponse.IvdResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.jobdispatchevent.JobDispatchEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.notificationmessageevent.NotificationMessageEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.vehiclecommfailedrequest.VehicleCommFailedRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.bookingservice.client.apis.BookingControllerApi;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.paxpaymentservice.client.apis.PaymentMethodApi;
import integration.IntegrationTestBase;
import integration.utils.EsbJobEventRequestUtility;
import integration.utils.JobDispatchServiceUtility;
import integration.utils.JobEventRequestUtility;
import java.time.Duration;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Slf4j
public class JobDispatchEventConsumerIT extends IntegrationTestBase {

  private final KafkaTemplate<String, Object> kafkaProducer;

  private final MessageSource messageSource;

  private final DomainEventByteArrayJsonSchemaMessageConverter messageConverter;

  private final PaymentMethodApi paymentMethodApiSvc;

  private final BookingControllerApi bookingControllerApiSvc;

  @Value("${spring.kafka.vehcomm.topic.job_event}")
  String jobEventTopic;

  @Value("${event2Topic.IvdResponse.name}")
  String jobEventIvdResponse;

  @Value("${event2Topic.VehicleCommFailedRequest.name}")
  String jobEventErrorOutTopic;

  @Value("${event2Topic.NotificationMessageEvent.name}")
  String notifyResponse;

  @Test
  void successfulJobModification() {

    JobDispatchEvent jobDispatchEvent = JobEventRequestUtility.constructJobModificationEvent();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generatePaymentMethodResponse()));

    when(bookingControllerApiSvc.getBookingProducts())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generateBookingProductResponse()));

    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(jobDispatchEvent.getEventName().value());
  }

  @Test
  void successfulJobModificationPayload() {

    JobDispatchEvent jobDispatchEvent =
        JobEventRequestUtility.constructJobModificationEventPayload();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generatePaymentMethodResponse()));

    when(bookingControllerApiSvc.getBookingProducts())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generateAlternateBookingProductResponse()));

    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(jobDispatchEvent.getEventName().value());
  }

  @Test
  void successfulStreetHailScenario() {
    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generatePaymentMethodResponse()));
    JobDispatchEvent jobDispatchEvent = JobEventRequestUtility.constructStreetJobEvent();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generatePaymentMethodResponse()));

    when(bookingControllerApiSvc.getBookingProducts())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generateBookingProductResponse()));

    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(jobDispatchEvent.getEventName().value());
  }

  @Test
  void successfulJobModificationNotificationToAppPayload() {

    JobDispatchEvent jobDispatchEvent =
        JobEventRequestUtility.constructJobModificationEventAppPayload();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generatePaymentMethodResponse()));

    kafkaProducer.send(messageBuilder.build());
    validateKafkaAppNotificationJobResponse(jobDispatchEvent.getEventName().value());
  }

  @Test
  void successfulSentMDTAliveNotificationToAppPayload() {

    JobDispatchEvent jobDispatchEvent = JobEventRequestUtility.constructSentMDTAliveNotification();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    kafkaProducer.send(messageBuilder.build());
    validateKafkaAppNotificationJobResponse(jobDispatchEvent.getEventName().value());
  }

  @Test
  void errorOutJobModification() {

    JobDispatchEvent jobDispatchEvent =
        JobEventRequestUtility.constructJobModificationEventErrorOut();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generatePaymentMethodResponse()));

    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.IVD_NO_REQUIRED.getCode().toString(), null, Locale.getDefault()));
  }

  @Test
  void errorOutJobCancel() {

    JobDispatchEvent jobDispatchEvent = JobEventRequestUtility.constructJobCancelEventErrorOut();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.IVD_NO_REQUIRED.getCode().toString(), null, Locale.getDefault()));
  }

  @Test
  void successfulCallOutPayload() {

    JobDispatchEvent jobDispatchEvent = JobEventRequestUtility.constructCallOutEventPayload("MDT");
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generatePaymentMethodResponse()));

    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(jobDispatchEvent.getEventName().value());
  }

  @Test
  void successfulLevyUpdatePayload() {

    JobDispatchEvent jobDispatchEvent =
        JobEventRequestUtility.constructLevyUpdateEventPayload("MDT");
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(jobDispatchEvent.getEventName().value());
  }

  @Test
  void successfulCallOutToAppPayload() {

    JobDispatchEvent jobDispatchEvent = JobEventRequestUtility.constructCallOutEventAppPayload();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generatePaymentMethodResponse()));

    kafkaProducer.send(messageBuilder.build());
    validateKafkaAppNotificationJobResponse(jobDispatchEvent.getEventName().value());
  }

  @Test
  void errorOutCallOut() {

    JobDispatchEvent jobDispatchEvent = JobEventRequestUtility.constructCallOutEventErrorOut();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generatePaymentMethodResponse()));

    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.IVD_NO_REQUIRED.getCode().toString(), null, Locale.getDefault()));
  }

  @Test
  void errorOutLevyUpdate() {

    JobDispatchEvent jobDispatchEvent = JobEventRequestUtility.constructLevyUpdateEventErrorOut();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.INVALID_IVD_NUMBER.getCode().toString(), null, Locale.ENGLISH));
  }

  @Test
  void successfulJobCancellation() {

    JobDispatchEvent jobDispatchEvent = JobEventRequestUtility.constructJobCancelEventPayload();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(jobDispatchEvent.getEventName().value());
  }

  @Test
  void successfulJobCancelPayload() {

    JobDispatchEvent jobDispatchEvent = JobEventRequestUtility.constructJobCancelEventAppPayload();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    kafkaProducer.send(messageBuilder.build());
    validateKafkaAppNotificationJobResponse(jobDispatchEvent.getEventName().value());
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

                    assertEquals(message, vehicleCommFailedRequest.getMessage());
                  });
              return true;
            });
    consumer.close();
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

  private void validateKafkaIvdJobResponse_withAdditionalCharges(String eventName) {
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

  private void validateKafkaAppNotificationJobResponse(String eventName) {
    KafkaConsumer<String, byte[]> consumer = getKafkaConsumer();
    consumer.subscribe(Collections.singletonList(notifyResponse));
    Awaitility.await()
        .atMost(10, TimeUnit.SECONDS)
        .until(
            () -> {
              ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(1000));
              if (records.isEmpty()) {
                return false;
              }
              records.forEach(
                  payload -> {
                    NotificationMessageEvent notificationMessageEvent =
                        (NotificationMessageEvent)
                            messageConverter.extractAndConvertValue(
                                payload, NotificationMessageEvent.class);
                    assertEquals(eventName, notificationMessageEvent.getTemplate().getEventType());
                  });
              return true;
            });
    consumer.close();
  }

  @Test
  void successfulJobConfirmation() {
    JobDispatchEvent jobDispatchEvent = JobEventRequestUtility.constructJobConfirmEventPayload();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(jobDispatchEvent.getEventName().value());
  }

  @Test
  void successfulJobConfirmationJobDispatch() {
    JobDispatchEvent jobDispatchEvent =
        JobEventRequestUtility.constructJobConfirmEventDispatchPayload();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);
    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generatePaymentMethodResponse()));
    when(bookingControllerApiSvc.getBookingProducts())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generateBookingProductResponse()));

    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(jobDispatchEvent.getEventName().value());
  }

  @Test
  void successfulJobConfirmPayload() {
    JobDispatchEvent jobDispatchEvent = JobEventRequestUtility.constructJobConfirmEventAppPayload();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generatePaymentMethodResponse()));
    when(bookingControllerApiSvc.getBookingProducts())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generateBookingProductResponse()));
    kafkaProducer.send(messageBuilder.build());
    validateKafkaAppNotificationJobResponse(jobDispatchEvent.getEventName().value());
  }

  @Test
  void errorOutJobConfirm() {
    JobDispatchEvent jobDispatchEvent = JobEventRequestUtility.constructJobConfirmEventErrorOut();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generatePaymentMethodResponse()));
    when(bookingControllerApiSvc.getBookingProducts())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generateBookingProductResponse()));
    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.IVD_NO_REQUIRED.getCode().toString(), null, Locale.getDefault()));
  }

  @Test
  void successfulJobOfferWithMultiStop() {

    JobDispatchEvent jobDispatchEvent = JobEventRequestUtility.constructJobOfferEvent();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(JobDispatchServiceUtility.paymentMethodResponse()));
    when(bookingControllerApiSvc.getBookingProducts())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generateBookingProductResponse()));
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(jobDispatchEvent.getEventName().value());
  }

  @Test
  void successfulJobOfferWithInvalidMultiStop() {

    JobDispatchEvent jobDispatchEvent = JobEventRequestUtility.constructJobOfferEvent();
    jobDispatchEvent.getMultiStop().setIntermediateAddr(null);
    jobDispatchEvent.getMultiStop().setIntermediateLat(null);
    jobDispatchEvent.getMultiStop().setIntermediateLng(null);
    // To cover platform fee applicability null scenario
    jobDispatchEvent.getPlatformFeeItem().get(0).setPlatformFeeApplicability(null);
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(JobDispatchServiceUtility.paymentMethodResponse()));
    when(bookingControllerApiSvc.getBookingProducts())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generateAlternateBookingProductResponse()));
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(jobDispatchEvent.getEventName().value());
  }

  @Test
  void successfulJobOfferDestLatAndDestLongZero() {
    // This test is designed to ensure coverage for the setDestinationDetails method in the
    // BeanToByteConverterImpl.java file.
    JobDispatchEvent jobDispatchEvent =
        JobEventRequestUtility.constructJobOfferEventDestLatAndDestLongZero();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(JobDispatchServiceUtility.paymentMethodResponse()));
    when(bookingControllerApiSvc.getBookingProducts())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generateBookingProductResponse()));
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(jobDispatchEvent.getEventName().value());
  }

  @Test
  void successfulJobOfferDestLatZeroDestLongNonZero() {
    // This test is designed to ensure coverage for the setDestinationDetails method in the
    // BeanToByteConverterImpl.java file.
    JobDispatchEvent jobDispatchEvent =
        JobEventRequestUtility.constructJobOfferEventDestLatZeroDestLongNonZero();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(JobDispatchServiceUtility.paymentMethodResponse()));
    when(bookingControllerApiSvc.getBookingProducts())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generateBookingProductResponse()));
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse(jobDispatchEvent.getEventName().value());
  }

  @Test
  void successfulJobOfferNotificationToAppPayload() {

    JobDispatchEvent jobDispatchEvent = JobEventRequestUtility.constructJobOfferEventAppPayload();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generatePaymentMethodResponse()));

    kafkaProducer.send(messageBuilder.build());
    validateKafkaAppNotificationJobResponse(jobDispatchEvent.getEventName().value());
  }

  @Test
  void errorOutJobOffer() {

    JobDispatchEvent jobDispatchEvent = JobEventRequestUtility.constructJobOfferEventErrorOut();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generatePaymentMethodResponse()));
    when(bookingControllerApiSvc.getBookingProducts())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generateBookingProductResponse()));
    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.IVD_NO_REQUIRED.getCode().toString(), null, Locale.getDefault()));
  }

  @Test
  void errorOutDueToInvalidJobStatus() {
    JobDispatchEvent jobDispatchEvent = JobEventRequestUtility.constructJobOfferEvent();
    jobDispatchEvent.setJobStatus(JobDispatchEvent.JobStatusEnum.NEW);
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(JobDispatchServiceUtility.paymentMethodResponse()));
    when(bookingControllerApiSvc.getBookingProducts())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generateBookingProductResponse()));
    kafkaProducer.send(messageBuilder.build());
    validateKafkaErrorOut(
        messageSource.getMessage(
            ErrorCode.INVALID_JOB_STATUS.getCode().toString(), null, Locale.getDefault()));
  }

  @Test
  void successfulJobOfferWithMultiStop_additionalCharge() {

    JobDispatchEvent jobDispatchEvent =
        JobEventRequestUtility.constructJobOfferEventWithAdditionalCharges();
    MessageBuilder<JobDispatchEvent> messageBuilder =
        MessageBuilder.withPayload(jobDispatchEvent).setHeader(KafkaHeaders.TOPIC, jobEventTopic);

    when(paymentMethodApiSvc.getPaymentMethodList())
        .thenReturn(Mono.just(JobDispatchServiceUtility.paymentMethodResponse()));
    when(bookingControllerApiSvc.getBookingProducts())
        .thenReturn(Mono.just(EsbJobEventRequestUtility.generateBookingProductResponse()));
    kafkaProducer.send(messageBuilder.build());
    validateKafkaIvdJobResponse_withAdditionalCharges(jobDispatchEvent.getEventName().value());
  }
}
