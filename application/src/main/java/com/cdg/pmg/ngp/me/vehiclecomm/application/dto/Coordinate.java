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
public class Coordinate implements Serializable {
  @Serial private static final long serialVersionUID = -1721399027828741901L;
  private Double latitude;
  private Double longitude;
}
