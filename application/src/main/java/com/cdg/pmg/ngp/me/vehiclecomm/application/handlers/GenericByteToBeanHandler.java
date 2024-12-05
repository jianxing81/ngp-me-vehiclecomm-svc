package com.cdg.pmg.ngp.me.vehiclecomm.application.handlers;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.GenericByteData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.GenericByteToBeanStrategy;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.GenericByteToBeanMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.utils.ObjectUtils;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.aggregateroots.derived.GenericByteToBean;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.annotations.ServiceComponent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ByteToBeanEvent;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ErrorCode;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.BadRequestException;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@ServiceComponent
@RequiredArgsConstructor
public class GenericByteToBeanHandler {

  private final Map<ByteToBeanEvent, GenericByteToBeanStrategy> byteToBeanStrategyMap;
  private final GenericByteToBeanMapper genericByteToBeanMapper;
  private final ObjectUtils objectUtils;

  /**
   * This method converts hex string to a java bean
   *
   * @param genericByteToBean genericByteToBean
   */
  public void handle(GenericByteToBean genericByteToBean) {
    ByteToBeanEvent eventId = ByteToBeanEvent.findById(genericByteToBean.getEventId());
    Optional.ofNullable(byteToBeanStrategyMap.getOrDefault(eventId, null))
        .ifPresentOrElse(
            converter -> {
              GenericByteData byteData = converter.convert(genericByteToBean.getHexString());
              mapByteDataRepresentationToGenericByteToBean(genericByteToBean, byteData);
            },
            () -> {
              throw new BadRequestException(ErrorCode.INVALID_EVENT_IDENTIFIER.getCode());
            });
  }

  private void mapByteDataRepresentationToGenericByteToBean(
      GenericByteToBean genericByteToBean, GenericByteData byteData) {
    genericByteToBeanMapper.genericByteDataToGenericByteToBean(genericByteToBean, byteData);
    Map<String, Object> byteDataMap = objectUtils.pojoToMap(genericByteToBean.getByteData());
    genericByteToBean.setResponseMap(byteDataMap);
  }
}
