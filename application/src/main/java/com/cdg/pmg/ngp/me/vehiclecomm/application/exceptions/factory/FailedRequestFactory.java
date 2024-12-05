package com.cdg.pmg.ngp.me.vehiclecomm.application.exceptions.factory;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommApplicationCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommFailedRequest;

/** Interface for a factory that creates failed request objects. */
public interface FailedRequestFactory {
  VehicleCommFailedRequest createFailedRequest(
      VehicleCommApplicationCommand command, String message, String topic);
}
