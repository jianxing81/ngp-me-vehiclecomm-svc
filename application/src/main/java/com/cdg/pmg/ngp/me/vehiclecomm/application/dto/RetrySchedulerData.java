package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** This class will have the data required for event retry logic */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class RetrySchedulerData {

  private Long id;
  private String eventName;
  private Payload payload;
  private String remarks;
  private RetryStatus status;
  private LocalDateTime createdDt;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Payload {
    private String eventType;
    private UUID eventId;
    private LocalDateTime occurredAt;
    private String eventIdentifier;
    private LocalDateTime eventDate;
    private String message;
  }

  public enum RetryStatus {
    PENDING,
    SUCCESS,
    FAILED
  }
}
