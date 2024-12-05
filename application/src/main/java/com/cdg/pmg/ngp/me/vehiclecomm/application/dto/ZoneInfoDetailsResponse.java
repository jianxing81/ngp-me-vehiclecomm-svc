package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/** Response of zone info details from the MDT service */
public class ZoneInfoDetailsResponse implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String zoneId;
  private String zoneIvdDesc;
  private Integer roofTopIndex;
}
