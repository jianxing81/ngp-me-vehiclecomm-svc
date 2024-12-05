package com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.AggregateRoot;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.entities.RcsaMessageEventRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.MessageTypeEnum;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.GenericEventCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.CmsConfiguration;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RcsaMessage extends AggregateRoot<String> {

  private RcsaMessageEventRequest rcsaMessageEventRequest;
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
