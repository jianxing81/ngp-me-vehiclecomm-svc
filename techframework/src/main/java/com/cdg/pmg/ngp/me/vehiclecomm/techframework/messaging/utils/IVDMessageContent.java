package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.BytesOrder;
import java.io.Serial;
import java.io.Serializable;

public class IVDMessageContent extends IVDAbstractBytesHolder implements Serializable {
  @Serial private static final long serialVersionUID = 1L;

  public IVDMessageContent() {
    this(BytesOrder.LITTLE_ENDIAN);
  }

  public IVDMessageContent(byte[] bytes) {
    this(bytes, BytesOrder.LITTLE_ENDIAN);
  }

  public IVDMessageContent(byte[] bytes, BytesOrder order) {
    super(bytes, order);
  }

  public IVDMessageContent(BytesOrder order) {
    super(order);
  }
}
