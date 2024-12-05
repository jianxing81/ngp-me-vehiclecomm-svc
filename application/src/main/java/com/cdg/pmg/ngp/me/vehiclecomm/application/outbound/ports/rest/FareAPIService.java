package com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.rest;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.FareTariffRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.FareTariffResponse;

/** Internal class for wrapper API calls to Fare Service */
public interface FareAPIService {

  /**
   * This method is used to call fare service
   *
   * @param fareTariffRequest FareTariffRequest
   * @return FareTariffResponse
   */
  FareTariffResponse fareTariff(FareTariffRequest fareTariffRequest);
}
