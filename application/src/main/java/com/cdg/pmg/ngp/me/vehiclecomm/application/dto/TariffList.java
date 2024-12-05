package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TariffList implements Serializable {
  @Serial private static final long serialVersionUID = 1L;
  private String tariffTypeCode;
  private Integer tariffUnit;
  private Double discountedTotalAmount;
  private String discountedCurrency;
  private String description;
  private BigDecimal fare;
  private BigDecimal levy;
}
