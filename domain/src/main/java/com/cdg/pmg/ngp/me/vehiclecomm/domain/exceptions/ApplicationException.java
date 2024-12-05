package com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.message.DomainEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommApplicationCommand;
import lombok.Getter;

/** Class for application exception */
@Getter
public class ApplicationException extends RuntimeException {

  private final DomainException domainException;

  private final VehicleCommApplicationCommand command;

  private final Class<? extends DomainEvent> domainEventClass;

  private final String topic;

  public ApplicationException(
      DomainException domainException,
      VehicleCommApplicationCommand command,
      Class<? extends DomainEvent> domainEventClass,
      String topic) {
    super(domainException);
    this.domainException = domainException;
    this.domainEventClass = domainEventClass;
    this.command = command;
    this.topic = topic;
  }
}
