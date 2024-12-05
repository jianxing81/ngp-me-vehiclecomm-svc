package com.cdg.pmg.ngp.me.vehiclecomm.domain.entities;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.message.DomainEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommApplicationCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommFailedRequest;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IvdMessageRequest implements VehicleCommApplicationCommand {

  private UUID eventId;
  private LocalDateTime occurredAt;
  private String eventIdentifier;
  private LocalDateTime eventDate;
  private Long retryId;

  @NotBlank(message = "Message is required")
  private String message;

  @Override
  public Class<? extends DomainEvent> fetchFailedEventClass() {
    return VehicleCommFailedRequest.class;
  }

  @Override
  public RetryDomainEntity getRetryDomainEntity() {
    return RetryDomainEntity.builder()
        .eventId(this.eventId)
        .eventIdentifier(this.eventIdentifier)
        .eventDate(this.eventDate)
        .retryId(this.retryId)
        .message(this.message)
        .build();
  }
}
