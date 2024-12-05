package com.cdg.pmg.ngp.me.vehiclecomm.domain.entities;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.message.DomainEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommApplicationCommand;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.VehicleCommFailedRequest;
import jakarta.validation.constraints.NotBlank;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IvdVehicleEventMessageRequest implements VehicleCommApplicationCommand {
  @Serial private static final long serialVersionUID = 1L;
  private UUID eventId;
  private LocalDateTime occurredAt;
  private String eventIdentifier;
  private LocalDateTime eventDate;

  @NotBlank(message = "Message is required")
  private String message;

  @Override
  public Class<? extends DomainEvent> fetchFailedEventClass() {
    return VehicleCommFailedRequest.class;
  }
}
