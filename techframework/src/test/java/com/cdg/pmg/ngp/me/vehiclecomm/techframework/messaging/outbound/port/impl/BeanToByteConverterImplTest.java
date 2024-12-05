package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.outbound.port.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.cdg.pmg.ngp.me.vehiclecomm.domain.exceptions.IvdConversionException;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.internal.service.impl.BeanToByteConverterImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BeanToByteConverterImplTest {

  @InjectMocks private BeanToByteConverterImpl beanToByteConverter;

  @Test
  void testConvertToBytePingMessageResponseInvalid() {
    assertThrows(
        IvdConversionException.class, () -> beanToByteConverter.convertToBytePingMessage(35, null));
  }

  @Test
  void testConvertToByteConvertToStreetHailResponseInvalid() {
    assertThrows(IvdConversionException.class, () -> beanToByteConverter.convertToStreetHail(null));
  }
}
