package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RooftopMessages implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  private String message;
  private String colour;
  private Boolean scroll;
  private String speed;
  private String bitmap;
}
