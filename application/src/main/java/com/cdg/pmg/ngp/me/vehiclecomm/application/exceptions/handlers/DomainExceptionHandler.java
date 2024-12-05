package com.cdg.pmg.ngp.me.vehiclecomm.application.exceptions.handlers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.exceptions.factory.FailedRequestFactory;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.messaging.VehicleCommChannelProducer;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.DomainException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommApplicationCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommFailedRequest;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;

@ServiceComponent
@Slf4j
public class DomainExceptionHandler implements ApplicationExceptionHandler<DomainException> {

  private final VehicleCommChannelProducer producer;

  private final MessageSource messageSource;

  private final FailedRequestFactory failedRequestFactory;

  @Autowired
  public DomainExceptionHandler(
      VehicleCommChannelProducer producer,
      MessageSource messageSource,
      @Qualifier("domainExceptionFailedRequestFactory") FailedRequestFactory failedRequestFactory) {
    this.failedRequestFactory = failedRequestFactory;
    this.messageSource = messageSource;
    this.producer = producer;
  }

  @Override
  public void handleException(
      DomainException domainException, VehicleCommApplicationCommand command, String topic) {
    try {
      AtomicReference<String> message =
          new AtomicReference<>(
              messageSource.getMessage(
                  domainException.getErrorCode().toString(), null, Locale.getDefault()));
      Optional.ofNullable(domainException.getArguments())
          .ifPresent(arg -> message.set(String.format(message.get(), arg)));
      if (StringUtils.isNotBlank(domainException.getMessage())) {
        message.set(message.get().concat(" - ").concat(domainException.getMessage()));
      }
      VehicleCommFailedRequest failEvent =
          failedRequestFactory.createFailedRequest(command, message.get(), topic);
      producer.sendFailedEvent(failEvent);
    } catch (Exception e) {
      log.error("[handleException] Exception in handling bad Request ", e);
    }
  }

  @Override
  public Class<DomainException> getSupportedException() {
    return DomainException.class;
  }
}
