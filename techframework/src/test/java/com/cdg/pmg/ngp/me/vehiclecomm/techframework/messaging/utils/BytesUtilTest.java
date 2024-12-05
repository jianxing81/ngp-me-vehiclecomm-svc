package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.BytesSize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BytesUtilTest {

  @Test
  void testToCheckUTextOfToDecimal() {
    byte[] bytes = {10, 20, 10, 40};
    assertNotNull(BytesUtil.toDecimal(bytes, BytesSize.UTEXT));
  }
}
