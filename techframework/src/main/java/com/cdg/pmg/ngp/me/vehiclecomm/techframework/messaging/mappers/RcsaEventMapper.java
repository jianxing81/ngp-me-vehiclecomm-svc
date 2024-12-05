package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.Rcsa;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs.properties.RefreshableProperties;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.rcsaevent.RcsaEvent;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    builder = @Builder(disableBuilder = true),
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RcsaEventMapper {
  @Mapping(source = "rcsaEvent.occurredAt", target = "rcsaMessageRequest.occurredAt")
  @Mapping(source = "rcsaEvent.eventIdentifier", target = "rcsaMessageRequest.eventIdentifier")
  @Mapping(source = "rcsaEvent.message", target = "rcsaMessageRequest.message")
  @Mapping(source = "rcsaEvent.eventDate", target = "rcsaMessageRequest.eventDate")
  @Mapping(
      source = "refreshableProperties.storeForwardEvents",
      target = "cmsConfiguration.storeForwardEvents")
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
  Rcsa rcsaEventToInboundEvent(RcsaEvent rcsaEvent, RefreshableProperties refreshableProperties);
}
