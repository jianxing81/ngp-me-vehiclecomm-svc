package com.cdg.pmg.ngp.me.vehiclecomm.application.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.GenericByteData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtIvdDeviceConfigApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtLogOffApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtLogOnApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtPowerUpApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.GenericByteToBean;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.ByteDataRepresentation;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.valueobjects.ByteData;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    builder = @Builder(disableBuilder = true),
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GenericByteToBeanMapper {

  @Mapping(source = "isLoyaltyMember", target = "loyaltyMember")
  ByteData genericByteDataToGenericByteToBean(GenericByteData byteData);

  GenericByteData byteDataRepresentationToGenericByteData(
      ByteDataRepresentation byteDataRepresentation);

  @Mapping(source = "isAckRequired", target = "ackRequired")
  GenericByteData mdtLogonApiRequestToGenericByteData(MdtLogOnApiRequest mdtLogOnApiRequest);

  @Mapping(source = "isAckRequired", target = "ackRequired")
  GenericByteData mdtLogOffApiRequestToGenericByteData(MdtLogOffApiRequest mdtLogOffApiRequest);

  @Mapping(source = "isAckRequired", target = "ackRequired")
  @Mapping(source = "ipAddr", target = "ipAddress")
  @Mapping(source = "vehiclePlateNum", target = "vehiclePlateNumber")
  GenericByteData mdtPowerUpApiRequestToGenericByteData(MdtPowerUpApiRequest mdtPowerUpApiRequest);

  @Mapping(source = "isAckRequired", target = "ackRequired")
  GenericByteData ivdDeviceConfigRequestToGenericByteData(
      MdtIvdDeviceConfigApiRequest mdtIvdDeviceConfigApiRequest);

  default void genericByteDataToGenericByteToBean(
      GenericByteToBean genericByteToBean, GenericByteData genericByteData) {
    ByteData byteData = genericByteDataToGenericByteToBean(genericByteData);
    genericByteToBean.setByteData(byteData);
  }
}
