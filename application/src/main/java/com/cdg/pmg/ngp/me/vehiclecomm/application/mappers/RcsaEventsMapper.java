package com.cdg.pmg.ngp.me.vehiclecomm.application.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.VehicleDetailsResponse;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.Rcsa;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.ByteDataRepresentation;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
    builder = @Builder(disableBuilder = true),
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RcsaEventsMapper {

  /**
   * Maps byteDataRepresentation to aggregate root
   *
   * @param rcsa Rcsa
   * @param byteDataRepresentation byteDataRepresentation
   */
  @Mapping(source = "byteDataRepresentation.ivdNo", target = "rcsa.byteData.ivdNo")
  @Mapping(source = "byteDataRepresentation.voiceStreamArray", target = "rcsa.byteData.voiceStream")
  @Mapping(source = "byteDataRepresentation.serialNumber", target = "rcsa.byteData.serialNumber")
  @Mapping(source = "byteDataRepresentation.messageId", target = "rcsa.byteData.messageId")
  @Mapping(source = "byteDataRepresentation.eventTime", target = "rcsa.byteData.eventTime")
  @Mapping(source = "byteDataRepresentation.jobNo", target = "rcsa.byteData.jobNo")
  @Mapping(source = "byteDataRepresentation.emergencyId", target = "rcsa.byteData.emergencyId")
  @Mapping(source = "byteDataRepresentation.driverId", target = "rcsa.byteData.driverId")
  @Mapping(
      source = "byteDataRepresentation.offsetLatitude",
      target = "rcsa.byteData.offsetLatitude")
  @Mapping(
      source = "byteDataRepresentation.offsetLongitude",
      target = "rcsa.byteData.offsetLongitude")
  @Mapping(source = "byteDataRepresentation.speed", target = "rcsa.byteData.speed")
  @Mapping(source = "byteDataRepresentation.direction", target = "rcsa.byteData.direction")
  @Mapping(source = "byteDataRepresentation.uniqueMsgId", target = "rcsa.byteData.uniqueMsgId")
  @Mapping(source = "byteDataRepresentation.selection", target = "rcsa.byteData.selection")
  @Mapping(
      source = "byteDataRepresentation.messageContent",
      target = "rcsa.byteData.messageContent")
  @Mapping(source = "byteDataRepresentation.ackRequired", target = "rcsa.byteData.ackRequired")
  void byteDataRepresentationToRcsaInboundEvent(
      @MappingTarget Rcsa rcsa, ByteDataRepresentation byteDataRepresentation);

  @Mapping(source = "vehicleDetailsResponse.vehicleId", target = "rcsa.vehicleDetails.id")
  @Mapping(source = "vehicleDetailsResponse.ipAddress", target = "rcsa.vehicleDetails.ipAddress")
  @Mapping(source = "vehicleDetailsResponse.imsi", target = "rcsa.vehicleDetails.imsi")
  @Mapping(source = "vehicleDetailsResponse.driverId", target = "rcsa.driverId")
  void vehicleDetailsResponseToRcsaInboundEvent(
      @MappingTarget Rcsa rcsa, VehicleDetailsResponse vehicleDetailsResponse);
}
