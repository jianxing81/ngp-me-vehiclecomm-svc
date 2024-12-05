package com.cdg.pmg.ngp.me.vehiclecomm.application.exceptions.handlers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.exceptions.factory.FailedRequestFactory;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.service.SchedulerTransactionService;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.messaging.VehicleCommChannelProducer;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.NetworkException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommApplicationCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommFailedRequest;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;

@ServiceComponent
@Slf4j
public class NetworkExceptionHandler implements ApplicationExceptionHandler<NetworkException> {

  private final VehicleCommChannelProducer producer;

  private final MessageSource messageSource;
  private final FailedRequestFactory failedRequestFactory;
  private final SchedulerTransactionService schedulerTransactionService;

  @Autowired
  public NetworkExceptionHandler(
      VehicleCommChannelProducer producer,
      MessageSource messageSource,
      @Qualifier("networkExceptionFailedRequestFactory") FailedRequestFactory failedRequestFactory,
      SchedulerTransactionService schedulerTransactionService) {
    this.failedRequestFactory = failedRequestFactory;
    this.messageSource = messageSource;
    this.producer = producer;
    this.schedulerTransactionService = schedulerTransactionService;
  }

  @Override
  public void handleException(
      NetworkException networkException, VehicleCommApplicationCommand command, String topic) {
    try {
      String message =
          messageSource.getMessage(
              networkException.getErrorCode().toString(), null, Locale.getDefault());
      if (StringUtils.isNotBlank(networkException.getMessage())) {
        message = message.concat(" - ").concat(networkException.getMessage());
      }
      schedulerTransactionService.insertOrUpdateRetryEntity(command.getRetryDomainEntity());
      VehicleCommFailedRequest failEvent =
          failedRequestFactory.createFailedRequest(command, message, topic);
      producer.sendFailedEvent(failEvent);
    } catch (Exception e) {
      log.error("Exception in handling network exception ", e);
    }
  }

  /**
   * @return - Returns Class of Network Exception
   */
  @Override
  public Class<NetworkException> getSupportedException() {
    return NetworkException.class;
  }
}
