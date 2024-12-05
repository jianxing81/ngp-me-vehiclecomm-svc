package com.cdg.pmg.ngp.me.vehiclecomm.application.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.FareTariffResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.FareTariff;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    builder = @Builder(disableBuilder = true),
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FareTariffMapper {

  /**
   * Method to map FareTariffResponse to FareTariff
   *
   * @param fareTariffResponse fareTariffResponse
   * @return FareTariff
   */
  FareTariff fareTariffResponseToFareTariff(FareTariffResponse fareTariffResponse);
}
