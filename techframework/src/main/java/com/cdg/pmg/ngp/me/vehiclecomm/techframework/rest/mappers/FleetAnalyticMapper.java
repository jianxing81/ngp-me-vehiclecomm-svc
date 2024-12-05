package com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.DriverPerformanceHistoryResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.rest.fleetAnalytic.client.models.DriverPerformanceHistoryData;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FleetAnalyticMapper {

  List<DriverPerformanceHistoryResponse> mapToDriverPerformanceHistoryData(
      List<DriverPerformanceHistoryData> driverPerformanceHistoryData);
}
