package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.bytetobean;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.GenericByteData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.GenericByteToBeanStrategy;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.GenericByteToBeanMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.internal.ByteToBeanConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ByteToBeanEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JobAcceptConverter implements GenericByteToBeanStrategy {

  private final ByteToBeanConverter byteToBeanConverter;
  private final GenericByteToBeanMapper genericByteToBeanMapper;

  /**
   * Converts byte data to java bean
   *
   * @param hexString hexString
   * @return GenericByteData
   */
  @Override
  public GenericByteData convert(String hexString) {
    return genericByteToBeanMapper.byteDataRepresentationToGenericByteData(
        byteToBeanConverter.jobAcceptConverter(hexString));
  }

  /**
   * Returns event name
   *
   * @return ByteToBeanEvent
   */
  @Override
  public ByteToBeanEvent getEventId() {
    return ByteToBeanEvent.JOB_ACCEPT;
  }
}
