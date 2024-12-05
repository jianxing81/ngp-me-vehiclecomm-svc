package com.cdg.pmg.ngp.me.vehiclecomm.application.exceptions.handlers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.exceptions.factory.FailedRequestFactory;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.messaging.VehicleCommChannelProducer;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.IvdConversionException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommApplicationCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommFailedRequest;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;

@ServiceComponent
@Slf4j
public class IvdConversionExceptionHandler
    implements ApplicationExceptionHandler<IvdConversionException> {

  private final VehicleCommChannelProducer producer;

  private final MessageSource messageSource;

  private final FailedRequestFactory failedRequestFactory;

  @Autowired
  public IvdConversionExceptionHandler(
      VehicleCommChannelProducer producer,
      MessageSource messageSource,
      @Qualifier("ivdConversionExceptionFailedRequestFactory")
          FailedRequestFactory failedRequestFactory) {
    this.failedRequestFactory = failedRequestFactory;
    this.messageSource = messageSource;
    this.producer = producer;
  }

  @Override
  public void handleException(
      IvdConversionException ivdConversionException,
      VehicleCommApplicationCommand command,
      String topic) {
    try {
      String message =
          messageSource.getMessage(
              ivdConversionException.getErrorCode().toString(), null, Locale.getDefault());
      if (ivdConversionException.getArguments() != null) {
        message = String.format(message, ivdConversionException.getArguments());
      }
      VehicleCommFailedRequest failEvent =
          failedRequestFactory.createFailedRequest(command, message, topic);
      producer.sendFailedEvent(failEvent);
    } catch (Exception e) {
      log.error("[handleException] Exception in handling Ivd Conversion ", e);
    }
  }

  /**
   * @return - Returns Class of ivdConversion Exception
   */
  @Override
  public Class<IvdConversionException> getSupportedException() {
    return IvdConversionException.class;
  }
}
