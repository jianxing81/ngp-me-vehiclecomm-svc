package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.EsbJob;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs.properties.RefreshableProperties;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.esbjobevent.EsbJobEvent;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    builder = @Builder(disableBuilder = true),
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IvdEventMapper {

  @Mapping(source = "esbJobEvent.occurredAt", target = "ivdMessageRequest.occurredAt")
  @Mapping(source = "esbJobEvent.eventIdentifier", target = "ivdMessageRequest.eventIdentifier")
  @Mapping(source = "esbJobEvent.eventId", target = "ivdMessageRequest.eventId")
  @Mapping(source = "esbJobEvent.eventDate", target = "ivdMessageRequest.eventDate")
  @Mapping(source = "esbJobEvent.message", target = "ivdMessageRequest.message")
  @Mapping(source = "esbJobEvent.retryId", target = "ivdMessageRequest.retryId")
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
  EsbJob esbJobEventToIvdInboundEvent(
      EsbJobEvent esbJobEvent, RefreshableProperties refreshableProperties);
}
