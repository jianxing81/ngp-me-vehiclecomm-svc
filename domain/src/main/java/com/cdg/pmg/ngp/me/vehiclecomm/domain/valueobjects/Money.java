package com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.*;

@Getter
@AllArgsConstructor
public class Money implements Serializable {

  @Serial private static final long serialVersionUID = 7725773171700538442L;
  private BigDecimal value;
}
