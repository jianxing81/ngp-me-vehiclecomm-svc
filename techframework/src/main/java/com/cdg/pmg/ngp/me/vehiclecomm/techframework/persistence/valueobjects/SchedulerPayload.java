package com.cdg.pmg.ngp.me.vehiclecomm.techframework.persistence.valueobjects;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** This class has the data to be stored in scheduler_retry table as the payload */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SchedulerPayload implements Serializable {
  @Serial private static final long serialVersionUID = 2L;
  private String eventType;
  private String eventId;
  private LocalDateTime occurredAt;
  private String eventIdentifier;
  private LocalDateTime eventDate;
  private String message;
}
