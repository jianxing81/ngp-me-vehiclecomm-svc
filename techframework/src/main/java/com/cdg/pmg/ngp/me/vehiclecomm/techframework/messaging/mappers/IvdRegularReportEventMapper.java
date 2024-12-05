package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbVehicle;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs.properties.RefreshableProperties;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.esbregularreportevent.EsbRegularReportEvent;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    builder = @Builder(disableBuilder = true),
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IvdRegularReportEventMapper {
  @Mapping(
      source = "esbRegularReportEvent.occurredAt",
      target = "ivdVehicleEventMessageRequest.occurredAt")
  @Mapping(
      source = "esbRegularReportEvent.eventIdentifier",
      target = "ivdVehicleEventMessageRequest.eventIdentifier")
  @Mapping(
      source = "esbRegularReportEvent.eventDate",
      target = "ivdVehicleEventMessageRequest.eventDate")
  @Mapping(
      source = "esbRegularReportEvent.message",
      target = "ivdVehicleEventMessageRequest.message")
  @Mapping(
      source = "refreshableProperties.offsetMultiplier",
      target = "cmsConfiguration.offsetMultiplier")
  @Mapping(
      source = "refreshableProperties.coordinateMultiplier",
      target = "cmsConfiguration.coordinateMultiplier")
  @Mapping(
      source = "refreshableProperties.longitudeOrigin",
      target = "cmsConfiguration.longitudeOrigin")
  @Mapping(
      source = "refreshableProperties.latitudeOrigin",
      target = "cmsConfiguration.latitudeOrigin")
  @Mapping(
      source = "refreshableProperties.storeForwardEvents",
      target = "cmsConfiguration.storeForwardEvents")
  EsbVehicle esbRegularReportEventToIvdInboundEvent(
      EsbRegularReportEvent esbRegularReportEvent, RefreshableProperties refreshableProperties);
}
