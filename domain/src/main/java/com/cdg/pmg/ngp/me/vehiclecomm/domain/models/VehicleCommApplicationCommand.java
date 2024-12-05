package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.entities.RetryDomainEntity;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.message.DomainEvent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

public interface VehicleCommApplicationCommand extends Serializable {
  Class<? extends DomainEvent> fetchFailedEventClass();

  @JsonIgnore
  default RetryDomainEntity getRetryDomainEntity() {
    return null;
  }
}
