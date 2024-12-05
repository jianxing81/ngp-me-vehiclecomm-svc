package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.RcsaMessage;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.configs.properties.RefreshableProperties;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.listener.models.rcsamessageevent.RcsaMessageEvent;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    builder = @Builder(disableBuilder = true),
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RcsaMessageEventMapper {
  @Mapping(source = "rcsaMessageEvent.ivdNo", target = "rcsaMessageEventRequest.ivdNo")
  @Mapping(source = "rcsaMessageEvent.driverId", target = "rcsaMessageEventRequest.driverId")
  @Mapping(source = "rcsaMessageEvent.vehicleId", target = "rcsaMessageEventRequest.vehicleId")
  @Mapping(source = "rcsaMessageEvent.deviceType", target = "rcsaMessageEventRequest.deviceType")
  @Mapping(source = "rcsaMessageEvent.ipAddr", target = "rcsaMessageEventRequest.ipAddr")
  @Mapping(source = "rcsaMessageEvent.msgId", target = "rcsaMessageEventRequest.msgId")
  @Mapping(
      source = "rcsaMessageEvent.messageSerialNo",
      target = "rcsaMessageEventRequest.messageSerialNo")
  @Mapping(
      source = "rcsaMessageEvent.requestServiceType",
      target = "rcsaMessageEventRequest.requestServiceType")
  @Mapping(
      source = "rcsaMessageEvent.canMessageId",
      target = "rcsaMessageEventRequest.canMessageId")
  @Mapping(
      source = "rcsaMessageEvent.commandVariable",
      target = "rcsaMessageEventRequest.commandVariable")
  @Mapping(source = "rcsaMessageEvent.msgContent", target = "rcsaMessageEventRequest.msgContent")
  @Mapping(source = "rcsaMessageEvent.messageType", target = "rcsaMessageEventRequest.messageType")
  @Mapping(
      source = "refreshableProperties.storeForwardEvents",
      target = "cmsConfiguration.storeForwardEvents")
  RcsaMessage rcsaMessageEventToRcsaMessageInboundEvent(
      RcsaMessageEvent rcsaMessageEvent, RefreshableProperties refreshableProperties);
}
