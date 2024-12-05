package com.cdg.pmg.ngp.me.vehiclecomm.application.inbound.strategy;

import com.cdg.pmg.ngp.me.vehiclecomm.application.dto.GenericByteData;
import com.cdg.pmg.ngp.me.vehiclecomm.domain.enums.ByteToBeanEvent;

/** This interface acts as a base for converting byte to bean */
public interface GenericByteToBeanStrategy {

  /**
   * Converts byte data to java bean
   *
   * @param hexString hexString
   * @return GenericByteData
   */
  GenericByteData convert(String hexString);

  /**
   * Returns event name
   *
   * @return ByteToBeanEvent
   */
  ByteToBeanEvent getEventId();
}
