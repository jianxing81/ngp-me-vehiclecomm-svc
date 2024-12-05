package com.cdg.pmg.ngp.me.vehiclecomm.domain.models;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.message.DomainEvent;
import java.io.Serial;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class IvdResponseData extends DomainEvent {

  @Serial private static final long serialVersionUID = 7816611451460267759L;

  private String eventIdentifier;
  private String message;
  private Integer ivdNo;
  private LocalDateTime eventDate;
}
