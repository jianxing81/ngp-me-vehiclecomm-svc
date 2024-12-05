package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.BytesOrder;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.BytesSize;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Date;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Utility class for bytes manipulations and conversions.
 * <!-- -->
 * Big Endian format is assumed.
 * <!-- -->
 * Reversal of bytes can be used when Little Endian format is desired.
 */
@Slf4j
@UtilityClass
public class BytesUtil {

  /***
   * Number of bits per byte.
   */
  public static final int BYTE = 8;

  /***
   * Milliseconds integral value.
   */
  public static final int MILLISECONDS = 1000;

  /**
   * Method to adjust bytes for tags
   *
   * @param bytes byte array
   * @param length length
   * @return byte array
   */
  public static byte[] adjustBytes(byte[] bytes, int length) {
    byte[] newBytes = new byte[length];
    try {
      for (int i = (bytes.length - 1), j = (length - 1); i >= 0; i--, j--) {
        newBytes[j] = bytes[i];
      }
    } catch (Exception ignored) {
      log.debug("Exception in adjustBytes");
    }
    return newBytes;
  }

  /**
   * Method to copy bytes
   *
   * @param bytes byte array
   * @return byte array
   */
  public static byte[] copyBytes(byte[] bytes) {
    return ArrayUtils.clone(bytes);
  }

  /***
   * Reverses the values in a byte array. Used to switch between Big and Little Endian pattern.
   *
   * @param bytes bytes
   */
  public static void reverseBytes(byte[] bytes) {
    ArrayUtils.reverse(bytes);
  }

  public static byte[] toBytes(String strHex) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    Arrays.stream(strHex.split("\\s+"))
        .map(
            hex -> {
              try {
                return (byte) Integer.parseInt(hex, 16);
              } catch (NumberFormatException e) {
                return (byte) 0x00;
              }
            })
        .forEach(outputStream::write);

    return outputStream.toByteArray();
  }

  /***
   * Returns the decimal representation of a hexadecimal byte array (defaults Big Endian).<!-- --> Casting is required.
   *
   * @param bytes bytes
   * @param size size
   * @return object
   */
  public static Object toDecimal(byte[] bytes, BytesSize size) {
    return BytesUtil.toDecimal(bytes, size, BytesOrder.BIG_ENDIAN);
  }

  /***
   * Returns the decimal representation of a hexadecimal byte array specifying bytes order.<!-- --> Casting is required.
   *
   * @param bytes bytes
   * @param size size
   * @param order order
   * @return object
   */
  public static Object toDecimal(byte[] bytes, BytesSize size, BytesOrder order) {
    bytes = BytesUtil.copyBytes(bytes);

    Object obj = null;

    if (order == BytesOrder.LITTLE_ENDIAN) {
      bytes = ArrayUtils.subarray(bytes, 0, bytes.length);
      BytesUtil.reverseBytes(bytes);
    }

    if (bytes != null) {
      ByteBuffer buffer = null;
      if (size == BytesSize.BOOLEAN) {
        buffer = ByteBuffer.wrap(BytesUtil.adjustBytes(bytes, Byte.SIZE / BytesUtil.BYTE));
        obj = buffer.get() == 1;
      } else if ((size == BytesSize.BYTE) || (size == BytesSize.SPEED)) {
        buffer = ByteBuffer.wrap(BytesUtil.adjustBytes(bytes, Byte.SIZE / BytesUtil.BYTE));
        obj = buffer.get();
      } else if ((size == BytesSize.CHAR) || (size == BytesSize.UCHAR)) {
        buffer = ByteBuffer.wrap(BytesUtil.adjustBytes(bytes, Character.SIZE / BytesUtil.BYTE));
        obj = buffer.getChar();
      } else if (size == BytesSize.INT16) {
        buffer = ByteBuffer.wrap(BytesUtil.adjustBytes(bytes, Short.SIZE / BytesUtil.BYTE));
        obj = buffer.getShort();
      } else if ((size == BytesSize.INT32)
          || (size == BytesSize.UINT16)
          || (size == BytesSize.MONEY)
          || (size == BytesSize.TIME)
          || (size == BytesSize.COORD)) {
        buffer = ByteBuffer.wrap(BytesUtil.adjustBytes(bytes, Integer.SIZE / BytesUtil.BYTE));
        obj = buffer.getInt();
      } else if ((size == BytesSize.INT64)
          || (size == BytesSize.UINT32)
          || (size == BytesSize.TIMESTAMP)
          || (size == BytesSize.DISTANCE)
          || (size == BytesSize.UINT64)
          || (size == BytesSize.DATETIME)) {
        buffer = ByteBuffer.wrap(BytesUtil.adjustBytes(bytes, Long.SIZE / BytesUtil.BYTE));
        obj = buffer.getLong();
      } else if (size == BytesSize.TEXT) {
        StringBuilder strBuffer = new StringBuilder();
        setText(bytes, strBuffer);
        obj = strBuffer.toString();
      } else if (size == BytesSize.UTEXT) {
        StringBuilder strBuffer = new StringBuilder();
        setUtext(bytes, strBuffer);
        obj = strBuffer.toString();
      }
    }

    return obj;
  }

  private static void setUtext(byte[] bytes, StringBuilder strBuffer) {
    ByteBuffer buffer;
    for (byte aByte : bytes) {
      byte[] charBytes = new byte[] {aByte};
      buffer = ByteBuffer.wrap(BytesUtil.adjustBytes(charBytes, Character.SIZE / BytesUtil.BYTE));
      strBuffer.append(buffer.getChar());
    }
  }

  private static void setText(byte[] bytes, StringBuilder strBuffer) {
    ByteBuffer buffer;
    for (byte aByte : bytes) {
      byte[] charBytes = new byte[] {0x00, aByte};
      buffer = ByteBuffer.wrap(BytesUtil.adjustBytes(charBytes, Character.SIZE / BytesUtil.BYTE));
      strBuffer.append(buffer.getChar());
    }
  }

  /**
   * Method to Convert to Bytes
   *
   * @param obj object
   * @param size byte size
   * @return byte array
   */
  public static byte[] convertToBytes(Object obj, BytesSize size) {
    byte[] bytes = null;
    if (obj instanceof String str) {
      bytes = new byte[str.length()];
      for (int i = 0; i < str.length(); i++) {
        bytes[i] = BytesUtil.baseBytesGenerator(str.charAt(i), BytesSize.CHAR)[0];
      }
    } else {
      bytes = BytesUtil.baseBytesGenerator(obj, size);
    }
    return bytes;
  }

  /**
   * Method to trim the byte array
   *
   * @param bytes bytes
   * @param order bytes order
   * @return byte array
   */
  public static byte[] trimBytes(byte[] bytes, BytesOrder order) {
    byte[] newBytes = BytesUtil.copyBytes(bytes);
    if (order == BytesOrder.LITTLE_ENDIAN) {
      BytesUtil.reverseBytes(newBytes);
    }
    newBytes = BytesUtil.trimBytes(newBytes);
    if (order == BytesOrder.LITTLE_ENDIAN) {
      BytesUtil.reverseBytes(newBytes);
    }

    return newBytes;
  }

  public static byte[] trimBytes(byte[] bytes) {
    byte[] newBytes = null;
    if ((bytes != null) && (bytes.length > 0)) {
      int idx = -1;
      for (int i = 0; i < bytes.length; i++) {
        if (bytes[i] != 0) {
          idx = i;
          break;
        }
      }

      idx = (idx == -1) ? (bytes.length - 1) : (idx);
      newBytes = ArrayUtils.subarray(bytes, idx, bytes.length);
    }

    return newBytes;
  }

  /**
   * Method baseBytesGenerator
   *
   * @param obj Object
   * @param size Byte Size
   * @return byte array
   */
  private static byte[] baseBytesGenerator(Object obj, BytesSize size) {
    byte[] bytes = new byte[0];
    ByteBuffer buffer = null;
    if (obj instanceof Character character) {
      buffer = ByteBuffer.allocate(Character.SIZE / BytesUtil.BYTE);
      buffer.putChar(character);
    } else if (obj instanceof Byte b) {
      buffer = ByteBuffer.allocate(Byte.SIZE / BytesUtil.BYTE);
      buffer.put(b);
    } else if (obj instanceof Short shorts) {
      buffer = ByteBuffer.allocate(Short.SIZE / BytesUtil.BYTE);
      buffer.putShort(shorts);
    } else if (obj instanceof Integer intgr) {
      buffer = ByteBuffer.allocate(Integer.SIZE / BytesUtil.BYTE);
      buffer.putInt(intgr);
    } else if (obj instanceof Long lng) {
      buffer = ByteBuffer.allocate(Long.SIZE / BytesUtil.BYTE);
      buffer.putLong(lng);
    } else if (obj instanceof Boolean boolVal) {
      byte value = Boolean.TRUE.equals((boolVal)) ? (byte) 1 : (byte) 0;
      buffer = ByteBuffer.allocate(Byte.SIZE / BytesUtil.BYTE);
      buffer.put(value);
    } else if (obj instanceof Date data) {
      long value = data.getTime() / BytesUtil.MILLISECONDS;
      buffer = ByteBuffer.allocate(Long.SIZE / BytesUtil.BYTE);
      buffer.putLong(value);
    }

    if (buffer != null) {
      bytes = buffer.array();
      size = (obj instanceof Character) ? (BytesSize.CHAR) : (size);
      if ((size != null) && (size != BytesSize.AUTO)) {
        bytes = BytesUtil.adjustBytes(bytes, size.getSize());
      }
    }

    return bytes;
  }

  /**
   * @param value Value
   * @param size size
   * @return object
   */
  public static Object parse(String value, BytesSize size) {
    try {
      if (size == BytesSize.BOOLEAN) {
        return Boolean.parseBoolean(value);
      } else if ((size == BytesSize.BYTE) || (size == BytesSize.SPEED)) {
        return Byte.parseByte(value);
      } else if (size == BytesSize.CHAR) {
        return value != null ? value.charAt(0) : null;
      } else if (size == BytesSize.DATETIME) {
        return new Date(Long.parseLong(value) * 1000);
      } else if (size == BytesSize.INT16) {
        return Short.parseShort(value);
      } else if ((size == BytesSize.INT32)
          || (size == BytesSize.COORD)
          || (size == BytesSize.MONEY)
          || (size == BytesSize.TIME)
          || (size == BytesSize.UINT16)) {
        return Integer.parseInt(value);
      } else if ((size == BytesSize.INT64)
          || (size == BytesSize.DISTANCE)
          || (size == BytesSize.TIMESTAMP)
          || (size == BytesSize.UINT32)
          || (size == BytesSize.UINT64)) {
        return Long.parseLong(value);
      } else if (size == BytesSize.TEXT) {
        return value;
      }
    } catch (Exception ex) {
      log.error("Exception in parse method");
    }

    return value;
  }

  public static byte[] joinBytes(byte[] holderBytes, byte[] newBytes) {
    holderBytes = ArrayUtils.addAll(holderBytes, newBytes);
    return holderBytes;
  }

  /**
   * Method to string
   *
   * @param bytes byte array
   * @return string
   */
  public static String toString(byte[] bytes) {
    StringBuilder buffer = new StringBuilder();
    for (int i = 0; ((bytes != null) && (i < bytes.length)); i++) {
      String hexStr = Integer.toHexString(bytes[i] & 0xFF).toUpperCase();
      buffer.append(hexStr.length() < 2 ? "0" : "").append(hexStr);
      buffer.append((i < (bytes.length - 1) ? " " : ""));
    }
    return (bytes != null) ? buffer.toString() : null;
  }

  /**
   * Method to append the byte
   *
   * @param bytes byte array
   * @param value value
   * @return
   */
  public static byte[] appendByte(byte[] bytes, byte value) {
    byte[] newBytes = new byte[] {value};
    return BytesUtil.joinBytes(bytes, newBytes);
  }
}
