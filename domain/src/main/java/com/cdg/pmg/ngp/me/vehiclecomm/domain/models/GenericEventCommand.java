package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.message.DomainEvent;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericEventCommand implements VehicleCommApplicationCommand {
  @Serial private static final long serialVersionUID = -7725773171700538447L;

  private byte[] byteArray;
  private Integer byteArraySize;
  private String byteArrayMessage;

  @Override
  public Class<? extends DomainEvent> fetchFailedEventClass() {
    return VehicleCommFailedRequest.class;
  }
}
