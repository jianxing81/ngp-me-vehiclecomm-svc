package com.cdg.pmg.ngp.me.vehiclecomm.application.exceptions.handlers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.Error;
import com.cdg.pmg.ngp.me.vehiclecomm.application.exceptions.factory.FailedRequestFactory;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.messaging.VehicleCommChannelProducer;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.ConstraintException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommApplicationCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommFailedRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;

@ServiceComponent
@Slf4j
public class ConstraintExceptionHandler
    implements ApplicationExceptionHandler<ConstraintException> {

  private final VehicleCommChannelProducer producer;

  private final FailedRequestFactory failedRequestFactory;

  @Autowired
  public ConstraintExceptionHandler(
      VehicleCommChannelProducer producer,
      MessageSource messageSource,
      @Qualifier("constraintExceptionFailedRequestFactory")
          FailedRequestFactory failedRequestFactory) {
    this.failedRequestFactory = failedRequestFactory;
    this.producer = producer;
  }

  @Override
  public void handleException(
      ConstraintException constraintException,
      VehicleCommApplicationCommand command,
      String topic) {
    try {
      Throwable throwable = constraintException.getThrowable();
      if (throwable instanceof ConstraintViolationException violationException) {
        List<Error.ErrorDetail.FieldError> fieldErrors =
            violationException.getConstraintViolations().stream()
                .map(this::createFieldError)
                .toList();
        String message =
            fieldErrors.stream()
                .map(Error.ErrorDetail.FieldError::getMessages)
                .flatMap(Collection::stream)
                .toList()
                .toString();
        VehicleCommFailedRequest failEvent =
            failedRequestFactory.createFailedRequest(command, message, topic);
        producer.sendFailedEvent(failEvent);
      }
    } catch (Exception e) {
      log.error("[handleException] Exception in handling Constraint validation ", e);
    }
  }

  private Error.ErrorDetail.FieldError createFieldError(ConstraintViolation<?> violation) {
    return Error.ErrorDetail.FieldError.builder()
        .name(violation.getPropertyPath().toString())
        .messages(List.of(violation.getMessage()))
        .build();
  }

  /**
   * @return - Returns Class of Constraint Exception
   */
  @Override
  public Class<ConstraintException> getSupportedException() {
    return ConstraintException.class;
  }
}
