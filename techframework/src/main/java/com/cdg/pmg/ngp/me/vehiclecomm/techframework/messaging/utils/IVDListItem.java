package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.BytesOrder;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.BytesSize;
import java.io.Serial;
import java.io.Serializable;

/** Class use to store TLV List items. */
public class IVDListItem extends IVDAbstractBytesHolder implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /**
   * Constructor to parse TLV list bytes array, specifying Byte Order.
   *
   * @param bytes bytes
   * @param order order
   */
  public IVDListItem(byte[] bytes, BytesOrder order) {
    super(bytes, order);
  }

  public void putUInt32(IVDFieldTag tag, long value) {
    this.putUInt32(tag, value, true);
  }

  public void putUInt32(IVDFieldTag tag, long value, boolean override) {
    this.put(tag, value, BytesSize.UINT32, override);
  }

  public IVDListItem() {
    this(BytesOrder.LITTLE_ENDIAN);
  }

  public IVDListItem(BytesOrder order) {
    super(order);
  }
}
