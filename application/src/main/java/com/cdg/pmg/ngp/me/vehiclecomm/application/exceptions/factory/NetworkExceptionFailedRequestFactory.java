package com.cdg.pmg.ngp.me.vehiclecomm.application.exceptions.factory;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommApplicationCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommFailedRequest;
import org.springframework.stereotype.Component;

/** Class for creating failed request objects specific to NetworkException. */
@Component
public class NetworkExceptionFailedRequestFactory implements FailedRequestFactory {

  /**
   * Creates a failed request object for NetworkException.
   *
   * @param command command associated with the failed request.
   * @param message The message describing the failure.
   * @param topic The kafka topic related to the failed request.
   * @return A VehicleCommFailedRequest object representing the failed request.
   */
  @Override
  public VehicleCommFailedRequest createFailedRequest(
      VehicleCommApplicationCommand command, String message, String topic) {
    return VehicleCommFailedRequest.builder()
        .requestObject(command)
        .message(message)
        .topic(topic)
        .build();
  }
}
