package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.BytesSize;
import java.util.Date;
import java.util.Map;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class JobDispatchUtil {

  /**
   * Method to add each of the value to byte array from map
   *
   * @param values - Map with tag and value
   * @param message - message
   */
  public static void setMultipleItemsIntoIvdMessage(
      Map<IVDFieldTag, Object> values, IVDMessageContent message) {
    for (Map.Entry<IVDFieldTag, Object> entry : values.entrySet()) {
      putItemIntoIvdMessage(entry.getKey(), entry.getValue(), message);
    }
  }

  /**
   * Method to add data to message
   *
   * @param data data
   * @param tag Ivd tag
   * @param message message
   */
  public static void putItemIntoIvdMessage(
      IVDFieldTag tag, Object data, IVDMessageContent message) {

    /* Don't put the null obj to the message */
    if (data == null) return;

    /* Get BytesSize associated with this tag */
    BytesSize size = TlvTagsFactory.getTagByteSize(tag);

    if (size == null) return;

    switch (size) {
      case BOOLEAN -> putBooleanData(data, tag, message);
      case CHAR -> putCharData(data, tag, message);
      case BYTE -> putByteData(data, tag, message);
      case COORD, MONEY, SPEED, TIME, UINT16, INT32 -> putInt32Data(data, tag, message);
      case DATETIME -> putDateTimeData(data, tag, message);
      case INT16 -> putInt16Data(data, tag, message);
      case DISTANCE, TIMESTAMP, UINT32, INT64, UINT64 -> putInt64Data(data, tag, message);
      default -> putObjectData(data, size, tag, message);
    }
  }

  private void putBooleanData(Object data, IVDFieldTag tag, IVDMessageContent message) {
    boolean bValue = data instanceof Boolean b ? b : Boolean.parseBoolean(String.valueOf(data));
    message.putBoolean(tag, bValue);
  }

  private void putCharData(Object data, IVDFieldTag tag, IVDMessageContent message) {
    char cValue = data.toString().charAt(0);
    message.putChar(tag, cValue);
  }

  private void putByteData(Object data, IVDFieldTag tag, IVDMessageContent message) {
    byte binary = data instanceof Byte bytes ? bytes : Byte.parseByte(String.valueOf(data));
    message.putByte(tag, binary);
  }

  private void putInt32Data(Object data, IVDFieldTag tag, IVDMessageContent message) {
    int iValue = data instanceof Integer in ? in : Integer.parseInt(String.valueOf(data));
    message.putInt32(tag, iValue);
  }

  private void putDateTimeData(Object data, IVDFieldTag tag, IVDMessageContent message) {
    if (data instanceof Date dataVal) {
      message.putDateTime(tag, dataVal);
    } else if (data instanceof Long lng) {
      message.putDateTime(tag, lng);
    }
  }

  private void putInt16Data(Object data, IVDFieldTag tag, IVDMessageContent message) {
    short sValue = data instanceof Short s ? s : Short.parseShort(String.valueOf(data));
    message.putInt16(tag, sValue);
  }

  private void putInt64Data(Object data, IVDFieldTag tag, IVDMessageContent message) {
    long lValue = 0;
    if (data instanceof Long l) {
      lValue = l;
    } else if (data instanceof Double d) {
      lValue = d.longValue();
    } else {
      try {
        lValue = Long.parseLong(data.toString());
      } catch (NumberFormatException e) {
        log.error("Exception in putInt64Data ", e);
      }
    }
    message.putInt64(tag, lValue);
  }

  private void putObjectData(
      Object data, BytesSize size, IVDFieldTag tag, IVDMessageContent message) {
    Object objValue = parseObject(data, size);
    message.put(tag, objValue, size);
  }

  private static Object parseObject(Object data, BytesSize size) {
    Object objValue;
    objValue = BytesUtil.parse(String.valueOf(data), size);
    return objValue;
  }
}
