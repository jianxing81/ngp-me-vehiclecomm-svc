package com.cdg.pmg.ngp.me.vehiclecomm.application.exceptions.handlers;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.DomainException;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommApplicationCommand;

public interface ApplicationExceptionHandler<T extends DomainException> {

  void handleException(T exception, VehicleCommApplicationCommand command, String topic);

  Class<T> getSupportedException();
}
