package com.cdg.pmg.ngp.me.vehiclecomm.application.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
public class PaymentMethodResponse implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  private List<PaymentMethodData> data;
}
