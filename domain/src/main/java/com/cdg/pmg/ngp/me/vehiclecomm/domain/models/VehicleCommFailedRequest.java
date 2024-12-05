package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.message.DomainEvent;
import java.io.Serial;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleCommFailedRequest extends DomainEvent {

  @Serial private static final long serialVersionUID = 7816611451460267759L;

  private VehicleCommApplicationCommand requestObject;

  private String message;
  private String topic;
}
