package com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.FareTariffRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.FareTariffResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fare.client.models.IvdFareRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fare.client.models.IvdFareResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FareMapper {

  IvdFareRequest mapToIvdFareRequest(FareTariffRequest fareTariffRequest);

  FareTariffResponse mapToFareTariffResponse(IvdFareResponse response);
}
