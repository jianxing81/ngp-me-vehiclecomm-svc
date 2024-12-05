package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.BytesOrder;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.BytesSize;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

/** Class to represent message header content. */
@Slf4j
@Getter
@Setter
public class IVDMessageHeader implements Serializable {

  @Serial private static final long serialVersionUID = 1L;
  private boolean acknowledgement;
  private int direction;
  private int emergencyStatus;
  private boolean expressway;
  private boolean gpsFixStatus;
  private int ivdStatus;
  private int mobileId;
  private final BytesOrder order;
  private IVDMessageType type;
  private byte sequence;
  private int serialNum;
  private int speed;
  private int taxiMeterStatus;
  private boolean suspensionStatus;
  private long timestamp;
  private int offsetLatitude;
  private int offsetLongitude;
  private int zoneOrRank;

  /**
   * Constructor for generating message header, specifying Bytes Order.
   *
   * @param bytes - bytes
   */
  public IVDMessageHeader(byte[] bytes) {
    this(BytesOrder.LITTLE_ENDIAN);
    this.parseBytes(bytes);
  }

  /**
   * Sets message time-stamp via Date object.
   *
   * @param timestamp timestamp
   */
  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp.getTime();
  }

  /**
   * Sets message time-stamp
   *
   * @param timestamp
   */
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public IVDMessageHeader() {
    this(BytesOrder.LITTLE_ENDIAN);
  }

  /**
   * Constructor for parsing message header TLV bytes, specifying Bytes Order.
   *
   * @param order order
   */
  public IVDMessageHeader(BytesOrder order) {
    this.type = IVDMessageType.NONE;
    this.order = order;
    this.serialNum = 0;
    this.acknowledgement = false;
    this.mobileId = 0;
    this.offsetLatitude = 0;
    this.offsetLongitude = 0;
    this.speed = 0;
    this.direction = 0;
    this.expressway = false;
    this.gpsFixStatus = false;
    this.emergencyStatus = 0;
    this.ivdStatus = 0;
    this.suspensionStatus = false;
    this.zoneOrRank = 0;
    this.sequence = 0;
  }

  private void parseBytes(byte[] bytes) {
    try {

      int origLength = bytes.length;
      this.type = IVDMessageType.getType(bytes[0] & 0xFF);

      byte byte2 = bytes[1];
      byte ackBit = (byte) ((byte2 & 128) >> 7);
      this.serialNum = (byte) (byte2 & 127);
      this.acknowledgement = (ackBit == 1);

      byte[] byte3And4Bytes = ArrayUtils.subarray(bytes, 2, 2 + 2);
      if (this.order == BytesOrder.LITTLE_ENDIAN) {
        BytesUtil.reverseBytes(byte3And4Bytes);
      }
      this.mobileId = (Integer) BytesUtil.toDecimal(byte3And4Bytes, BytesSize.UINT16);

      int byteStart = 4;
      if (this.type.isFromIVD()
          || (this.type == IVDMessageType.NONE)
              && (origLength >= IVDHeaderType.COMM_RPTDATA.getLength())) {
        byte[] byte5And6Bytes = ArrayUtils.subarray(bytes, byteStart, byteStart + 2);
        if (this.order == BytesOrder.LITTLE_ENDIAN) {
          BytesUtil.reverseBytes(byte5And6Bytes);
        }
        this.offsetLongitude = (Integer) BytesUtil.toDecimal(byte5And6Bytes, BytesSize.UINT16);
        byteStart = byteStart + 2;

        byte[] byte7And8Bytes = ArrayUtils.subarray(bytes, byteStart, byteStart + 2);
        if (this.order == BytesOrder.LITTLE_ENDIAN) {
          BytesUtil.reverseBytes(byte7And8Bytes);
        }
        this.offsetLatitude = (Integer) BytesUtil.toDecimal(byte7And8Bytes, BytesSize.UINT16);
        byteStart = byteStart + 2;

        this.speed = bytes[byteStart] & 0xFF;
        byteStart++;

        byte byte10 = bytes[byteStart];
        byte emergencyBits = (byte) ((byte10 & 192) >> 6);
        byte gpsStatBits = (byte) ((byte10 & 32) >> 5);
        byte expressWayBits = (byte) ((byte10 & 16) >> 4);
        byte directionBits = (byte) (byte10 & 15);
        this.emergencyStatus = emergencyBits;
        this.gpsFixStatus = (gpsStatBits == 1);
        this.expressway = (expressWayBits == 1);
        byteStart++;

        byte byte11 = bytes[byteStart];
        byte direction5thBit = (byte) ((byte11 & 128) >> 3);
        byte meterStatBits = (byte) ((byte11 & 96) >> 5);
        byte suspensionBit = (byte) ((byte11 & 16) >> 4);
        byte ivdBits = (byte) (byte11 & 15);
        this.direction = (byte) (direction5thBit | directionBits);
        this.taxiMeterStatus = meterStatBits;
        this.suspensionStatus = (suspensionBit == 1);
        this.ivdStatus = ivdBits;
        byteStart++;

        this.zoneOrRank = bytes[byteStart] & 0xFF;
        byteStart++;
      }

      if ((origLength == IVDHeaderType.COMM_TSTMP.getLength())
          || (origLength == IVDHeaderType.COMM_RPTDATA_TSTMP.getLength())) {

        byte[] timestampBytes = ArrayUtils.subarray(bytes, byteStart, byteStart + 4);
        if (this.order == BytesOrder.LITTLE_ENDIAN) {
          BytesUtil.reverseBytes(timestampBytes);
        }
        long tempTimestamp = (Long) BytesUtil.toDecimal(timestampBytes, BytesSize.UINT32);
        this.timestamp = tempTimestamp * BytesUtil.MILLISECONDS;
        byteStart += 4;

        this.sequence = bytes[byteStart];
      }

    } catch (Exception ex) {
      log.error("Error in parse bytes {}", ex.getMessage());
    }
  }

  /**
   * Returns hexadecimal byte array representation of the message header.
   *
   * @return Bytes
   */
  public byte[] toHexBytes() {
    byte[] bytes = new byte[] {};

    try {

      byte byte1 = BytesUtil.convertToBytes(this.type.getId(), BytesSize.BYTE)[0];
      bytes = BytesUtil.appendByte(bytes, byte1);

      byte ackBit = (byte) (this.toByte(this.acknowledgement) << 7);
      byte serialNumBit = BytesUtil.convertToBytes(this.serialNum, BytesSize.BYTE)[0];
      byte byte2 = (byte) (ackBit | serialNumBit);
      bytes = BytesUtil.appendByte(bytes, byte2);

      byte[] bytes3And4 = BytesUtil.convertToBytes(this.mobileId, BytesSize.UINT16);
      if (this.order == BytesOrder.LITTLE_ENDIAN) {
        BytesUtil.reverseBytes(bytes3And4);
      }
      bytes = BytesUtil.joinBytes(bytes, bytes3And4);

      // IVD Report Data is from IVD only, not applicable to messages from backend
      if (this.type.isFromIVD() || (this.type == IVDMessageType.NONE)) {

        byte[] bytes5And6 = BytesUtil.convertToBytes(this.offsetLatitude, BytesSize.UINT16);
        if (this.order == BytesOrder.LITTLE_ENDIAN) {
          BytesUtil.reverseBytes(bytes5And6);
        }
        bytes = BytesUtil.joinBytes(bytes, bytes5And6);

        byte[] bytes7And8 = BytesUtil.convertToBytes(this.offsetLongitude, BytesSize.UINT16);
        if (this.order == BytesOrder.LITTLE_ENDIAN) {
          BytesUtil.reverseBytes(bytes7And8);
        }
        bytes = BytesUtil.joinBytes(bytes, bytes7And8);

        byte byte9 = BytesUtil.convertToBytes(this.speed, BytesSize.BYTE)[0];
        bytes = BytesUtil.appendByte(bytes, byte9);

        byte emergencyStatusField = (byte) (this.emergencyStatus << 6);
        byte gpxFixStatusBit = (byte) (this.toByte(this.gpsFixStatus) << 5);
        byte expresswayBit = (byte) (this.toByte(this.expressway) << 4);
        byte directionByte = BytesUtil.convertToBytes(this.direction, BytesSize.BYTE)[0];
        byte directionLower4Bits = (byte) (directionByte & 15);
        byte byte10 =
            (byte) (emergencyStatusField | gpxFixStatusBit | expresswayBit | directionLower4Bits);
        bytes = BytesUtil.appendByte(bytes, byte10);

        byte direction5thBit = (byte) ((directionByte & 16) << 3);
        byte taxiMeterBits =
            (byte)
                (((BytesUtil.convertToBytes(this.taxiMeterStatus, BytesSize.BYTE)[0]) << 5) & 96);
        byte suspensionBit = (byte) (this.toByte(this.suspensionStatus) << 4);
        byte ivdBits = (byte) (BytesUtil.convertToBytes(this.ivdStatus, BytesSize.BYTE)[0] & 15);
        byte byte11 = (byte) (direction5thBit | taxiMeterBits | suspensionBit | ivdBits);
        bytes = BytesUtil.appendByte(bytes, byte11);

        byte byte12 = BytesUtil.convertToBytes(this.zoneOrRank, BytesSize.BYTE)[0];
        bytes = BytesUtil.appendByte(bytes, byte12);
      }

      // Timestamp
      long tempTimestamp = this.timestamp / BytesUtil.MILLISECONDS;
      byte[] timestampBytes = BytesUtil.convertToBytes(tempTimestamp, BytesSize.UINT32);
      if (this.order == BytesOrder.LITTLE_ENDIAN) {
        BytesUtil.reverseBytes(timestampBytes);
      }
      bytes = BytesUtil.joinBytes(bytes, timestampBytes);

      // Sequence Number
      byte[] sequenceBytes = BytesUtil.convertToBytes(this.sequence, BytesSize.BYTE);
      bytes = BytesUtil.joinBytes(bytes, sequenceBytes);

    } catch (Exception ex) {
      log.error("Error in hex bytes header {}", ex.getMessage());
    }

    return bytes;
  }

  private byte toByte(boolean value) {
    return (value) ? (byte) 1 : (byte) 0;
  }
}
