package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.utils;

import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.BytesOrder;
import com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums.BytesSize;
import java.io.Serial;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Abstract class for generating or parsing Tag-Length-Value (TLV) patterned bytes array.
 * <!-- -->
 * Defaults to BytesOrder#LITTLE_ENDIAN
 */
@Slf4j
public abstract class IVDAbstractBytesHolder implements Serializable {

  protected static class TLVMapKey {

    private final int random;

    @Getter private IVDFieldTag tag;

    private final long timestamp;

    public TLVMapKey(IVDFieldTag tag) {
      this.tag = tag;
      this.timestamp = Calendar.getInstance().getTimeInMillis();
      SecureRandom secureRandom = new SecureRandom();
      this.random = secureRandom.nextInt(10000);
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof TLVMapKey other) {
        return (this.tag == other.tag)
            && (this.timestamp == other.timestamp)
            && (this.random == other.random);
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      return this.tag.hashCode() + Long.valueOf(this.timestamp).hashCode() + this.random;
    }
  }

  protected static final int LEN_BYTES_SIZE = 2;

  @Serial private static final long serialVersionUID = 1L;

  protected static final int TAG_BYTES_SIZE = 2;

  protected BytesOrder order;

  protected transient Map<TLVMapKey, byte[]> valuesMap;

  /**
   * Constructor for parsing TLV bytes array, specifying desired Bytes Order.
   *
   * @param bytes bytes
   * @param order order
   */
  protected IVDAbstractBytesHolder(byte[] bytes, BytesOrder order) {
    this.valuesMap = new LinkedHashMap<>();
    this.order = order;
    this.parseBytes(bytes);
  }

  protected IVDAbstractBytesHolder(BytesOrder order) {
    this.valuesMap = new LinkedHashMap<>();
    this.order = order;
  }

  /**
   * Extracts tag's Object value, specifying Bytes Order. Needs explicit parsing.
   *
   * @param tag tag
   * @param size size
   * @return object
   */
  public Object get(IVDFieldTag tag, BytesSize size) {
    return Optional.ofNullable(this.getArray(tag, size))
        .filter(array -> array.length > 0)
        .map(array -> array[0])
        .orElse(null);
  }

  /**
   * Extracts tag's Object value in array, specifying Bytes Order. Needs explicit parsing.
   *
   * @param tag tag
   * @param size size
   * @return object[]
   */
  public Object[] getArray(IVDFieldTag tag, BytesSize size) {
    Object[] objects = null;
    try {
      List<byte[]> bytesList = this.getValuesMapItem(tag);
      if (!bytesList.isEmpty()) {
        objects = new Object[bytesList.size()];
        for (int i = 0; i < bytesList.size(); i++) {
          byte[] bytes = bytesList.get(i);
          objects[i] = this.toDecimal(bytes, size);
        }
      }
    } catch (Exception ex) {
      log.error("Error in getArray ", ex);
    }
    return objects;
  }

  /**
   * Extracts tag's value. Returns {@link java.lang.Integer#MIN_VALUE} if not found.
   *
   * @param tag tag
   * @return int
   */
  public int getCoord(IVDFieldTag tag) {
    return this.getInt32(tag);
  }

  /**
   * Extracts tag's value. Returns {@link java.lang.Integer#MIN_VALUE} if not found.
   *
   * @param tag tag
   * @return int
   */
  public int getInt32(IVDFieldTag tag) {
    try {
      return this.getInt32Array(tag)[0];
    } catch (Exception e) {
      return Integer.MIN_VALUE;
    }
  }

  /**
   * Extracts tag's value in array. Returns null if not found.
   *
   * @param tag tag
   * @return int[]
   */
  public int[] getInt32Array(IVDFieldTag tag) {
    int[] result = null;
    try {
      Object[] objects = this.getArray(tag, BytesSize.INT32);
      result = new int[objects.length];
      for (int i = 0; i < objects.length; i++) {
        result[i] = (Integer) objects[i];
      }
    } catch (Exception ex) {
      log.error("Error in getInt32Array ", ex);
    }
    return result;
  }

  /**
   * Extract's tag's value. Returns null if not found.
   *
   * @param tag tag
   * @return IVDListItem
   */
  public IVDListItem getListItem(IVDFieldTag tag) {
    return Optional.ofNullable(this.getListItemArray(tag))
        .filter(array -> array.length > 0)
        .map(array -> array[0])
        .orElse(null);
  }

  /**
   * Extract's tag's value in array. Returns null if not found.
   *
   * @param tag tag
   * @return IVDListItem[]
   */
  public IVDListItem[] getListItemArray(IVDFieldTag tag) {
    return Optional.ofNullable(this.getValuesMapItem(tag))
        .map(
            bytesList ->
                bytesList.stream()
                    .map(bytes -> new IVDListItem(bytes, this.order))
                    .toArray(IVDListItem[]::new))
        .orElse(null);
  }

  /**
   * Extracts tag's value. Returns {@link java.lang.Integer#MIN_VALUE} if not found.
   *
   * @param tag tag
   * @return int
   */
  public int getMoney(IVDFieldTag tag) {
    return this.getInt32(tag);
  }

  /**
   * Extracts tag's value. Returns null if not found.
   *
   * @param tag tag
   * @return String
   */
  public String getText(IVDFieldTag tag) {

    return Optional.ofNullable(this.getTextArray(tag))
        .filter(array -> array.length > 0)
        .map(array -> array[0])
        .orElse(null);
  }

  /**
   * Extracts tag's value in array. Returns null if not found.
   *
   * @param tag tag
   * @return String[]
   */
  public String[] getTextArray(IVDFieldTag tag) {
    String[] result = null;
    try {
      Object[] objects = this.getArray(tag, BytesSize.TEXT);
      if (objects != null) {
        result = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
          result[i] = (String) objects[i];
        }
      }

    } catch (Exception ex) {
      log.error("Error in getTextArray ", ex);
    }
    return result;
  }

  private List<byte[]> getValuesMapItem(IVDFieldTag tag) {
    return this.valuesMap.keySet().stream()
        .filter(key -> key.getTag() == tag)
        .map(this.valuesMap::get)
        .toList();
  }

  protected void parseBytes(byte[] bytes) {
    try {
      int tagStartIdx = 0;
      int lenStartIdx = 2;
      int valStartIdx = 4;

      while (tagStartIdx < bytes.length) {
        byte[] tagBytes =
            ArrayUtils.subarray(
                bytes, tagStartIdx, tagStartIdx + IVDAbstractBytesHolder.TAG_BYTES_SIZE);
        byte[] lenBytes =
            ArrayUtils.subarray(
                bytes, lenStartIdx, lenStartIdx + IVDAbstractBytesHolder.LEN_BYTES_SIZE);

        short tagVal = (Short) this.toDecimal(tagBytes, BytesSize.INT16);
        short lenVal = (Short) this.toDecimal(lenBytes, BytesSize.INT16);

        IVDFieldTag tag = IVDFieldTag.getTag(tagVal);

        byte[] valBytes = ArrayUtils.subarray(bytes, valStartIdx, valStartIdx + lenVal);

        this.putValuesMapItem(tag, valBytes, false);

        tagStartIdx = tagStartIdx + tagBytes.length + lenBytes.length + valBytes.length;
        lenStartIdx = lenStartIdx + tagBytes.length + lenBytes.length + valBytes.length;
        valStartIdx = valStartIdx + tagBytes.length + lenBytes.length + valBytes.length;
      }

    } catch (Exception ex) {
      log.error("Error in parseBytes ", ex);
    }
  }

  private void putValuesMapItem(IVDFieldTag tag, byte[] valBytes, boolean override) {
    TLVMapKey mapKey = null;
    if (!override) {
      mapKey = new TLVMapKey(tag);

      while (this.valuesMap.containsKey(mapKey)) {
        mapKey = new TLVMapKey(tag);
      }
    } else {
      for (TLVMapKey key : this.valuesMap.keySet()) {
        if (key.getTag() == tag) {
          mapKey = key;
          break;
        }
      }
      if (mapKey == null) {
        mapKey = new TLVMapKey(tag);
      }
    }
    this.valuesMap.put(mapKey, valBytes);
  }

  private Object toDecimal(byte[] bytes, BytesSize size) {
    bytes = BytesUtil.copyBytes(bytes);
    if ((size != BytesSize.TEXT) && (this.order == BytesOrder.LITTLE_ENDIAN)) {
      BytesUtil.reverseBytes(bytes);
    }
    return BytesUtil.toDecimal(bytes, size);
  }

  public void put(IVDFieldTag tag, Object obj, BytesSize size) {
    this.put(tag, obj, size, true);
  }

  public void put(IVDFieldTag tag, Object obj, BytesSize size, boolean override) {
    byte[] bytes = this.convertToBytes(obj, size);
    bytes = BytesUtil.trimBytes(bytes, this.order);
    this.putValuesMapItem(tag, bytes, override);
  }

  private byte[] convertToBytes(Object obj, BytesSize size) {
    byte[] bytes = BytesUtil.convertToBytes(obj, size);
    if ((!(obj instanceof String)) && (this.order == BytesOrder.LITTLE_ENDIAN)) {
      BytesUtil.reverseBytes(bytes);
    }
    return bytes;
  }

  public void putBoolean(IVDFieldTag tag, boolean value) {
    this.putBoolean(tag, value, true);
  }

  public void putBoolean(IVDFieldTag tag, boolean value, boolean override) {
    this.put(tag, value, BytesSize.BOOLEAN, override);
  }

  public void putChar(IVDFieldTag tag, char value) {
    this.putChar(tag, value, true);
  }

  public void putChar(IVDFieldTag tag, char value, boolean override) {
    this.put(tag, value, BytesSize.CHAR, override);
  }

  public void putCoord(IVDFieldTag tag, int value) {
    this.putCoord(tag, value, true);
  }

  public void putCoord(IVDFieldTag tag, int value, boolean override) {
    this.putInt32(tag, value, override);
  }

  public void putDateTime(IVDFieldTag tag, Date value) {
    this.putDateTime(tag, value, true);
  }

  public void putDateTime(IVDFieldTag tag, Date value, boolean override) {
    this.put(tag, value.getTime() / BytesUtil.MILLISECONDS, BytesSize.DATETIME, override);
  }

  public void putDateTime(IVDFieldTag tag, long value) {
    this.putDateTime(tag, value, true);
  }

  public void putDateTime(IVDFieldTag tag, long value, boolean override) {
    this.put(tag, value / BytesUtil.MILLISECONDS, BytesSize.DATETIME, override);
  }

  public void putInt16(IVDFieldTag tag, short value) {
    this.putInt16(tag, value, true);
  }

  public void putInt16(IVDFieldTag tag, short value, boolean override) {
    this.put(tag, value, BytesSize.INT16, override);
  }

  public void putUInt16(IVDFieldTag tag, int value) {
    this.putUInt16(tag, value, true);
  }

  public void putUInt16(IVDFieldTag tag, int value, boolean override) {
    this.put(tag, value, BytesSize.UINT16, override);
  }

  public void putInt32(IVDFieldTag tag, int value) {
    this.putInt32(tag, value, true);
  }

  public void putInt32(IVDFieldTag tag, int value, boolean override) {
    this.put(tag, value, BytesSize.INT32, override);
  }

  public void putInt64(IVDFieldTag tag, long value) {
    this.putInt64(tag, value, true);
  }

  public void putInt64(IVDFieldTag tag, long value, boolean override) {
    this.put(tag, value, BytesSize.INT64, override);
  }

  public void putByte(IVDFieldTag tag, byte value) {
    this.putByte(tag, value, true);
  }

  public void putByte(IVDFieldTag tag, byte value, boolean override) {
    this.put(tag, value, BytesSize.BYTE, override);
  }

  public void putText(IVDFieldTag tag, String value) {
    this.putText(tag, value, true);
  }

  public void putText(IVDFieldTag tag, String value, boolean override) {
    this.put(tag, value, BytesSize.TEXT, override);
  }

  public void putMoney(IVDFieldTag tag, int value) {
    this.putMoney(tag, value, true);
  }

  public void putMoney(IVDFieldTag tag, int value, boolean override) {
    this.putInt32(tag, value, override);
  }

  public void putListItem(IVDFieldTag tag, IVDListItem item, boolean override) {
    this.putValuesMapItem(tag, item.toHexBytes(), override);
  }

  public void putTime(IVDFieldTag tag, int value) {
    this.putTime(tag, value, true);
  }

  public void putTime(IVDFieldTag tag, int value, boolean override) {
    this.putInt32(tag, value, override);
  }

  public byte[] toHexBytes() {
    byte[] bytes = null;
    for (Map.Entry<TLVMapKey, byte[]> entry : this.valuesMap.entrySet()) {
      IVDFieldTag tagKey = entry.getKey().getTag();
      byte[] tagBytes = this.convertToBytes(tagKey.getId(), BytesSize.INT16);
      byte[] valBytes = entry.getValue();
      byte[] lenBytes =
          this.convertToBytes(valBytes != null ? valBytes.length : 0, BytesSize.INT16);

      bytes = BytesUtil.joinBytes(bytes, tagBytes);
      bytes = BytesUtil.joinBytes(bytes, lenBytes);
      bytes = BytesUtil.joinBytes(bytes, valBytes);
    }
    return bytes;
  }
}
