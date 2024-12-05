package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.RcsaEvents;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.message.DomainEvent;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RcsaEventProducerData extends DomainEvent {

  @Serial private static final long serialVersionUID = 7816611451460267759L;

  private String eventIdentifier;
  private String voiceStream;
  private String emergencyId;
  private Integer ivdNo;
  private String vehicleId;
  private RcsaEvents rcsaEvent;
  private RcsaEventMessageData message;
  private String driverId;
}
