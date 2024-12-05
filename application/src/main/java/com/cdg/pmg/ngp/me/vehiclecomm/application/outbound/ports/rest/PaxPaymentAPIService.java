package com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.PaymentMethodData;
import java.util.List;
import java.util.Optional;

/* Internal class for wrapper API calls to Pax Payment Service*/
public interface PaxPaymentAPIService {
  Optional<List<PaymentMethodData>> getPaymentMethodList();

  PaymentMethodData getPaymentMethodData(String paymentMethod);

  PaymentMethodData getPaymentMethodData(Integer meCode);
}
