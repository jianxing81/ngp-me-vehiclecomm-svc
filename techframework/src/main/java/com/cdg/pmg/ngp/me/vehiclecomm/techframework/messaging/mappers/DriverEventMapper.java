package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.Driver;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs.properties.RefreshableProperties;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.driverevent.DriverEvent;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    builder = @Builder(disableBuilder = true),
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DriverEventMapper {
  @Mapping(source = "driverEvent.commandType", target = "driverEventRequest.commandType")
  @Mapping(source = "driverEvent.commandVariable", target = "driverEventRequest.commandVariable")
  @Mapping(source = "driverEvent.vehicleSuspends", target = "driverEventRequest.vehicleSuspends")
  @Mapping(source = "driverEvent.eventIdentifier", target = "driverEventRequest.eventIdentifier")
  Driver driverEventToDriverInboundEvent(
      DriverEvent driverEvent, RefreshableProperties refreshableProperties);
}
