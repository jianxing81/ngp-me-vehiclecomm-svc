package com.cdg.pmg.ngp.me.vehiclecomm.techframework.messaging.enums;

import lombok.Getter;

/***
 * Enum class for supported ICD data types and lengths.
 *
 *
 */
@Getter
public enum BytesSize {

  /***
   * Used enable auto-selection of probable data type with least number of bytes for storing a value.
   */
  AUTO(0),

  /***
   * 1-byte boolean data type with values either 1 or 0.
   */
  BOOLEAN(1),

  /***
   * 1-byte unsigned byte data type.<!-- --> Range is from 0 to 255.
   */
  BYTE(1),

  /***
   * 1-byte character data type.<!-- --> Note that Java character data type is 2-bytes in length.
   */
  CHAR(1),

  /***
   * 4-bytes WGS84 latitude or longitude coordinate value in decimal degree format.<!-- --> Coordinate value is formatted as a signed integer with right-most 7 digits representing decimal value.<!-- --> (Example, actual coordinate value x 10,000,000).
   */
  COORD(4),

  /***
   * 4-bytes date-time data type in Enoch format.<!-- --> It can be used as time-stamp for Date object in Java when multiplied with 1000 milliseconds.
   */
  DATETIME(4),

  /***
   * 4-byte unsigned integral distance value.<!-- --> Range is from 0 to 4,294,967,295 m.
   */
  DISTANCE(4),

  /***
   * 2-bytes singed integral value.<!-- --> Equivalent to short data type in Java.
   */
  INT16(2),

  /***
   * 4-bytes signed integral value.<!-- --> Equivalent to int data type in Java.
   */
  INT32(4),

  /***
   * 8-bytes signed integral value.<!-- --> Equivalent to long data type in Java.
   */
  INT64(8),

  /***
   * 4-byte unsigned integral monetary value.<!-- --> Right-most 3 digits represents cents.<!-- --> Range from 0 to 4,294,967,295 cents.
   */
  MONEY(4),

  /***
   * 1 unsigned byte for speed in km/hr.<!-- --> Rightmost 1 digit represents decimal value.<!-- --> Range is from 0 to 255 km/hr.
   */
  SPEED(1),

  /***
   * Null terminated 8-bit ASII text.<!-- --> One character is one byte length.
   */
  TEXT(0),

  /***
   * 4-bytes unsigned integral time value.<!-- --> Represents the number of seconds with no decimal value.
   */
  TIME(4),

  /***
   * 8-bytes integral value.<!-- --> Number of milliseconds elapsed since 1/1/1970 00:00:00 UTC.
   */
  TIMESTAMP(8),

  /***
   * 1-byte unicode character data type.<!-- --> Note that Java character data type is 2-bytes in length.
   */
  UCHAR(1),

  /**
   * 2-bytes unsigned integral value.
   * <!-- -->
   * When the value is extracted in Java, it'll be stored as int since Java doesn't support unsigned
   * values for short.
   */
  UINT16(2),

  /**
   * 4-bytes unsigned integral value.
   * <!-- -->
   * When the value is extracted in Java, it'll be stored as long since Java doesn't support
   * unsigned values for int.
   */
  UINT32(4),

  /**
   * 8-bytes unsigned integral value.
   * <!-- -->
   * Do not use, no implementation yet.
   */
  UINT64(8),

  /**
   * 2-bytes Unicode character.
   * <!-- -->
   */
  UTEXT(2);

  /***
   * .
   * Returns BytesSize for a given type name.
   *
   * @return number of bytes
   */
  public static BytesSize getSize(String type) {
    for (BytesSize size : BytesSize.values()) {
      if (size.toString().equalsIgnoreCase(type)) {
        return size;
      }
    }
    return null;
  }

  private final int size;

  BytesSize(int size) {
    this.size = size;
  }
}
