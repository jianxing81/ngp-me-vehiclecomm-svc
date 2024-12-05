package com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.mappers;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.models.IvdPingMessageRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.web.providers.models.IvdPingMessage;
import java.util.Collections;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(imports = Collections.class, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SendPingMessageRequestMapper {

  /**
   * Method for mapping IvdPingMessage To IvdPingMessageRequest
   *
   * @param ivdPingMessage ivdPingMessage
   * @return IvdPingMessageRequest
   */
  IvdPingMessageRequest mapToIvdPingMessageRequest(IvdPingMessage ivdPingMessage);
}
