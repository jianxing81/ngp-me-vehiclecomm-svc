package com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects;

import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Coordinate implements Serializable {

  @Serial private static final long serialVersionUID = 7725773171700538443L;
  private double value;
}
