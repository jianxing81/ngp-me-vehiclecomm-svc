package com.cdg.pmg.ngp.me.vehiclecomm.domain.message;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.utils.Helper;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

/** The type Domain event. */
@Getter
public abstract class DomainEvent implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  final UUID eventId = UUID.randomUUID();
  final LocalDateTime occurredAt = Helper.getCurrentTime();
}
