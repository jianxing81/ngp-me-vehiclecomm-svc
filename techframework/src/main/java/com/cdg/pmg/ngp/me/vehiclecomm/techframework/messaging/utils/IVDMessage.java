package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.BytesOrder;
import java.io.Serial;
import java.io.Serializable;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;

/** Class to represent ICD message (both incoming and outgoing). */
public class IVDMessage extends IVDAbstractBytesHolder implements Serializable {

  protected static final int HEADER_START_IDX = 0;
  @Serial private static final long serialVersionUID = 1L;

  public static IVDMessage newInstance(byte[] bytes, IVDHeaderType headerType) {
    return IVDMessage.newInstance(bytes, headerType, BytesOrder.LITTLE_ENDIAN);
  }

  public static IVDMessage newInstance(byte[] bytes, IVDHeaderType headerType, BytesOrder order) {
    IVDMessage message = new IVDMessage();
    message.order = order;
    message.headerType = headerType;
    message.parseBytes(bytes);
    return message;
  }

  /** -- GETTER -- Returns instance of the message header. */
  @Getter private IVDMessageHeader header;

  protected IVDHeaderType headerType;

  /** Constructor for parsing TLV message bytes with default BytesOrder#LITTLE_ENDIAN format. */
  public IVDMessage(byte[] bytes) {
    this(bytes, BytesOrder.LITTLE_ENDIAN);
  }

  public IVDMessage() {
    this(BytesOrder.LITTLE_ENDIAN);
  }

  /** Constructor for parsing TLV message bytes, specifying BytesOrder#LITTLE_ENDIAN format. */
  public IVDMessage(byte[] bytes, BytesOrder order) {
    super(bytes, order);
  }

  @Override
  protected void parseBytes(byte[] bytes) {
    IVDMessageType messageType = IVDMessageType.getType(bytes[0] & 0xFF);

    int headerLength =
        (messageType.isFromIVD()
            ? IVDHeaderType.COMM_RPTDATA_TSTMP.getLength()
            : IVDHeaderType.COMM_TSTMP.getLength());
    if (this.headerType != null) {
      headerLength = this.headerType.getLength();
    }

    byte[] headerBytes = ArrayUtils.subarray(bytes, IVDMessage.HEADER_START_IDX, headerLength);
    byte[] valueBytes = ArrayUtils.subarray(bytes, headerLength, bytes.length);
    this.header = new IVDMessageHeader(headerBytes);
    super.parseBytes(valueBytes);
  }

  public IVDMessage(BytesOrder order) {
    super(order);
    this.header = new IVDMessageHeader();
  }

  @Override
  public byte[] toHexBytes() {
    byte[] msgBytes = new byte[] {};
    msgBytes = BytesUtil.joinBytes(msgBytes, this.header.toHexBytes());
    msgBytes = BytesUtil.joinBytes(msgBytes, super.toHexBytes());
    return msgBytes;
  }
}
