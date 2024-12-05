package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request fields for calling the vehicle service */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMdtRequest implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private String eventType;
  private LocalDateTime zoneDate;
  private String shiftDestination;
  private String destinationCode;
}
