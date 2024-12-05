package com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.AggregateRoot;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.entities.DriverEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.MessageTypeEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.GenericEventCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.CmsConfiguration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Driver extends AggregateRoot<String> {
  private DriverEventRequest driverEventRequest;
  private transient CmsConfiguration cmsConfiguration;
  private GenericEventCommand genericEventCommand;
  private MessageTypeEnum messageTypeEnum;

  @Override
  public boolean equals(final Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
