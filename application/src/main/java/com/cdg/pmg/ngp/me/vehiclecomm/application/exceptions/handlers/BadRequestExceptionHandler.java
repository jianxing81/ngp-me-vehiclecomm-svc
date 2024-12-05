package com.cdg.pmg.ngp.me.vehiclecomm.application.exceptions.handlers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.exceptions.factory.FailedRequestFactory;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.messaging.VehicleCommChannelProducer;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.BadRequestException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommApplicationCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommFailedRequest;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;

@ServiceComponent
@Slf4j
public class BadRequestExceptionHandler
    implements ApplicationExceptionHandler<BadRequestException> {
  private final VehicleCommChannelProducer producer;
  private final MessageSource messageSource;
  private final FailedRequestFactory failedRequestFactory;

  @Autowired
  public BadRequestExceptionHandler(
      VehicleCommChannelProducer producer,
      MessageSource messageSource,
      @Qualifier("badRequestExceptionFailedRequestFactory")
          FailedRequestFactory failedRequestFactory) {
    this.failedRequestFactory = failedRequestFactory;
    this.messageSource = messageSource;
    this.producer = producer;
  }

  @Override
  public void handleException(
      BadRequestException badRequestException,
      VehicleCommApplicationCommand command,
      String topic) {
    try {
      String message =
          messageSource.getMessage(
              badRequestException.getErrorCode().toString(), null, Locale.getDefault());
      if (badRequestException.getArguments() != null) {
        message = String.format(message, badRequestException.getArguments());
      }
      VehicleCommFailedRequest failEvent =
          failedRequestFactory.createFailedRequest(command, message, topic);
      producer.sendFailedEvent(failEvent);
    } catch (Exception e) {
      log.error("[handleException] Exception in handling bad Request ", e);
    }
  }

  /**
   * @return - Returns Class of BadRequest Exception
   */
  @Override
  public Class<BadRequestException> getSupportedException() {
    return BadRequestException.class;
  }
}
