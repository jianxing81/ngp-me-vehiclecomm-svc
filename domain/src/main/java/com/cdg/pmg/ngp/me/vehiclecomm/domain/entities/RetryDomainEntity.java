package com.cdg.pmg.ngp.me.vehiclecomm.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetryDomainEntity {
  private Long retryId;
  private UUID eventId;
  private String eventIdentifier;
  private LocalDateTime occurredAt;
  private LocalDateTime eventDate;
  private String message;
}
