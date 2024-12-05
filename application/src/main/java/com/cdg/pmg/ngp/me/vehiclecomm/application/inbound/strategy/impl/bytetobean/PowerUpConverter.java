package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.impl.bytetobean;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.GenericByteData;
import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.MdtPowerUpApiRequest;
import com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy.GenericByteToBeanStrategy;
import com.cdg.pmg.ngp.me.vehiclecomm.application.mappers.GenericByteToBeanMapper;
import com.cdg.pmg.ngp.me.vehiclecomm.application.outbound.ports.internal.ByteToBeanConverter;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ByteToBeanEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PowerUpConverter implements GenericByteToBeanStrategy {

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
    MdtPowerUpApiRequest powerUpBean =
        byteToBeanConverter.convertPowerUpByteToBeanConverter(hexString, new byte[0]);
    powerUpBean.setIpAddr(byteToBeanConverter.extractMdtIpAddress(hexString));
    return genericByteToBeanMapper.mdtPowerUpApiRequestToGenericByteData(powerUpBean);
  }

  /**
   * Returns event name
   *
   * @return ByteToBeanEvent
   */
  @Override
  public ByteToBeanEvent getEventId() {
    return ByteToBeanEvent.POWER_UP;
  }
}
