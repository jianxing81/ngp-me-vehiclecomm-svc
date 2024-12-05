package com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.PaymentMethodData;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.paxpaymentservice.client.models.PaymentsMethodData;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaxPaymentMapper {

  /**
   * Method to map payment method response
   *
   * @param paymentsMethodData - payment method response from API
   * @return - mapped response
   */
  List<PaymentMethodData> paymentMethodResponseMapper(List<PaymentsMethodData> paymentsMethodData);

  PaymentMethodData paymentMethodResponseToPaymentMethodData(PaymentsMethodData paymentsMethodData);
}
