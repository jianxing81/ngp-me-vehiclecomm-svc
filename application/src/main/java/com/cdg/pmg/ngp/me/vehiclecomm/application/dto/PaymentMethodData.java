package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodData implements Serializable {

  @Serial private static final long serialVersionUID = 1L;
  private String code;
  private String codeDesc;
  private Integer paymentMode;
  private Integer entryMode;
}
